package org.openjfx.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Model {

    private String states;

    private String age;

    private String therapies;

    private String transMatrix;

    private String sex;

    private double[][] numericTransMatrix;

    private List<String> conditions = new ArrayList<>();


    public void setTherapies(String therapies) {
        this.therapies = therapies;
    }

    public void setStates(String states) {
        this.states = states;
    }

    public List<String> getStatesList() {
        return Arrays.stream(this.states.split(", ")).toList();
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public List<String> getTherapiesList() {
        return Arrays.stream(this.therapies.split(", ")).toList();
    }

    public String getTransMatrix() {
        return transMatrix;
    }

    private void setNumericTransMatrix(String matr) {
        if (matr.contains(":")) {
            var end_ind = matr.indexOf("',");
            matr = matr.substring(5, end_ind);
        }
        var matrRows = matr.replace("[", "").replace("]", "").strip().split(",");
        var statesCount = this.getStatesList().size();
        numericTransMatrix = new double[statesCount][statesCount];
        for (int i = 0; i < matrRows.length; i++) {
            numericTransMatrix[i / statesCount][i % statesCount] = Double.parseDouble(matrRows[i]);
        }
    }

    public double[][] getNumericTransMatrix() {
        return this.numericTransMatrix;
    }

    public void setTransMatrix(String transMatrix) {
        this.transMatrix = transMatrix;
        this.setNumericTransMatrix(transMatrix);
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Model() {

    }

    public List<String> getConditions() {
        return conditions;
    }

    public void setConditions(List<String> conditions) {
        this.conditions = conditions;
    }
}
