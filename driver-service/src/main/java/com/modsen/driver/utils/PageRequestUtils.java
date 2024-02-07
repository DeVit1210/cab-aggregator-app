package com.modsen.driver.utils;

import com.modsen.driver.exception.IncorrectPageNumberException;
import com.modsen.driver.exception.IncorrectPageSizeException;
import com.modsen.driver.exception.IncorrectSortFieldNameException;
import com.modsen.driver.model.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

public class PageRequestUtils {
    private static final Predicate<String> IS_SORT_FIELD_EXIST_PREDICATE = sortField ->
            Arrays.stream(Driver.class.getDeclaredFields())
                    .map(Field::getName)
                    .anyMatch(fieldName -> fieldName.equals(sortField));

    public static PageRequest makePageRequest(int pageNumber, int pageSize, String sortField) {
        if (pageSize < 1) {
            throw new IncorrectPageSizeException(pageSize);
        }
        if (pageNumber < 1) {
            throw new IncorrectPageNumberException(pageNumber);
        }
        if (!IS_SORT_FIELD_EXIST_PREDICATE.test(sortField)) {
            throw new IncorrectSortFieldNameException(sortField);
        }

        return Optional.of(sortField)
                .map(field -> PageRequest.of(pageNumber - 1, pageSize, Sort.Direction.ASC, sortField))
                .orElse(PageRequest.of(pageNumber - 1, pageSize));
    }

    public static void validatePageResponse(PageRequest pageRequest, Page<Driver> driverPage) {
        int actualPageQuantity = driverPage.getTotalPages();
        int requestedPageQuantity = pageRequest.getPageNumber() + 1;
        int pageSize = pageRequest.getPageSize();
        if (!driverPage.hasContent() && requestedPageQuantity > actualPageQuantity) {
            throw new IncorrectPageNumberException(pageSize, actualPageQuantity, requestedPageQuantity);
        }
    }
}