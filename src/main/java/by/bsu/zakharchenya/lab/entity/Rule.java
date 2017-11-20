package by.bsu.zakharchenya.lab.entity;

import java.io.IOException;
import java.util.HashMap;

public class Rule {
    private Integer id;
    private HashMap<Attribute, String> conditions = new HashMap<>();
    private Attribute targetAttribute;
    private String targetValue;
    private boolean isAnalyzed;
    private Boolean isCorrect;

    public Rule(Integer id) {
        this.id = id;
    }

    public void addCondition(Attribute attribute, String value) throws IOException {
        String prevValue = conditions.put(attribute, value);
        if (prevValue != null) {
            throw new IOException("attribute with name " + attribute + " was overrided in rule " + id);
        }
    }

    @Override
    public String toString() {
        return id.toString();
    }

    public Integer getId() {
        return id;
    }

    private void setId(Integer id) {
        this.id = id;
    }

    public HashMap<Attribute, String> getConditions() {
        return conditions;
    }

    public void setConditions(HashMap<Attribute, String> conditions) {
        this.conditions = conditions;
    }

    public Attribute getTargetAttribute() {
        return targetAttribute;
    }

    public void setTargetAttribute(Attribute targetAttribute) {
        this.targetAttribute = targetAttribute;
    }

    public String getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(String targetValue) {
        this.targetValue = targetValue;
    }

    public boolean isAnalyzed() {
        return isAnalyzed;
    }

    public void setAnalyzed(boolean analyzed) {
        isAnalyzed = analyzed;
    }

    public Boolean getCorrect() {
        return isCorrect;
    }

    public void setCorrect(Boolean correct) {
        isCorrect = correct;
    }
}
