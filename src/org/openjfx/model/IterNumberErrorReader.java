package org.openjfx.model;

import java.text.MessageFormat;
import java.util.Map;

public class IterNumberErrorReader implements ErrorReader {
    private static final Map<Integer, String> ERROR_MAPPER = Map.of(
            -1, "количество итераций должно быть натуральным числом в диапазоне от 1 до 50 включительно"
    );

    public IterNumberErrorReader() {
    }

    @Override
    public String getMessageByCode(String field, int errorCode, int... index) {
        return MessageFormat.format("Поле {0}: {1}", field, ERROR_MAPPER.get(errorCode));
    }
}
