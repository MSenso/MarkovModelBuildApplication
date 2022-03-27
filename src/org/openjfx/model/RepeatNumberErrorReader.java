package org.openjfx.model;

import java.text.MessageFormat;
import java.util.Map;

public class RepeatNumberErrorReader implements ErrorReader {
    private static final Map<Integer, String> ERROR_MAPPER = Map.of(
            -1, "количество повторов моделирования должно быть натуральным числом в диапазоне от 1 до 10000 включительно"
    );

    public RepeatNumberErrorReader() {
    }

    @Override
    public String getMessageByCode(String field, int errorCode, int... index) {
        return MessageFormat.format("Поле {0}: {1}", field, ERROR_MAPPER.get(errorCode));
    }
}
