package org.openjfx.model;

import java.text.MessageFormat;
import java.util.Map;

public class MatrixErrorReader implements ErrorReader {
    private static final Map<Integer, String> ERROR_MAPPER = Map.of(
            -1, "формат введенной матрицы переходов некорректен. Пожалуйста, прочтите справку о формате ввода",
            -2, "номера итерации должны быть целыми неотрицательными числами",
            -3, "необходимо ввести значения для нулевой итерации",
            -4, "номера итераций должны быть различны",
            -5, "количество строк матрицы должно равняться количеству состояний",
            -6, "количество элементов в каждой строке матрицы должно равняться количеству состояний",
            -7, "элементы матрицы должны быть дробными числами в диапазоне от 0 до 1 включительно",
            -8, "сумма элементов в каждой строке матрицы должна равняться единице"
    );

    public MatrixErrorReader() {
    }

    @Override
    public String getMessageByCode(String field, int errorCode, int... index) {
        if (index.length == 1) {
            return MessageFormat.format("Поле {0}: {1}. Ошибка в строке матрицы №{2}", field,
                    ERROR_MAPPER.get(errorCode), index[0] + 1);
        } else if (index.length == 2) {
            return MessageFormat.format("Поле {0}: {1}. Ошибка в строке матрицы №{2} пары данных №{3}",
                    field, ERROR_MAPPER.get(errorCode), index[0] + 1, index[1] + 1);
        }
        return MessageFormat.format("Поле {0}: {1}", field, ERROR_MAPPER.get(errorCode));
    }
}
