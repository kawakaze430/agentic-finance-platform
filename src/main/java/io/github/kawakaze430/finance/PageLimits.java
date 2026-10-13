package io.github.kawakaze430.finance;

import org.springframework.data.domain.PageRequest;
public final class PageLimits {
    private PageLimits() {}
    static PageRequest of(int page, int size) {
        if (page < 0 || size < 1 || size > 100) throw DomainException.invalid("Page must be nonnegative and size must be between 1 and 100");
        return PageRequest.of(page, size, org.springframework.data.domain.Sort.by("createdAt").descending().and(org.springframework.data.domain.Sort.by("id")));
    }
}
