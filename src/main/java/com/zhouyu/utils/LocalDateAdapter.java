package com.zhouyu.utils;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author shanweidi
 * @date 2026-03-17 13:45
 **/
public class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
    @Override
    public JsonElement serialize(LocalDate localDate, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(formatter.format(localDate));
    }

    @Override
    public LocalDate deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return LocalDate.parse(jsonElement.getAsString(), formatter);
    }
}
