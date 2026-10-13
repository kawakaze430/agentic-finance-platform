package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
public class PaginationBoundsTest {
 @Test void rejectsExcessivePageSize(){assertThatThrownBy(()->PageLimits.of(0,101)).isInstanceOf(DomainException.class);}
 @Test void rejectsNegativePages(){assertThatThrownBy(()->PageLimits.of(-1,25)).isInstanceOf(DomainException.class);}
 @Test void usesStableSort(){assertThat(PageLimits.of(0,25).getSort().getOrderFor("id")).isNotNull();}
}
