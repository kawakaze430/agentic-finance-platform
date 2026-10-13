package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
public class IdempotencyConflictTest extends IntegrationSupport {
 @Test void changedPayloadCannotReuseKey(){String k=key();var r=new Requests.Vendor("First","first@example.com","INR");commands.execute("a",k,"vendor:create",r,VendorView.class,()->vendors.create(r,"a"));var other=new Requests.Vendor("Other","other@example.com","INR");assertThatThrownBy(()->commands.execute("a",k,"vendor:create",other,VendorView.class,()->vendors.create(other,"a"))).isInstanceOf(DomainException.class).hasMessageContaining("different command");}
 @Test void changedCommandCannotReuseKey(){String k=key();var r=new Requests.Vendor("First","first@example.com","INR");commands.execute("a",k,"vendor:create",r,VendorView.class,()->vendors.create(r,"a"));assertThatThrownBy(()->commands.execute("a",k,"vendor:update",r,VendorView.class,()->vendors.create(r,"a"))).isInstanceOf(DomainException.class);}
}
