package com.project.quartz.util;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by user on 11:24 21/04/2025, 2025
 */
public class ObjectMapperUtil {

    private static final ObjectMapper INSTANCE = new ObjectMapper();

    private ObjectMapperUtil() {
        // Private constructor to prevent instantiation
    }

    public static ObjectMapper getInstance() {
        return INSTANCE;
    }

}
