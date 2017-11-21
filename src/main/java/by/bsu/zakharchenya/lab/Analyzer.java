package by.bsu.zakharchenya.lab;

import by.bsu.zakharchenya.lab.entity.Attribute;
import by.bsu.zakharchenya.lab.entity.Rule;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;


public class Analyzer {
    private Start startAction;
    private KBase base = new KBase();
    private Stack<TargetValue> targets = new Stack<>();
    private HashMap<Attribute, ContextValue> context = new HashMap<>();
    private boolean isFinished;

    public Analyzer(Start startAction, KBase base) {
        this.startAction = startAction;
        this.base = base;
    }

    private String processNextQuestion(Attribute target) {
        String[] choices = new String[target.getPossibleValues().size()];
        target.getPossibleValues().toArray(choices);
        int defaultChoice = 0;
        String input = (String) JOptionPane.showInputDialog(startAction.getMasterComponent(), target.getQuestion(), target.toString(), JOptionPane.QUESTION_MESSAGE, null, choices, choices[defaultChoice]);

        if (input == null) { startAction.writeLine("User canceled data input"); }
        return input;
    }

    public void process(Attribute target) {
        targets.clear(); context.clear();
        targets.push(new TargetValue(target, null));
        isFinished = false;
        while (!isFinished) {
            if (targets.empty()) { isFinished = true; break; }
            Attribute current = targets.peek().attribute;
            Rule toAnalyze = base.findNextRule(current);
            if (toAnalyze != null) { analyze(toAnalyze); }
            else {
                if (current.getQuestion() != null) {
                    String res = processNextQuestion(current);
                    if (res == null) { return; }
                    if (!targets.empty()) { toAnalyze = targets.pop().rule; }
                    context.put(current, new ContextValue(res, null));
                    startAction.writeLine("Your answer: " + current + " = " + res + "\n");
                    if (toAnalyze != null) { analyze(toAnalyze); }
                } else { isFinished = true; }
            }
        }
        String result = getTargetValue(target);
        if (result != null) {
            startAction.writeLine("------ [FINISH]  I have the answer: " + target + " = " + result + " ------\n");
        } else {
            for (Rule rule : target.getTargetRules()) {
                analyze(rule);
            }
            String resultSecond = getTargetValue(target);
            if (resultSecond != null) {
                startAction.writeLine("------ [FINISH]  I have the answer: " + target + " = " + resultSecond + " ------\n");
            } else {
                startAction.writeLine("------ [FINISH]  Sorry, I can not find the answer...   ------");
            }
        }
    }

    private String getTargetValue(Attribute target) {
        if (!context.containsKey(target)) { return null; }
        else return context.get(target).value;
    }

    private void analyze(Rule rule) {
        boolean res = true;
        rule.setAnalyzed(true);
        for (Map.Entry<Attribute, String> entry : rule.getConditions().entrySet()) {
            Boolean isRight = checkAttribute(entry.getKey(), entry.getValue());
            if (isRight == null) {
                targets.push(new TargetValue(entry.getKey(), rule));
                startAction.writeLine("Analyzed: rule " + rule + "... [?]  (" + entry.getKey() + ")");
                return;
            } else if (!isRight) {
                startAction.writeLine("Analyzed: rule " + rule + "... [-]   (" + entry.getKey() + ")");
                res = false;
                break;
            }
        }
        if (res) {
            context.put(rule.getTargetAttribute(), new ContextValue(rule.getTargetValue(), rule));
            startAction.writeLine("Analyzed: rule " + rule + "...  [+]    (" + rule.getTargetAttribute() + ")");
            if (targets.empty()) { isFinished = true; }
            else { targets.pop(); }
        }
        rule.setCorrect(res);
    }

    private Boolean checkAttribute(Attribute att, String val) {
        if (!context.containsKey(att)) { return null; }
        else { return context.get(att).value.equals(val); }
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
