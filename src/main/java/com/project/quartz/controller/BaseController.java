package com.project.quartz.controller;

import com.project.quartz.constant.StatusConstant;
import com.project.quartz.model.dto.response.ResponseApiDTO;
import com.project.quartz.model.dto.response.ResponseApiPagedDTO;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;

/**
 * Created by user on 3:33 21/04/2025, 2025
 */
public class BaseController {

    public ResponseApiDTO responseApi(boolean isSuccess, String message, Object data) {

        ResponseApiDTO dto = new ResponseApiDTO();
        dto.setSuccess(isSuccess);
        if (StringUtils.hasText(message)) {
            dto.setMessage(message);
        } else {
            dto.setMessage(StatusConstant.SUCCESS);
        }
        dto.setData(data);

        return dto;
    }

    public <T> ResponseApiPagedDTO responseApiPaged(Page<T> page) {

        ResponseApiPagedDTO dto = new ResponseApiPagedDTO();
        dto.setCode(200);
        dto.setMessage(StatusConstant.SUCCESS);
        dto.setData(page.getContent());
        dto.setPage(page.getNumber());
        dto.setTotalElement(page.getTotalElements());
        dto.setTotalPages(page.getTotalPages());

        return dto;
    }

}
