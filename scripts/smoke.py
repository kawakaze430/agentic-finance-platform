#!/usr/bin/env python3
"""Run against the seeded local demo; records a synthetic invoice settlement."""
import base64,json,os,uuid,urllib.request
base=os.environ.get('FINANCE_BASE_URL','http://localhost:8080')
def call(path,body=None,role='analyst'):
    password=os.environ.get('FINANCE_'+role.upper()+'_PASSWORD','demo-'+role+'-password')
    headers={'Authorization':'Basic '+base64.b64encode((role+':'+password).encode()).decode()}
    if body is not None:headers.update({'Content-Type':'application/json','Idempotency-Key':str(uuid.uuid4())})
    request=urllib.request.Request(base+path,data=None if body is None else json.dumps(body).encode(),headers=headers)
    with urllib.request.urlopen(request,timeout=50) as response:return json.load(response)
invoices=call('/api/invoices?size=100')['items']
invoice=next(i for i in invoices if i['reference']=='ACME-2026-1001')
result=call('/api/agent/runs',{'invoiceId':invoice['id'],'question':'Review bank evidence and approval limits'})
assert result['status']=='COMPLETED' and len(result['trace'])>=2
if invoice['status']=='RECEIVED':call('/api/invoices/'+invoice['id']+'/approve',{'reason':'Synthetic smoke test reviewed exact invoice evidence'},'approver')
invoice=call('/api/invoices/'+invoice['id'])
if invoice['status']=='APPROVED':
    candidate=next(c for c in call('/api/reconciliation/candidates?invoiceId='+invoice['id']) if c['score']==100)
    call('/api/reconciliation/matches',{'invoiceId':invoice['id'],'bankTransactionId':candidate['transaction']['id']},'approver')
assert call('/api/invoices/'+invoice['id'])['status']=='PAID'
from decimal import Decimal
assert sum(Decimal(a['balance']) for a in call('/api/ledger/accounts'))==0
print('PASS: agent evidence, human approval, exact settlement, and balanced ledger')
