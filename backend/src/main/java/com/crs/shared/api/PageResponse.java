package com.crs.shared.api;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/**
 * 统一分页响应模型。
 *
 * <p>页码对外统一使用从 1 开始的 {@code currentPage}，避免前端感知 Spring Data
 * 从 0 开始的内部页码。</p>
 */
public record PageResponse<T>(
        List<T> data,
        long totalElements,
        int totalPages,
        int currentPage,
        int pageSize,
        boolean first,
        boolean last
) {
    /** 将 Spring Data 分页结果转换为稳定的 API 合同。 */
    public static <T> PageResponse<T> from(Page<T> page) {
        return from(page, Function.identity());
    }

    /** 转换分页内容的同时保留分页元数据。 */
    public static <S, T> PageResponse<T> from(Page<S> page, Function<S, T> mapper) {
        List<T> data = page.getContent().stream().map(mapper).toList();
        return new PageResponse<>(data, page.getTotalElements(), page.getTotalPages(),
                page.getNumber() + 1, page.getSize(), page.isFirst(), page.isLast());
    }
}
