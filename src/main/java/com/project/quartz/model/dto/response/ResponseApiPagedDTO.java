package com.project.quartz.model.dto.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * Created by user on 3:31 21/04/2025, 2025
 */

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseApiPagedDTO {
    Integer code;
    String message;
    Object data;
    Integer page;
    Integer totalPages;
    Long totalElement;
}
