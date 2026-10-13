package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.concurrent.*;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
public class ConcurrentApprovalTest extends IntegrationSupport {
 @Test @Transactional(propagation=Propagation.NOT_SUPPORTED)
 void concurrentReviewsProduceExactlyOneAccrual() throws Exception {
    var i=invoice("99");var executor=Executors.newFixedThreadPool(2);var gate=new CountDownLatch(1);
    try {
        Callable<Boolean> review=()->{gate.await();try{invoices.decide(i.id(),decision(),"approver",false,true);return true;}catch(DomainException conflict){return false;}};
        var first=executor.submit(review);var second=executor.submit(review);gate.countDown();
        assertThat(List.of(first.get(15,TimeUnit.SECONDS),second.get(15,TimeUnit.SECONDS))).containsExactlyInAnyOrder(true,false);
        assertThat(journalRepository.existsByReference("invoice-approved:"+i.id())).isTrue();
        assertThat(invoices.get(i.id()).status()).isEqualTo(InvoiceStatus.APPROVED);
    } finally {executor.shutdownNow();}
 }
}
