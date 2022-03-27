package org.openjfx.model;

import java.text.MessageFormat;
import java.util.Map;

public class AgeErrorReader implements ErrorReader {
    private static final Map<Integer, String> ERROR_MAPPER = Map.of(
            -1, "возраст должен быть натуральным числом"
    );

    public AgeErrorReader() {
    }

    @Override
    public String getMessageByCode(String field, int errorCode, int... index) {
        return MessageFormat.format("Поле {0}: {1}", field, ERROR_MAPPER.get(errorCode));
    }
}
