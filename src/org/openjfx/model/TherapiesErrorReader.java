package org.openjfx.model;

import java.text.MessageFormat;
import java.util.Map;

public final class TherapiesErrorReader implements ErrorReader {
    private static final Map<Integer, String> ERROR_MAPPER = Map.of(
            -1, "не допускается пустая строка",
            -2, "название терапии не может быть пустой строкой",
            -3, "названия терапий должны быть уникальными",
            -4, "названия терапий и состояний не должны совпадать"
    );

    public TherapiesErrorReader() {
    }

    @Override
    public String getMessageByCode(String field, int errorCode, int... index) {
        return MessageFormat.format("Поле {0}: {1}", field, ERROR_MAPPER.get(errorCode));
    }
}
