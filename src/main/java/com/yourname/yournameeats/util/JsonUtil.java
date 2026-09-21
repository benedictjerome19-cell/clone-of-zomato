package com.yourname.yournameeats.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

public class JsonUtil {

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class,
            (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                src == null ? null : new JsonPrimitive(src.format(FORMATTER)))
        .create();
}