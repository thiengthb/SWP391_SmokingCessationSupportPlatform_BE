package com.swpteam.smokingcessation.common;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Objects;

public record PageableRequest (

    @Min(value = 0, message = "PAGE_NO_MIN")
    Integer page,

    @Min(value = 1, message = "PAGE_SIZE_MIN")
    @Max(value = 100, message = "PAGE_SIZE_MAX")
    Integer size,

    Sort.Direction direction,
    
    String sortBy
) {
    public static Pageable getPageable(PageableRequest request) {
        int page = Objects.nonNull(request.page()) ? request.page() : 0;
        int size = Objects.nonNull(request.size()) ? request.size() : 20;
        Sort.Direction direction = Objects.nonNull(request.direction()) ? request.direction() : Sort.Direction.ASC;
        String sortBy = Objects.nonNull(request.sortBy()) ? request.sortBy() : "id";

        return PageRequest.of(page, size, direction, sortBy);
    }
}
