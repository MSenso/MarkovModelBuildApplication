package org.openjfx.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.openjfx.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class InputWindow implements InteractiveWindow {
    private ModelFile file = new ModelFile();

    public ModelFile getModelFile() {
        return file;
    }

    public void setModelFile(ModelFile file) {
        this.file = file;
    }

    @FXML
    private Button inputButton;
    @FXML
    private Button formatButton;
    @FXML
    private Button condButton;
    @FXML
    private ImageView formatView;
    @FXML
    private ComboBox<String> condVarComboBox;
    @FXML
    private ComboBox<String> operatorComboBox;
    @FXML
    private ComboBox<String> editVarComboBox;
    @FXML
    private TextArea conditionValueField;
    @FXML
    private TextArea editValueField;
    @FXML
    private TextArea elseValueField;
    @FXML
    private ImageView condImageView;
    @FXML
    private TextArea stateField;
    @FXML
    private TextArea therapyField;
    @FXML
    private TextArea costField;
    @FXML
    private TextArea effectField;
    @FXML
    private TextArea matrixField;
    @FXML
    private TextField ageField;
    @FXML
    private ComboBox<String> sexComboBox;
    @FXML
    private BorderPane inputPane;
    private boolean isCorrectFormat = false;

    private Window parentWindow;

    public void setParentWindow(Window window) {
        this.parentWindow = window;
    }

    private Model model = new Model();

    public Model getModel() {
        return this.model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    @FXML
    private void showWindow(TextInputControl field) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/view/ImportWindow.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            System.out.println("Произошла ошибка при загрузке окна");
        }
        ImportWindow window = loader.getController();
        window.setTextArea(field);
        showStage(600, 150, "/org/openjfx/resources/images/ImportIcon.png", "Импорт данных", root);
        // field.setText(window.getText());
    }

    @FXML
    public void onFieldChange() {
        resetFormatImage();
        isCorrectFormat = false;
        checkButtonsAbility();
    }

    @FXML
    protected void handleImportStatesButtonAction() {
        showWindow(
                stateField);
    }

    @FXML
    protected void handleImportTherapiesButtonAction() {
        showWindow(
                therapyField);
    }

    @FXML
    protected void handleImportCostsButtonAction() {
        showWindow(
                costField);
    }

    @FXML
    protected void handleImportEffectsButtonAction() {
        showWindow(
                effectField);
    }

    @FXML
    protected void handleImportMatrixButtonAction() {
        showWindow(
                matrixField);
    }

    private void initializeVar(ComboBox<String> comboBox, List<String> items) {
        comboBox.getItems().removeAll(comboBox.getItems());
        comboBox.getItems().addAll(items);
        comboBox.getSelectionModel().select("Стоимости");
    }

    private void initializeOperator() {
        operatorComboBox.getItems().removeAll(operatorComboBox.getItems());
        operatorComboBox.getItems().addAll("==", "!=");
        operatorComboBox.getSelectionModel().select("==");
    }

    @FXML
    private void onSelectCondVarComboBox() {
        var arithmeticOperators = List.of(">", ">=", "<", "<=");
        if (condVarComboBox.getSelectionModel().getSelectedItem().equals("Возраст")) {
            operatorComboBox.getItems().addAll(arithmeticOperators);
        } else if (operatorComboBox.getItems().containsAll(arithmeticOperators)) {
            operatorComboBox.getItems().removeAll(arithmeticOperators);
            operatorComboBox.getSelectionModel().select("==");
        }
        condImageView.setImage(null);
    }

    private void checkButtonsAbility() {
        inputButton.setDisable(!isCorrectFormat);
        condButton.setDisable(!isCorrectFormat);
    }

    @FXML
    public void checkFieldsFormat() {
        List<String> errors = new ArrayList<>();
        onStatesChange().ifPresent(errors::add);
        onTherapiesChange().ifPresent(errors::add);
        onCostChange().ifPresent(errors::add);
        onEffectChange().ifPresent(errors::add);
        onMatrixChange().ifPresent(errors::add);
        onAgeChange().ifPresent(errors::add);
        onSexChange().ifPresent(errors::add);
        if (errors.isEmpty()) {
            setCorrectFormatIcon();
            isCorrectFormat = true;
            checkButtonsAbility();
        } else {
            setIncorrectFormatIcon();
            isCorrectFormat = false;
            checkButtonsAbility();
            showWindow(550, 400, "/org/openjfx/view/ErrorWindow.fxml",
                    "src/org/openjfx/resources/images/ErrorIcon.png", "Ошибка",
                    errors);
        }
    }

    private void setBackgroundImage() {
        try {
            Image image = new Image(new FileInputStream("src/org/openjfx/resources/images/addBackground.png"));
            inputPane.setBackground(new Background(new BackgroundImage(image, null, null, null, null)));
        } catch (FileNotFoundException e) {
            showWindow(550, 400, "/org/openjfx/view/ErrorWindow.fxml",
                    "src/org/openjfx/resources/images/ErrorIcon.png", "Ошибка",
                    List.of("Пожалуйста, проверьте наличие файла mainBackground в папке src/org/openjfx/resources/images"));
        }
    }

    @FXML
    public void initialize() {
        initializeVar(condVarComboBox, List.of("Состояния", "Терапии", "Стоимости", "Эффективность", "Матрица переходов", "Пол", "Возраст"));
        initializeVar(editVarComboBox, List.of("Стоимости", "Эффективность", "Матрица переходов"));
        initializeOperator();
        sexComboBox.getItems().addAll(List.of("Мужской", "Женский"));
        inputButton.setDisable(true);
        stateField.requestFocus();
        condButton.setDisable(true);
        setBackgroundImage();
    }

    private void setCursor(Button button, Cursor cursor) {
        button.cursorProperty().setValue(cursor);
    }

    @FXML
    public void onInputButtonEntered() {
        setCursor(inputButton, Cursor.HAND);
    }

    @FXML
    public void onInputButtonExited() {
        setCursor(inputButton, Cursor.DEFAULT);
    }

    @FXML
    public void onFormatButtonEntered() {
        setCursor(formatButton, Cursor.HAND);
    }

    @FXML
    public void onFormatButtonExited() {
        setCursor(formatButton, Cursor.DEFAULT);
    }

    private void setImageIcon(ImageView view, String path, String name) {
        try {
            Image image = new Image(new FileInputStream(path));
            view.setX(35);
            view.setY(35);
            view.setImage(image);
        } catch (FileNotFoundException e) {
            showWindow(550, 400, "/org/openjfx/view/ErrorWindow.fxml",
                    "src/org/openjfx/resources/images/ErrorIcon.png", "Ошибка",
                    List.of("Пожалуйста, проверьте наличие файла " + name + " в папке src/org/openjfx/resources/images"));
        }
    }

    private void resetFormatImage() {
        formatView.setImage(null);
    }

    private void setCorrectFormatIcon() {
        setImageIcon(formatView, "src/org/openjfx/resources/images/CorrectIcon.png", "CorrectIcon.png");
    }

    private void setIncorrectFormatIcon() {
        setImageIcon(formatView, "src/org/openjfx/resources/images/ErrorIcon.png", "ErrorIcon.png");
    }

    private int sexPropertyMapper(String input) {
        if (input != null) {
            input = input.toLowerCase();
            return switch (input) {
                case "мужской" -> 0;
                case "женский" -> 1;
                default -> -1;
            };
        } else return -1;
    }

    private List<String> parseOutput(String result, String defName) {
        List<String> outputs = new ArrayList<>();
        var i = "";
        var j = "";
        var output = "";
        var errorCode = "";
        var leftBrace = result.lastIndexOf("(");
        var delimiter = result.indexOf(", ");
        if (leftBrace != -1) {
            errorCode = result.substring(leftBrace + 1, delimiter);
            if (!errorCode.equals("0")) {
                leftBrace = result.lastIndexOf("(");
                errorCode = result.substring(leftBrace + 1, delimiter);
                var substr = result.substring(delimiter + 2);
                if (defName.equals("matr")) {
                    delimiter = substr.indexOf(", ");
                    var brace = substr.indexOf(")");
                    i = substr.substring(0, brace);
                    if (delimiter != -1) {
                        substr = substr.substring(delimiter + 2);
                        brace = substr.indexOf(")");
                        j = substr.substring(0, brace);
                    }
                }
            } else {
                output = result.substring(delimiter + 2, result.length() - 1);
            }
        } else errorCode = result;
        outputs.add(errorCode);
        if (!output.isEmpty()) outputs.add(output);
        if (!i.isEmpty()) outputs.add(i);
        if (defName.equals("matr") && !j.isEmpty()) outputs.add(j);
        return outputs;
    }

    private List<String> retrieveFromParse(String defName, String input) {
        List<String> outputs = new ArrayList<>();
        ProcessBuilder pb = new ProcessBuilder("python3",
                "src\\org\\openjfx\\scripts\\InputToParse.py", defName, input, stateField.getText(), therapyField.getText());
        try {
            Process p = pb.start();
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));
            String output = "";
            while (output != null) {
                output = stdInput.readLine();
                System.out.println(output);
                if (output != null) outputs.add(output);
            }
            var result = outputs.get(outputs.size() - 1);
            return parseOutput(result, defName);
        } catch (IOException e) {
            showWindow(550, 400, "/org/openjfx/view/ErrorWindow.fxml",
                    "src/org/openjfx/resources/images/ErrorIcon.png", "Ошибка",
                    List.of("Пожалуйста, проверьте наличие файла InputToParse.py в папке src/org/openjfx/scripts"));
            return outputs;
        }
    }

    private void writeFieldToFile(String path, String content) {
        try {
            FileWriter fWriter = new FileWriter(path);
            fWriter.write(content);
            fWriter.close();
        } catch (IOException e) {
            showWindow(550, 400, "/org/openjfx/view/ErrorWindow.fxml",
                    "src/org/openjfx/resources/images/ErrorIcon.png", "Ошибка",
                    List.of("Произошла ошибка во время записи поля в файл"));
        }
    }

    private Optional<String> onStatesChange() {
        var errorCode = 0;
        var outputs = retrieveFromParse("states", stateField.getText());
        errorCode = Integer.parseInt(outputs.get(0));
        if (errorCode == 0) {
            model.setStates(stateField.getText());
            file.setStates(stateField.getText());
            writeFieldToFile("src/org/openjfx/resources/modelfiles/parsed_states.txt", outputs.get(1));
            return Optional.empty();
        } else return Optional.of(new StatesErrorReader().getMessageByCode("состояния", errorCode));
    }

    private Optional<String> onTherapiesChange() {
        var errorCode = 0;
        var outputs = retrieveFromParse("therapies", therapyField.getText());
        errorCode = Integer.parseInt(outputs.get(0));
        if (errorCode == 0) {
            model.setTherapies(therapyField.getText());
            file.setTherapies(therapyField.getText());
            writeFieldToFile("src/org/openjfx/resources/modelfiles/parsed_therapies.txt", outputs.get(1));
            return Optional.empty();
        } else return Optional.of(new TherapiesErrorReader().getMessageByCode("терапии", errorCode));
    }

    private Optional<String> onCostChange() {
        var errorCode = 0;
        var outputs = retrieveFromParse("cost", costField.getText());
        errorCode = Integer.parseInt(outputs.get(0));
        if (errorCode == 0) {
            writeFieldToFile("src/org/openjfx/resources/modelfiles/parsed_costs.txt", outputs.get(1));
            file.setCosts(costField.getText());
            return Optional.empty();
        } else {
            if (outputs.size() == 3)
                return Optional.of(new CostsErrorReader().getMessageByCode("стоимости", errorCode, Integer.parseInt(outputs.get(2))));
            else return Optional.of(new CostsErrorReader().getMessageByCode("стоимости", errorCode));
        }
    }

    private Optional<String> onEffectChange() {
        var errorCode = 0;
        var outputs = retrieveFromParse("eff", effectField.getText());
        errorCode = Integer.parseInt(outputs.get(0));
        if (errorCode == 0) {
            file.setTherapiesEffect(effectField.getText());
            writeFieldToFile("src/org/openjfx/resources/modelfiles/parsed_eff.txt", outputs.get(1));
            return Optional.empty();
        } else {
            if (outputs.size() == 3)
                return Optional.of(new EffectsErrorReader().getMessageByCode("эффективности", errorCode, Integer.parseInt(outputs.get(2))));
            else return Optional.of(new EffectsErrorReader().getMessageByCode("эффективности", errorCode));
        }
    }

    private Optional<String> onMatrixChange() {
        var errorCode = 0;
        var outputs = retrieveFromParse("matr", matrixField.getText());
        errorCode = Integer.parseInt(outputs.get(0));
        if (errorCode == 0) {
            model.setTransMatrix(outputs.get(1));
            file.setTransMatrix(matrixField.getText());
            return Optional.empty();
        } else {
            if (outputs.size() == 3)
                return Optional.of(new MatrixErrorReader().getMessageByCode("матрица переходов", errorCode, Integer.parseInt(outputs.get(1)), Integer.parseInt(outputs.get(2))));
            else if (outputs.size() == 2)
                return Optional.of(new MatrixErrorReader().getMessageByCode("матрица переходов", errorCode, Integer.parseInt(outputs.get(1))));
            else return Optional.of(new MatrixErrorReader().getMessageByCode("матрица переходов", errorCode));
        }
    }

    private Optional<String> onSexChange() {
        var errorCode = sexPropertyMapper(sexComboBox.getSelectionModel().getSelectedItem());
        if (errorCode == -1) return Optional.of(new SexErrorReader().getMessageByCode("пол", errorCode));
        else {
            model.setSex(String.valueOf(errorCode));
            file.setSex(sexComboBox.getSelectionModel().getSelectedItem());
            return Optional.empty();
        }
    }

    private Optional<String> onAgeChange() {
        var errorCode = 0;
        var outputs = retrieveFromParse("age", ageField.getText());
        errorCode = Integer.parseInt(outputs.get(0));
        if (errorCode == 0) {
            model.setAge(ageField.getText());
            file.setAge(ageField.getText());
            return Optional.empty();
        } else return Optional.of(new AgeErrorReader().getMessageByCode("возраст", errorCode));
    }

    @FXML
    public void onInputButtonPressed() {
        List<String> therapyChoice = new ArrayList<>(model.getTherapiesList());
        therapyChoice.add("Контроль");
        ((InteractiveWindow) parentWindow).setModel(model);
        ((InteractiveWindow) parentWindow).setModelFile(file);
        ((MainWindow) parentWindow).drawModel();
        ((MainWindow) parentWindow).setTherapyComboBox(therapyChoice);
        ((MainWindow) parentWindow).setRunModelButtonAbility(false);
        ((MainWindow) parentWindow).showScrollPane();
        ((Stage) inputButton.getScene().getWindow()).close();
    }

    private String varMapper(String varText) {
        return switch (varText) {
            case "Состояния" -> "states";
            case "Терапии" -> "therapies";
            case "Стоимости" -> "costs";
            case "Эффективность" -> "th_effect";
            case "Матрица переходов" -> "trans_matr";
            case "Пол" -> "sex";
            case "Возраст" -> "age";
            default -> "";
        };
    }

    private String getStatesError(String field, List<Integer> indexes) {
        return new StatesErrorReader().getMessageByCode(field, indexes.get(0));
    }

    private String getTherapiesError(String field, List<Integer> indexes) {
        return new TherapiesErrorReader().getMessageByCode(field, indexes.get(0));
    }

    private String getCostsError(String field, List<Integer> indexes) {
        if (indexes.size() == 2) return new CostsErrorReader().getMessageByCode(field, indexes.get(0), indexes.get(1));
        return new CostsErrorReader().getMessageByCode(field, indexes.get(0));
    }

    private String getEffError(String field, List<Integer> indexes) {
        if (indexes.size() == 2)
            return new EffectsErrorReader().getMessageByCode(field, indexes.get(0), indexes.get(1));
        return new EffectsErrorReader().getMessageByCode(field, indexes.get(0));
    }

    private String getMatrError(String field, List<Integer> indexes) {
        if (indexes.size() == 2) return new MatrixErrorReader().getMessageByCode(field, indexes.get(0), indexes.get(1));
        else if (indexes.size() == 3)
            return new MatrixErrorReader().getMessageByCode(field, indexes.get(0), indexes.get(1), indexes.get(2));
        return new MatrixErrorReader().getMessageByCode(field, indexes.get(0));
    }

    private String getSexError(String field, List<Integer> indexes) {
        return new SexErrorReader().getMessageByCode(field, indexes.get(0));
    }

    private String getAgeError(String field, List<Integer> indexes) {
        return new AgeErrorReader().getMessageByCode(field, indexes.get(0));
    }

    private String getVarError(String var, String field, List<Integer> indexes) {
        return switch (var) {
            case "Состояния" -> getStatesError(field, indexes);
            case "Терапии" -> getTherapiesError(field, indexes);
            case "Стоимости" -> getCostsError(field, indexes);
            case "Эффективность" -> getEffError(field, indexes);
            case "Матрица переходов" -> getMatrError(field, indexes);
            case "Пол" -> getSexError(field, indexes);
            case "Возраст" -> getAgeError(field, indexes);
            default -> null;
        };
    }

    private String switchVarNameByIndex(int index) {
        if (index == 0) return condVarComboBox.getSelectionModel().getSelectedItem();
        return editVarComboBox.getSelectionModel().getSelectedItem();
    }

    private String switchFieldNameByIndex(int index) {
        return switch (index) {
            case 0 -> "значения условия";
            case 1 -> "значения переменной при истинном условии";
            default -> "значения переменной при ложном условии";
        };
    }

    private String writeCondition() {
        List<String> words = new ArrayList<>(Arrays.asList("Если", condVarComboBox.getSelectionModel().getSelectedItem(),
                operatorComboBox.getSelectionModel().getSelectedItem(), conditionValueField.getText(), "то",
                editVarComboBox.getSelectionModel().getSelectedItem(), "=", editValueField.getText()));
        if (!elseValueField.getText().isEmpty())
            words.addAll(List.of("иначе", elseValueField.getText()));
        return String.join(" ", words);
    }

    private void checkConditionFlags(String res, String input) {
        List<String> flags = Arrays.stream(res.split("; ")).toList();
        var errors = new ArrayList<String>();
        for (int index = 0; index < flags.size(); index++) {
            var i = 0;
            var j = 0;
            var k = 0;
            var errorCodes = new ArrayList<Integer>();
            var flag = flags.get(index);
            i = flag.indexOf("(");
            if (i == -1) {
                i = Integer.parseInt(flag);
                if (i != 0) errorCodes.add(i);
            } else {
                i = flag.lastIndexOf("(");
                var delimiter = flag.indexOf(", ");
                i = Integer.parseInt(flag.substring(i + 1, delimiter));
                errorCodes.add(i);
                var rightBrace = flag.indexOf(")");
                j = Integer.parseInt(flag.substring(delimiter + 2, rightBrace));
                errorCodes.add(j);
                if (rightBrace != flag.length() - 1) {
                    k = Integer.parseInt(flag.substring(rightBrace + 2, flag.length() - 1));
                    errorCodes.add(k);
                }
            }
            if (!errorCodes.isEmpty()) {
                errors.add(getVarError(switchVarNameByIndex(index), switchFieldNameByIndex(index), errorCodes));
            }
        }
        if (errors.isEmpty()) {
            setImageIcon(condImageView, "src/org/openjfx/resources/images/CorrectIcon.png", "CorrectIcon.png");
            model.getConditions().add(input);
            file.getConditions().add(writeCondition());
        } else {
            showWindow(550, 400, "/org/openjfx/view/ErrorWindow.fxml",
                    "src/org/openjfx/resources/images/ErrorIcon.png", "Ошибка",
                    errors);
            setImageIcon(condImageView, "src/org/openjfx/resources/images/ErrorIcon.png", "src/org/openjfx/resources/images/ErrorIcon.png");
        }
    }

    private String getConditionValueFiled(String condVar) {
        if (condVar.equals("sex")) {
            return switch (conditionValueField.getText().toLowerCase()) {
                case "мужской" -> "0";
                case "женский" -> "1";
                default -> "-1";
            };
        }
        return conditionValueField.getText();
    }

    @FXML
    public void onCondButtonPressed() {
        var condVar = varMapper(condVarComboBox.getSelectionModel().getSelectedItem());
        var operator = operatorComboBox.getSelectionModel().getSelectedItem();
        var condVal = getConditionValueFiled(condVar);
        var editVar = varMapper(editVarComboBox.getSelectionModel().getSelectedItem());
        var editValue = editValueField.getText();
        var elseValue = elseValueField.getText().isBlank() ? null : elseValueField.getText();
        var inputList = new ArrayList<String>();
        if (elseValue == null)
            inputList = new ArrayList<>(List.of(condVar, operator, condVal, editVar, editValue));
        else inputList = new ArrayList<>(List.of(condVar, operator, condVal, editVar, editValue, elseValue));
        var input = String.join("#", inputList);
        var matrix = model.getTransMatrix();
        var sex = model.getSex();
        var age = model.getAge();
        List<String> outputs = new ArrayList<>();
        ProcessBuilder pb = new ProcessBuilder("python3",
                "src\\org\\openjfx\\scripts\\InputToReader.py",
                matrix, sex, age, input);
        try {
            Process p = pb.start();
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));
            String output = "";
            while (output != null) {
                output = stdInput.readLine();
                System.out.println(output);
                outputs.add(output);
            }
            checkConditionFlags(outputs.get(0), input);
        } catch (IOException e) {
            showWindow(550, 400, "/org/openjfx/view/ErrorWindow.fxml",
                    "src/org/openjfx/resources/images/ErrorIcon.png", "Ошибка",
                    List.of("Пожалуйста, проверьте наличие файла InputToReader.py в папке src/org/openjfx/scripts"));
        }
    }

    @FXML
    void handleFormatMenuItemAction() {
        showWindow(1050, 700, "/org/openjfx/view/FormatWindow.fxml",
                "src/org/openjfx/resources/images/InfoIcon.png", "Формат ввода");
    }

    @FXML
    void handleAboutMenuItemAction() {
        showWindow(600, 400, "/org/openjfx/view/AboutWindow.fxml",
                "src/org/openjfx/resources/images/InfoIcon.png", "О программе");
    }
}
