package org.openjfx.model;

import java.text.MessageFormat;
import java.util.Map;

public final class SexErrorReader implements ErrorReader {
    private static final Map<Integer, String> ERROR_MAPPER = Map.of(
            -1, "допустимое значение пола: 'мужской' или 'женский'"
    );

    public SexErrorReader() {}

    @Override
    public String getMessageByCode(String field, int errorCode, int... index)
    {
        return MessageFormat.format("Поле {0}: {1}", field, ERROR_MAPPER.get(errorCode));
    }
}
