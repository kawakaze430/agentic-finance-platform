package io.github.kawakaze430.finance;

import java.util.List;
import org.springframework.data.domain.Page;
public record PageView<T>(List<T> items, int page, int size, long totalElements, int totalPages) {
    static <T> PageView<T> of(Page<T> p) { return new PageView<>(p.getContent(), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages()); }
}
