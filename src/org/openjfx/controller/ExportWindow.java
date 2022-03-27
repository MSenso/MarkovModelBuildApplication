package org.openjfx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.openjfx.model.ModelWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class ExportWindow implements Window {
    private Window parentWindow;

    public void setParentWindow(Window window) {
        this.parentWindow = window;
    }

    @FXML
    private BorderPane inputPane;

    @FXML
    private TextField pathText;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    private String saveFile() {
        FileChooser fileChooser = new FileChooser();
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        fileChooser.setInitialDirectory(new File(currentPath));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT файлы", "*.txt"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV файлы", "*.csv"));
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) return file.getAbsolutePath();
        else return "";
    }

    @FXML
    void handleMouseClickAction() {
        String path = saveFile();
        pathText.setText(path);
    }

    @FXML
    void handleCancelButtonAction() {
        ((Stage) cancelButton.getScene().getWindow()).close();
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
    void handleOkButtonAction() throws IOException {
        var file = ((InteractiveWindow) parentWindow).getModelFile();
        new ModelWriter(file, pathText.getText());
        ((Stage) okButton.getScene().getWindow()).close();
    }

    @FXML
    public void initialize() {
        setBackgroundImage();
    }
}
