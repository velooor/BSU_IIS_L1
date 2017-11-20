package by.bsu.zakharchenya.lab.entity;

import java.util.HashSet;

public class Attribute implements Comparable<Attribute> {
    private String name;
    private HashSet<String> possibleValues = new HashSet<>();
    private HashSet<Rule> targetRules;
    private String question = null;

    public Attribute(String name) {
        this.name = name;
        targetRules = new HashSet<>();
    }

    public boolean add(String value) {
        return possibleValues.add(value);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Attribute o) {
        return toString().compareTo(o.toString());
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public HashSet<String> getPossibleValues() {
        return possibleValues;
    }

    public void setPossibleValues(HashSet<String> possibleValues) {
        this.possibleValues = possibleValues;
    }

    public HashSet<Rule> getTargetRules() {
        return targetRules;
    }

    public void setTargetRules(HashSet<Rule> targetRules) {
        this.targetRules = targetRules;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
