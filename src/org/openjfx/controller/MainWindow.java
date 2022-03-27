package org.openjfx.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.openjfx.model.IterNumberErrorReader;
import org.openjfx.model.Model;
import org.openjfx.model.ModelFile;
import org.openjfx.model.TherapyErrorReader;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class MainWindow implements InteractiveWindow {

    private ModelFile file = new ModelFile();

    public ModelFile getModelFile() {
        return file;
    }

    public void setModelFile(ModelFile file) {
        this.file = file;
    }

    private Window parentWindow;

    public void setParentWindow(Window window) {
        this.parentWindow = window;
    }

    @FXML
    private ImageView imageView;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Label costLabel;

    @FXML
    private MenuItem exportMenu;

    @FXML
    private ComboBox<String> therapyComboBox;

    @FXML
    private TextField iterField;

    @FXML
    private TextField repeatField;

    @FXML
    private Button runModelButton;

    @FXML
    private BorderPane inputPane;

    private Model model;

    public Model getModel() {
        return this.model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void setRunModelButtonAbility(boolean isDisable) {
        runModelButton.setDisable(isDisable);
    }

    public void setTherapyComboBox(List<String> therapies) {
        therapyComboBox.getItems().removeAll(therapyComboBox.getItems());
        therapyComboBox.getItems().addAll(therapies);
    }

    private static final String defaultCostLabelText = "Затраты на пациента (руб):";


    private boolean[][] transMatr(double[][] matr) {
        boolean[][] hasLinkMatr = new boolean[matr.length][matr[0].length];
        for (int i = 0; i < matr.length; i++) {
            for (int j = 0; j < matr[0].length; j++) {
                hasLinkMatr[i][j] = matr[i][j] != 0;
            }
        }
        return hasLinkMatr;
    }

    public void showScrollPane() {
        scrollPane.setVisible(true);
    }

    public void drawStates(Group root, List<String> names) {
        int size = names.size();
        List<Circle> circles = new ArrayList<>();
        List<Text> namesToShow = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            var circle = new Circle(120, 70 + 80 * i, 20);
            var name = new Text(170, 70 + 80 * i, names.get(i));
            circles.add(circle);
            namesToShow.add(name);
        }
        root.getChildren().addAll(circles);
        root.getChildren().addAll(namesToShow);
    }

    public void drawUpArrow(Group root, double lineX, double lineY) {
        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(lineX + 3, lineY + 20,
                lineX - 7, lineY + 15,
                lineX + 3, lineY + 10);
        root.getChildren().add(polygon);
    }

    public void drawDownArrow(Group root, double lineX, double lineY) {
        var polygon = new Polygon();
        polygon.getPoints().addAll(lineX, lineY,
                lineX - 10, lineY - 5,
                lineX, lineY - 10);
        root.getChildren().add(polygon);
    }

    public void drawProb(Group root, double value, double x, double y) {
        var prob = new Text(x, y, String.valueOf(value));
        prob.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        root.getChildren().add(prob);
    }

    public void drawLinks(Group root, double[][] matr) {
        Path path = new Path();
        var boolMatr = transMatr(matr);
        for (int i = 0; i < matr.length; i++) {
            for (int j = 0; j < matr[0].length; j++) {
                if (boolMatr[i][j]) {
                    if (i != j) {
                        int stateDiff = Math.abs(i - j);
                        var moveTo = new MoveTo(100, 70 + 80 * i);
                        var curX = moveTo.getX();
                        var curY = moveTo.getY();
                        path.getElements().add(moveTo);
                        if (j > i || !boolMatr[j][i]) {
                            var curve = new CubicCurveTo(curX + 50, curY - 20 - 2 * stateDiff,
                                    curX - 50 * stateDiff, curY + 30 * stateDiff, curX, curY + 82 * stateDiff);
                            path.getElements().add(curve);
                        }
                        if (i < j) {
                            drawDownArrow(root, curX, curY + 82 * stateDiff);
                            drawProb(root, matr[i][j], curX - 5 - 18 * stateDiff, curY + 70 * stateDiff);

                        } else {
                            drawUpArrow(root, curX, curY - 82 * stateDiff);
                            drawProb(root, matr[i][j], curX - 3 * stateDiff, curY - 50 * stateDiff);
                        }
                    } else {
                        var moveTo = new MoveTo(120, 65 + 80 * i);
                        var curX = moveTo.getX();
                        var curY = moveTo.getY();
                        path.getElements().add(moveTo);
                        var curve = new CubicCurveTo(curX + 20, curY - 40,
                                curX - 20, curY - 50, curX + 2, curY - 3);
                        path.getElements().add(curve);
                        drawDownArrow(root, curX, curY - 13);
                        drawProb(root, matr[i][j], curX + 15, curY - 20);
                    }
                }
            }
        }
        root.getChildren().addAll(path);
    }

    private void showResult(List<String> outputs) {
        String cost = outputs.get(0);
        costLabel.setText(defaultCostLabelText + " " + cost);
        costLabel.setVisible(true);
        file.setCost(cost);
    }

    private String therapyPropertyMapper(String input) {
        var lowerInput = input.toLowerCase();
        final var mapper = new HashMap<String, String>();
        mapper.put("контроль", "Control");
        if (mapper.get(lowerInput) == null) {
            return input;
        }
        return mapper.get(lowerInput);
    }


    private void makeForecast() {
        var matrix = model.getTransMatrix();
        var sex = model.getSex();
        var age = model.getAge();
        var iterationsNumber = iterField.getText();
        var repeatNumber = repeatField.getText();
        var therapyName = therapyPropertyMapper(String.valueOf(therapyComboBox.getSelectionModel().getSelectedItem()));
        var conditions = String.join("\n", model.getConditions());
        ProcessBuilder pb = new ProcessBuilder("python3",
                "src\\org\\openjfx\\scripts\\InputToModel.py",
                age, matrix, sex, iterationsNumber,
                repeatNumber, therapyName, conditions);
        try {
            Process p = pb.start();
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));
            List<String> outputs = new ArrayList<>();
            String output = "";
            while (output != null) {
                output = stdInput.readLine();
                System.out.println(output);
                outputs.add(output);
            }
            showResult(outputs);
        } catch (IOException e) {
            showWindow(550, 400, "/org/openjfx/view/ErrorWindow.fxml",
                    "/org/openjfx/resources/images/ErrorIcon.png", "Ошибка",
                    List.of("Пожалуйста, проверьте наличие файла InputToModel.py в папке src/org/openjfx/scripts"));
        }
    }

    private WritableImage scale(Image source, double targetWidth, double targetHeight) {
        ImageView imageView = new ImageView(source);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(targetWidth);
        imageView.setFitHeight(targetHeight);
        return imageView.snapshot(null, null);
    }

    public void drawModel() {
        List<String> namesArr = model.getStatesList();
        double[][] matr = model.getNumericTransMatrix();
        Group root = new Group();
        drawStates(root, namesArr);
        drawLinks(root, matr);
        WritableImage image = root.snapshot(new SnapshotParameters(), null);
        image = scale(image, scrollPane.getPrefWidth() - 30, image.getHeight() * (scrollPane.getPrefWidth() - 30) / image.getWidth());
        imageView.setFitWidth(image.getWidth());
        imageView.setFitHeight(image.getHeight());
        imageView.setImage(image);
        scrollPane.setMaxHeight(image.getHeight());
    }

    private boolean tryParseToInt(String val) {
        try {
            Integer.parseInt(val);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private Optional<String> checkIterField() {
        var text = iterField.getText();
        if (tryParseToInt(text)) {
            var number = Integer.parseInt(text);
            if (number < 0 || number > 50)
                return Optional.of(new IterNumberErrorReader().getMessageByCode("количества итераций", -1));
            return Optional.empty();
        }
        return Optional.of(new IterNumberErrorReader().getMessageByCode("количества итераций", -1));
    }

    private Optional<String> checkRepeatField() {
        var text = repeatField.getText();
        if (tryParseToInt(text)) {
            var number = Integer.parseInt(text);
            if (number < 0 || number > 10000)
                return Optional.of(new IterNumberErrorReader().getMessageByCode("количества итераций", -1));
            return Optional.empty();
        }
        return Optional.of(new IterNumberErrorReader().getMessageByCode("количества итераций", -1));
    }

    private Optional<String> checkTherapyField() {
        return therapyComboBox.getSelectionModel().isEmpty() ? Optional.of(new TherapyErrorReader().getMessageByCode("выбора терапии", -1))
                : Optional.empty();
    }

    private boolean checkFieldsFormat() {
        var errors = new ArrayList<String>();
        checkIterField().ifPresent(errors::add);
        checkRepeatField().ifPresent(errors::add);
        checkTherapyField().ifPresent(errors::add);
        if (!errors.isEmpty()) {
            showWindow(550, 400, "/org/openjfx/view/ErrorWindow.fxml",
                    "/org/openjfx/resources/images/ErrorIcon.png", "Ошибка",
                    errors);
            return false;
        }
        file.setIterationsNumber(iterField.getText());
        file.setRepeatsNumber(repeatField.getText());
        file.setTherapy(therapyComboBox.getSelectionModel().getSelectedItem());
        return true;
    }

    @FXML
    public void onRunModelButton() {
        var isCorrect = checkFieldsFormat();
        if (isCorrect) {
            makeForecast();
            exportMenu.setDisable(false);
        } else exportMenu.setDisable(true);
    }

    @FXML
    protected void handleInputDataButtonAction() {
        showWindow(1000, 730, "/org/openjfx/view/InputWindow.fxml",
                "/org/openjfx/resources/images/InputIcon.png", "Ввод данных");
    }

    @FXML
    protected void handleExitButtonAction() {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    void handleExportMenuItemAction() {
        showWindow(600, 200, "/org/openjfx/view/ExportWindow.fxml",
                "/org/openjfx/resources/images/ExportIcon.png", "Экспорт");
    }

    @FXML
    void handleFormatMenuItemAction() {
        showWindow(1050, 700, "/org/openjfx/view/FormatWindow.fxml",
                "/org/openjfx/resources/images/InfoIcon.png", "Формат ввода");
    }

    @FXML
    void handleAboutMenuItemAction() {
        showWindow(600, 400, "/org/openjfx/view/AboutWindow.fxml",
                "/org/openjfx/resources/images/InfoIcon.png", "О программе");
    }

    private void setBackgroundImage() {
        try {
            Image image = new Image(new FileInputStream("src/org/openjfx/resources/images/mainBackground.png"));
            inputPane.setBackground(new Background(new BackgroundImage(image, null, null, null, null)));
        } catch (FileNotFoundException e) {
            showWindow(550, 400, "/org/openjfx/view/ErrorWindow.fxml",
                    "/org/openjfx/resources/images/ErrorIcon.png", "Ошибка",
                    List.of("Пожалуйста, проверьте наличие файла mainBackground в папке src/org/openjfx/resources/images"));
        }
    }

    @FXML
    public void initialize() {
        runModelButton.setDisable(true);
        scrollPane.setVisible(false);
        setBackgroundImage();
    }
}

