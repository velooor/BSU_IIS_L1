package by.bsu.zakharchenya.lab;

import by.bsu.zakharchenya.lab.entity.Attribute;
import by.bsu.zakharchenya.lab.entity.Rule;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;


public class MainAlgorithm {
    private StartAction startAction;
    private KnowledgeBase base = new KnowledgeBase();
    private Stack<TargetValue> targets = new Stack<>();
    private HashMap<Attribute, ContextValue> context = new HashMap<>();
    private boolean isFinished;

    public MainAlgorithm(StartAction startAction, KnowledgeBase base) {
        this.startAction = startAction;
        this.base = base;
    }

    private String nextQuestion(Attribute target) {
        String[] choices = new String[target.getPossibleValues().size()];
        target.getPossibleValues().toArray(choices);

        int defaultChoice = 0;

        String input = (String) JOptionPane.showInputDialog(startAction.getMasterComponent(), target.getQuestion(), target.toString(), JOptionPane.QUESTION_MESSAGE, null, choices, choices[defaultChoice]);

        if (input == null) {
            startAction.writeLine("User canceled data input");
        }

        return input;
    }

    public void startAlgo(Attribute target) {
        targets.clear();
        context.clear();
        targets.push(new TargetValue(target, null));
        isFinished = false;
        while (!isFinished) {
            if (targets.empty()) {
                isFinished = true;
                break;
            }
            Attribute current = targets.peek().attribute;
            Rule toAnalize = base.findNextRule(current);
            if (toAnalize != null) {// can find rule
                AnalyzeRule(toAnalize);
            } else {
                if (current.getQuestion() != null) {
                    String res = nextQuestion(current);
                    if (res == null) {
                        return;
                    }
                    if (!targets.empty()) {
                        toAnalize = targets.pop().rule;
                    }
                    context.put(current, new ContextValue(res, null));
                    startAction.writeLine("Answered: [" + current + " = " + res + "]\n");
                    if (toAnalize != null) {
                        AnalyzeRule(toAnalize);
                    }
                } else {
                    isFinished = true;
                }
            }
        }
        String result = getTargetValue(target);
        if (result != null) {
            startAction.writeLine("[FINISH]  I have the answer: " + target + " = " + result + "\n");
        } else {
            startAction.writeLine("[FINISH]  Sorry, I can not find the answer...");
        }
    }

    private String getTargetValue(Attribute target) {
        if (!context.containsKey(target)) {
            return null;
        }
        return context.get(target).value;
    }

    private Boolean AnalyzeRule(Rule rule) {
        boolean res = true;
        rule.setAnalyzed(true);
        for (Map.Entry<Attribute, String> entry : rule.getConditions().entrySet()) {
            Boolean isRight = checkAttribute(entry.getKey(), entry.getValue());
            if (isRight == null) {
                targets.push(new TargetValue(entry.getKey(), rule));
                startAction.writeLine("Rule #" + rule + " is UNKNOWNN! \t??? [" + entry.getKey() + "]");
                return null;
            } else if (!isRight) {
                startAction.writeLine("Rule #" + rule + " is FALSE!\t[" + entry.getKey() + " != " + entry.getValue() + "]");
                res = false;
                break;
            }
        }
        if (res) {
            context.put(rule.getTargetAttribute(), new ContextValue(rule.getTargetValue(), rule));
            startAction.writeLine("Rule #" + rule + " is TRUE!\t[" + rule.getTargetAttribute() + " = " + rule.getTargetValue() + "]");
            if (targets.empty()) {
                isFinished = true;
            } else {
                targets.pop();
            }
        }
        rule.setCorrect(res);
        return res;
    }

    private Boolean checkAttribute(Attribute att, String val) {
        if (!context.containsKey(att)) {
            return null;
        } else {
            return context.get(att).value.equals(val);
        }
    }

    private class ContextValue {
        String value;
        Rule rule;

        ContextValue(String value, Rule rule) {
            this.value = value;
            this.rule = rule;
        }
    }

    private class TargetValue {
        Attribute attribute;
        Rule rule;

        TargetValue(Attribute attribute, Rule rule) {
            this.attribute = attribute;
            this.rule = rule;
        }
    }

}
