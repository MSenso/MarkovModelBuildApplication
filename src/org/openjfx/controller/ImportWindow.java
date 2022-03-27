package org.openjfx.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Paths;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ImportWindow implements Window {

    @FXML
    public TextField pathText;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;
    @FXML
    private BorderPane inputPane;

    private TextInputControl textArea;

    private String retrievedText = "";

    private Window parentWindow;

    public void setParentWindow(Window window) {
        this.parentWindow = window;
    }

    public void setTextArea(TextInputControl textArea) {
        this.textArea = textArea;
    }

    @FXML
    void handleCancelButtonAction() {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    private String openFile() {
        FileChooser fileChooser = new FileChooser();
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        fileChooser.setInitialDirectory(new File(currentPath));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT файлы", " *.txt"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV файлы", " *.csv"));
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) return file.getAbsolutePath();
        else return "";
    }

    @FXML
    void handleMouseClickAction(MouseEvent event) {
        String path = openFile();
        pathText.setText(path);
    }

    public ObservableList<String> importData(String path) {
        ObservableList<String> errors = FXCollections.observableArrayList();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String row;
            int index = 0;
            List<String> rows = new ArrayList<>();
            while ((row = reader.readLine()) != null && !row.isEmpty()) {
                rows.add(row);
                index++;
            }
            if (index == 0) errors.add("Файл не должен быть пустым");
            retrievedText = String.join("\n", rows);
            reader.close();
            return errors;
        } catch (IOException e) {
            errors.add("Некорректный файл");
            return errors;
        }
    }

    private void setBackgroundImage() {
        try {
            Image image = new Image(new FileInputStream("src/org/openjfx/resources/images/addBackground.png"));
            inputPane.setBackground(new Background(new BackgroundImage(image, null, null, null, null)));
        } catch (FileNotFoundException e) {
            showWindow(550, 400, "/org/openjfx/view/ErrorWindow.fxml",
                    "/org/openjfx/resources/images/ErrorIcon.png", "Ошибка",
                    List.of("Пожалуйста, проверьте наличие файла mainBackground в папке src/org/openjfx/resources/images"));
        }
    }

    @FXML
    public void initialize() {
        setBackgroundImage();
    }

    @FXML
    void handleOkButtonAction() {
        if (pathText.getText().isEmpty()) {
            showWindow(550, 400, "/org/openjfx/view/ErrorWindow.fxml",
                    "src/ErrorIcon.png", "Ошибка", List.of("Файл не выбран"));
        } else {
            List<String> errors = importData(pathText.getText());
            if (errors.size() == 0) {
                ((Stage) okButton.getScene().getWindow()).close();
                this.textArea.setText(retrievedText);
                // this.textArea.clear();
            } else showWindow(550, 400, "/org/openjfx/view/ErrorWindow.fxml",
                    "src/ErrorIcon.png", "Ошибка", errors);
        }
    }
}
