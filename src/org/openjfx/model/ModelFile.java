package org.openjfx.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelFile {
    // states, costs, age, cycle_l, therapies, th_effect, trans_matr, sex, conditions=None):
    private String states;

    private String costs;

    private String age;

    private String cycleLength;

    private String therapies;

    private String therapiesEffect;

    private String transMatrix;

    private String sex;

    private String therapy;

    private String iterationsNumber;

    private String repeatsNumber;

    public void setTransMatrix(String transMatrix) {
        this.transMatrix = transMatrix;
    }

    public String getIterationsNumber() {
        return iterationsNumber;
    }

    public void setIterationsNumber(String iterationsNumber) {
        this.iterationsNumber = iterationsNumber;
    }

    public String getRepeatsNumber() {
        return repeatsNumber;
    }

    public void setRepeatsNumber(String repeatsNumber) {
        this.repeatsNumber = repeatsNumber;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    private List<String> conditions = new ArrayList<>();

    private String cost;

    public String getStates() {
        return states;
    }

    public void setStates(String states) {
        this.states = states;
    }

    public List<String> getStatesList() {
        return Arrays.stream(this.states.split(", ")).toList();
    }

    public String getCosts() {
        return costs;
    }

    public void setCosts(String costs) {
        this.costs = costs;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getTransMatrix() {
        return transMatrix;
    }


    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getTherapy() {
        return therapy;
    }

    public void setTherapy(String therapy) {
        this.therapy = therapy;
    }

    public List<String> getConditions() {
        return conditions;
    }

    public void setConditions(List<String> conditions) {
        this.conditions = conditions;
    }

    public String getCycleLength() {
        return cycleLength;
    }

    public void setCycleLength(String cycleLength) {
        this.cycleLength = cycleLength;
    }

    public String getTherapies() {
        return therapies;
    }

    public void setTherapies(String therapies) {
        this.therapies = therapies;
    }

    public String getTherapiesEffect() {
        return therapiesEffect;
    }

    public void setTherapiesEffect(String therapiesEffect) {
        this.therapiesEffect = therapiesEffect;
    }
}
