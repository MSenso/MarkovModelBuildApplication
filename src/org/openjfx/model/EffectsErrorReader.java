package org.openjfx.model;

import java.text.MessageFormat;
import java.util.Map;

public class EffectsErrorReader implements ErrorReader {
    private static final Map<Integer, String> ERROR_MAPPER = Map.of(
            -1, "формат введенных эффективностей некорректен. Пожалуйста, прочтите справку о формате ввода",
            -2, "номера итерации должны быть целыми неотрицательными числами",
            -3, "необходимо ввести значения для нулевой итерации",
            -4, "номера итераций должны быть различны",
            -5, "эффективности должны быть указаны строго для всех введенных терапий",
            -6, "эффективность каждой терапии должна быть указана для каждого введенного состояния",
            -7, "эффективности должны быть дробными числами в диапазоне от 0 до 1 включительно"
    );

    public EffectsErrorReader() {
    }

    @Override
    public String getMessageByCode(String field, int errorCode, int... index) {
        if (index.length == 1) {
            return MessageFormat.format("Поле {0}: {1}. Ошибка в паре данных №{2}", field, ERROR_MAPPER.get(errorCode), index[0] + 1);
        } else return MessageFormat.format("Поле {0}: {1}", field, ERROR_MAPPER.get(errorCode));
    }
}
