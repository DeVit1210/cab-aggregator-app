package com.modsen.payment.utils;

import com.modsen.payment.dto.request.PageSettingsRequest;
import com.modsen.payment.exception.IncorrectPageNumberException;
import com.modsen.payment.exception.IncorrectPageSizeException;
import com.modsen.payment.exception.IncorrectSortFieldNameException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiPredicate;

public class PageRequestUtils {
    private static final BiPredicate<String, Class<?>> IS_SORT_FIELD_EXIST_PREDICATE = (sortField, entityClass) ->
            Arrays.stream(entityClass.getDeclaredFields())
                    .map(Field::getName)
                    .anyMatch(fieldName -> fieldName.equals(sortField));

    public static PageRequest pageRequestForEntity(PageSettingsRequest request, Class<?> entityClass) {
        return pageRequestForEntity(request.getNumber(), request.getSize(), request.getSortField(), entityClass);
    }

    public static PageRequest pageRequestForEntity(int pageNumber, int pageSize, String sortField, Class<?> entityClass) {
        if (pageSize < 1) {
            throw new IncorrectPageSizeException(pageSize);
        }
        if (pageNumber < 1) {
            throw new IncorrectPageNumberException(pageNumber);
        }
        if (!IS_SORT_FIELD_EXIST_PREDICATE.test(sortField, entityClass)) {
            throw new IncorrectSortFieldNameException(sortField, entityClass);
        }

        return Optional.of(sortField)
                .map(field -> PageRequest.of(pageNumber - 1, pageSize, Sort.Direction.ASC, sortField))
                .orElse(PageRequest.of(pageNumber - 1, pageSize));
    }

    public static <T> void validatePageResponse(PageRequest pageRequest, Page<T> passengerPage) {
        int actualPageQuantity = passengerPage.getTotalPages();
        int requestedPageQuantity = pageRequest.getPageNumber() + 1;
        int pageSize = pageRequest.getPageSize();
        if (!passengerPage.hasContent() && requestedPageQuantity > actualPageQuantity) {
            throw new IncorrectPageNumberException(pageSize, actualPageQuantity, requestedPageQuantity);
        }
    }
}