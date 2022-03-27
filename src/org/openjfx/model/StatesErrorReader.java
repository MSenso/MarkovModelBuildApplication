package org.openjfx.model;

import java.text.MessageFormat;
import java.util.Map;

public final class StatesErrorReader implements ErrorReader {
    private static final Map<Integer, String> ERROR_MAPPER = Map.of(
            -1, "не допускается пустая строка",
            -2, "жолжно быть введено хотя бы два состояния",
            -3, "название состояния не может быть пустой строкой",
            -4, "названия состояний должны быть уникальными",
            -5, "названия терапий и состояний не должны совпадать"
    );

    public StatesErrorReader() {
    }

    @Override
    public String getMessageByCode(String field, int errorCode, int... index) {
        return MessageFormat.format("Поле {0}: {1}", field, ERROR_MAPPER.get(errorCode));
    }
}
