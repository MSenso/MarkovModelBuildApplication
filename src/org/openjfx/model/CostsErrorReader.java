package org.openjfx.model;

import java.text.MessageFormat;
import java.util.Map;

public class CostsErrorReader implements ErrorReader {
    private static final Map<Integer, String> ERROR_MAPPER = Map.of(
            -1, "формат введенных стоимостей некорректен. Пожалуйста, прочтите справку о формате ввода",
            -2, "номера итерации должны быть целыми неотрицательными числами",
            -3, "необходимо ввести значения для нулевой итерации",
            -4, "номера итераций должны быть различны",
            -5, "стоимости должны быть указаны строго для всех введенных состояний и терапий",
            -6, "стоимости должны быть целыми неотрицательньыми числами"
    );

    public CostsErrorReader() {
    }

    @Override
    public String getMessageByCode(String field, int errorCode, int... index) {
        if (index.length == 1) {
            return MessageFormat.format("Поле {0}: {1}. Ошибка в паре данных №{2}", field, ERROR_MAPPER.get(errorCode), index[0] + 1);
        } else return MessageFormat.format("Поле {0}: {1}", field, ERROR_MAPPER.get(errorCode));
    }
}
