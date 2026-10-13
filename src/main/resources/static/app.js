"use strict";
const $ = id => document.getElementById(id);
let credential = null, actor = null, selected = null, page = 0, totalPages = 0, currency = "INR", busy = false;
const pendingKeys = new Map();
const money = value => new Intl.NumberFormat(undefined,{style:"currency",currency,maximumFractionDigits:2}).format(value);
function node(tag, text, className) { const el=document.createElement(tag); if(text!=null)el.textContent=String(text);if(className)el.className=className;return el; }
function notice(text) { $("notice").textContent=text;$("notice").hidden=!text; }
async function api(path, body) {
  const headers={Authorization:`Basic ${credential}`};const options={headers};let signature;
  if(body!==undefined) {
    options.method="POST";options.body=JSON.stringify(body);headers["Content-Type"]="application/json";
    if(!path.startsWith("/api/agent/")) {
      signature=actor+path+options.body;
      if(!pendingKeys.has(signature))pendingKeys.set(signature,crypto.randomUUID());
      headers["Idempotency-Key"]=pendingKeys.get(signature);
    }
  }
  const response=await fetch(path,options);const result=await response.json().catch(()=>({}));
  if(signature && (response.ok || response.status<500))pendingKeys.delete(signature);
  if(!response.ok) {
    if(response.status===401)logout();
    throw new Error(result.detail || result.title || `Request failed (${response.status})`);
  }
  return result;
}
async function task(operation) {
  if(busy)return;busy=true;notice("");updateButtons();
  try { await operation(); } catch(error) {notice(error.message);} finally {busy=false;updateButtons();}
}
function logout() {credential=null;actor=null;selected=null;pendingKeys.clear();$("workspace").hidden=true;$("login-panel").hidden=false;$("logout").hidden=true;$("session-label").textContent="Signed out";$("password").value="";}
function updateButtons() {
  const canReview=actor==="approver" || actor==="admin";
  $("approve").disabled=busy || !canReview || !selected || selected.status!=="RECEIVED" || selected.createdBy===actor;
  $("reject").disabled=$("approve").disabled;
  $("run-agent").disabled=busy || !selected || !["analyst","admin"].includes(actor);
  $("new-invoice").disabled=busy || !["analyst","admin"].includes(actor);
  $("previous-page").disabled=busy || page===0;$("next-page").disabled=busy || page+1>=totalPages;
  for(const button of document.querySelectorAll(".settle-button"))button.disabled=busy || !canReview || !selected || selected.status!=="APPROVED";
}
async function refresh() {
  const [summary,invoices]=await Promise.all([api(`/api/dashboard?currency=${currency}`),api(`/api/invoices?currency=${currency}&page=${page}&size=10`)]);
  $("outstanding").textContent=money(summary.outstandingAmount);$("awaiting").textContent=summary.awaitingApproval;$("paid").textContent=summary.paid;$("unmatched").textContent=summary.unmatchedTransactions;
  $("mode-label").textContent=summary.agentMode==="demo"?"Deterministic demo — no model connected":"Ollama — self-hosted model";
  $("mode-description").textContent="Evidence is retrieved; approval and settlement remain human commands.";
  totalPages=invoices.totalPages;$("page-label").textContent=`${invoices.totalElements} invoices · Page ${page+1} of ${Math.max(1,totalPages)}`;
  $("invoice-rows").replaceChildren();
  for(const invoice of invoices.items) {
    const row=node("tr");if(selected?.id===invoice.id)row.className="selected-row";
    const ref=node("button",invoice.reference,"invoice-link");ref.onclick=()=>task(()=>selectInvoice(invoice.id));
    const cell=node("td");cell.append(ref,node("small",invoice.vendorName));row.append(cell,node("td",money(invoice.amount)),node("td",invoice.dueDate));
    const status=node("td");status.append(node("span",invoice.status,`status ${invoice.status}`));row.append(status);$("invoice-rows").append(row);
  }
  if(!invoices.items.length){const row=node("tr");const td=node("td","No invoices in this currency. Create one to start a review.");td.colSpan=4;row.append(td);$("invoice-rows").append(row);}
  if(selected)await selectInvoice(selected.id,false);
  updateButtons();
}
async function selectInvoice(id,clear=true) {
  selected=await api(`/api/invoices/${id}`);$("selection-empty").hidden=true;$("selection").hidden=false;
  $("selected-reference").textContent=selected.reference;$("selected-details").textContent=`${selected.vendorName} · ${money(selected.amount)} · ${selected.status}`;
  if(clear){$("agent-result").replaceChildren();$("tool-trace").replaceChildren();$("trace-panel").hidden=true;$("decision-reason").value="";}
  const candidates=await api(`/api/reconciliation/candidates?invoiceId=${id}`);$("candidates").replaceChildren();
  for(const candidate of candidates){
    const card=node("div",null,"match-card");card.append(node("strong",`${candidate.score}% evidence match · ${money(candidate.transaction.amount)}`),node("small",candidate.transaction.externalId),node("small",candidate.reasons.join(" · ")));
    if(candidate.score===100){const settle=node("button","Record settlement","secondary settle-button");settle.onclick=()=>task(async()=>{await api("/api/reconciliation/matches",{invoiceId:selected.id,bankTransactionId:candidate.transaction.id});await refresh();notice("Settlement recorded. No payment was sent.");});card.append(settle);}
    $("candidates").append(card);
  }
  if(!candidates.length)$("candidates").append(node("p","No open bank candidates for this invoice.","caption"));
  for(const row of $("invoice-rows").children)row.classList.toggle("selected-row",row.querySelector("button")?.textContent===selected.reference);
  updateButtons();
}
$("login-form").addEventListener("submit",event=>{event.preventDefault();task(async()=>{
  actor=$("username").value.trim();credential=btoa(unescape(encodeURIComponent(`${actor}:${$("password").value}`)));
  await refresh();$("password").value="";$("login-panel").hidden=true;$("workspace").hidden=false;$("logout").hidden=false;$("session-label").textContent=`${actor} · authenticated`;
});});
$("logout").onclick=logout;
$("currency").onchange=()=>task(async()=>{currency=$("currency").value;page=0;selected=null;$("selection").hidden=true;$("selection-empty").hidden=false;await refresh();const active=document.querySelector(".nav.active")?.dataset.tab;if(active!=="invoices")await loadTab(active);});
$("previous-page").onclick=()=>task(async()=>{page--;await refresh();});$("next-page").onclick=()=>task(async()=>{page++;await refresh();});
$("run-agent").onclick=()=>task(async()=>{
  $("agent-result").replaceChildren(node("p","Gathering invoice, policy, and bank evidence…"));
  const result=await api("/api/agent/runs",{invoiceId:selected.id,question:$("question").value});$("agent-result").replaceChildren();
  if(result.status==="FAILED")$("agent-result").append(node("p",`Review failed: ${result.errorCode}. No financial state changed.`));
  else {
    const r=result.recommendation;$("agent-result").append(node("span",`${r.risk} RISK`,`status ${r.risk}`),node("p",r.summary));
    const actions=node("ul");for(const action of r.actions)actions.append(node("li",action.toLowerCase().replaceAll("_"," ")));$("agent-result").append(actions,node("div",`Policy evidence: ${r.policyCitations.join(" · ")}`,"citations"));
  }
  $("trace-panel").hidden=false;$("tool-trace").replaceChildren();
  for(const trace of result.trace)$("tool-trace").append(node("h3",`${trace.step}. ${trace.tool}`),node("pre",JSON.stringify(trace.result,null,2)));
});
for(const action of ["approve","reject"])$(action).onclick=()=>task(async()=>{
  const reason=$("decision-reason").value.trim();if(reason.length<8)throw new Error("Add a review reason of at least eight characters.");
  await api(`/api/invoices/${selected.id}/${action}`,{reason});await refresh();notice(`Human decision recorded: ${action}.`);
});
$("new-invoice").onclick=()=>task(async()=>{
  const result=await api("/api/vendors?size=100");$("vendor-id").replaceChildren();
  for(const vendor of result.items.filter(v=>v.currency===currency)){const option=node("option",vendor.name);option.value=vendor.id;$("vendor-id").append(option);}
  if(!$("vendor-id").options.length)throw new Error("Create a vendor in this currency through the API first.");
  $("invoice-due").value=new Date(Date.now()+7*86400000).toISOString().slice(0,10);$("invoice-form").hidden=false;
});
$("cancel-invoice").onclick=()=>$("invoice-form").hidden=true;
$("invoice-form").onsubmit=event=>{event.preventDefault();task(async()=>{
  const invoice=await api("/api/invoices",{vendorId:$("vendor-id").value,reference:$("invoice-reference").value,amount:$("invoice-amount").value,currency,dueDate:$("invoice-due").value});
  $("invoice-form").reset();$("invoice-form").hidden=true;page=0;await refresh();await selectInvoice(invoice.id);notice("Invoice created. A different account must review it.");
});};
async function loadTab(tab){
  if(tab==="ledger") {
    const [accounts,journals]=await Promise.all([api(`/api/ledger/accounts?currency=${currency}`),api(`/api/ledger/journals?currency=${currency}`)]);$("accounts").replaceChildren();
    for(const account of accounts){const card=node("article",null,"account-card");card.append(node("span",account.code.replaceAll("_"," ")),node("strong",money(account.balance)));$("accounts").append(card);}
    $("journal-rows").replaceChildren();for(const journal of journals.items){const row=node("tr");row.append(node("td",journal.reference),node("td",journal.description),node("td",journal.lines.map(line=>`${line.account}: Dr ${money(line.debit)} / Cr ${money(line.credit)}`).join("; ")));$("journal-rows").append(row);}
  }
  if(tab==="audit"){
    const events=await api("/api/audit?size=50");$("audit-rows").replaceChildren();for(const event of events.items){const row=node("tr");row.append(node("td",event.action.replaceAll("_"," ")),node("td",event.actor),node("td",new Date(event.occurredAt).toLocaleString()),node("td",event.resourceId.slice(0,8)));$("audit-rows").append(row);}
  }
}
for(const button of document.querySelectorAll(".nav"))button.onclick=()=>task(async()=>{
  if(!credential)throw new Error("Sign in to open the workspace.");const tab=button.dataset.tab;await loadTab(tab);
  for(const el of document.querySelectorAll(".tab"))el.hidden=el.id!==`tab-${tab}`;for(const nav of document.querySelectorAll(".nav"))nav.classList.toggle("active",nav===button);
});
