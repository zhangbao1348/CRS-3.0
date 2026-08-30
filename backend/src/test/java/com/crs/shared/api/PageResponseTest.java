package com.crs.shared.api;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PageResponseTest {

    @Test
    void shouldExposeOneBasedPageAndMappedContent() {
        PageImpl<Integer> page = new PageImpl<>(List.of(3, 4), PageRequest.of(1, 2), 5);

        PageResponse<String> response = PageResponse.from(page, value -> "item-" + value);

        assertEquals(List.of("item-3", "item-4"), response.data());
        assertEquals(5, response.totalElements());
        assertEquals(3, response.totalPages());
        assertEquals(2, response.currentPage());
        assertEquals(2, response.pageSize());
        assertFalse(response.first());
        assertFalse(response.last());
    }
}
