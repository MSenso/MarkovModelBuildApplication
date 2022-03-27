package org.openjfx.model;

import java.text.MessageFormat;
import java.util.Map;

public class TherapyErrorReader implements ErrorReader {
        private static final Map<Integer, String> ERROR_MAPPER = Map.of(
                -1, "терапия для запуска модели должна быть выбрана"
        );

    public TherapyErrorReader() {
        }

        @Override
        public String getMessageByCode(String field, int errorCode, int... index) {
            return MessageFormat.format("Поле {0}: {1}", field, ERROR_MAPPER.get(errorCode));
        }
}
