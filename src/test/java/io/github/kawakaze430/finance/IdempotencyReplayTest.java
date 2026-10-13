package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
public class IdempotencyReplayTest extends IntegrationSupport {
 @Test void retriesReturnOriginalResponseWithoutMoreWrites(){var request=new Requests.Vendor("Replay vendor","replay@example.com","INR");String k=key();var first=commands.execute("analyst",k,"vendor:create",request,VendorView.class,()->vendors.create(request,"analyst"));long n=vendorRepository.count();var replay=commands.execute("analyst",k,"vendor:create",request,VendorView.class,()->{throw new AssertionError("Replay executed again");});assertThat(replay).isEqualTo(first);assertThat(vendorRepository.count()).isEqualTo(n);}
 @Test void scopeIsPerActor(){var r=new Requests.Vendor("Scoped vendor","scoped@example.com","INR");String k=key();var a=commands.execute("a",k,"vendor:create",r,VendorView.class,()->vendors.create(r,"a"));var b=commands.execute("b",k,"vendor:create",r,VendorView.class,()->vendors.create(r,"b"));assertThat(a.id()).isNotEqualTo(b.id());}
}
