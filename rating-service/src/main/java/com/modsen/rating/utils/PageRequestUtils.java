package com.modsen.rating.utils;

import com.modsen.rating.dto.request.PageSettingRequest;
import com.modsen.rating.exception.IncorrectPageNumberException;
import com.modsen.rating.exception.IncorrectPageSizeException;
import com.modsen.rating.exception.IncorrectSortFieldNameException;
import com.modsen.rating.model.Rating;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

@UtilityClass
public class PageRequestUtils {
    private static final Predicate<String> IS_SORT_FIELD_EXIST_PREDICATE = sortField ->
            Arrays.stream(Rating.class.getDeclaredFields())
                    .map(Field::getName)
                    .anyMatch(fieldName -> fieldName.equals(sortField));

    public static PageRequest makePageRequest(PageSettingRequest request) {
        return makePageRequest(request.getNumber(), request.getSize(), request.getSortField());
    }

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

    public static void validatePageResponse(PageRequest pageRequest, Page<Rating> ratingPage) {
        int actualPageQuantity = ratingPage.getTotalPages();
        int requestedPageQuantity = pageRequest.getPageNumber() + 1;
        int pageSize = pageRequest.getPageSize();
        if (!ratingPage.hasContent() && requestedPageQuantity > actualPageQuantity) {
            throw new IncorrectPageNumberException(pageSize, actualPageQuantity, requestedPageQuantity);
        }
    }
}