package org.openjfx.model;

import java.io.FileWriter;
import java.io.IOException;

public class ModelWriter {
    private String writeField(String name, String value) {
        return name + ": " + (value == null ? "" : value) + System.lineSeparator();
    }

    public ModelWriter(ModelFile file, String path) throws IOException {
        FileWriter writer = new FileWriter(path);
        writer.append(writeField("Состояния", file.getStates()));
        writer.append(writeField("Терапии", file.getTherapies()));
        writer.append(writeField("Стоимости", file.getCosts()));
        writer.append(writeField("Эффективность терапий", file.getTherapiesEffect()));
        writer.append(writeField("Матрица переходов", file.getTransMatrix()));
        writer.append(writeField("Возраст", file.getAge()));
        writer.append(writeField("Пол", file.getSex()));
        writer.append(writeField("Условия", String.join(System.lineSeparator(), file.getConditions())));
        writer.append(writeField("Клинический процесс", file.getTherapy()));
        writer.append(writeField("Количество итераций", file.getIterationsNumber()));
        writer.append(writeField("Количество повторов моделирования", file.getRepeatsNumber()));
        writer.append(writeField("Затраты на пациента (руб)", file.getCost()));
        writer.close();
    }
}
