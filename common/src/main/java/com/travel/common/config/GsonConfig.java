package com.travel.common.config;

import com.google.gson.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Gson的LocalDateTime和String转化
 * @author jianping5
 * @createDate 17/3/2023 下午 10:19
 */
@Configuration
public class GsonConfig {

    /**
     * 序列化
     */
    final static JsonSerializer<LocalDateTime> jsonSerializerDateTime = (localDateTime, type, jsonSerializationContext)
            -> new JsonPrimitive(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    final static JsonSerializer<LocalDate> jsonSerializerDate = (localDate, type, jsonSerializationContext)
            -> new JsonPrimitive(localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

    /**
     * 反序列化
     */
    final static JsonDeserializer<LocalDateTime> jsonDeserializerDateTime = (jsonElement, type, jsonDeserializationContext)
            -> LocalDateTime.parse(jsonElement.getAsJsonPrimitive().getAsString(),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    final static JsonDeserializer<LocalDate> jsonDeserializerDate = (jsonElement, type, jsonDeserializationContext)
            -> LocalDate.parse(jsonElement.getAsJsonPrimitive().getAsString(),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, jsonSerializerDateTime)
                .registerTypeAdapter(LocalDate.class, jsonSerializerDate)
                .registerTypeAdapter(LocalDateTime.class, jsonDeserializerDateTime)
                .registerTypeAdapter(LocalDate.class, jsonDeserializerDate)
                .create();
    }
}
