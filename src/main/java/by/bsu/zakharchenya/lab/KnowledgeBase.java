package by.bsu.zakharchenya.lab;

import by.bsu.zakharchenya.lab.entity.Attribute;
import by.bsu.zakharchenya.lab.entity.Rule;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class KnowledgeBase {
    private final static String ifString = "if";
    private final static String thenString ="->";
    private final static String isString = "=";
    private final static String andString = "&";
    private final static String endAttributeToken = ";";
    private final static String questionAttributeToken = ":";
    private HashMap<String, Attribute> attributes = new HashMap<>();
    private HashMap<Integer, Rule> rules = new HashMap<>();
    HashSet<Attribute> targetAttributes = new HashSet<>();

    void initBase(FileInputStream fis) throws IOException {
        Scanner sc = new Scanner(fis);

       /* while (sc.hasNext() && !sc.hasNextInt())
            sc.next();*/

        while (sc.hasNext()) {
            String[] strings = sc.nextLine().split(" ");
            String idString = strings[0];
            int id = Integer.parseInt(idString.trim());
            //int id = (int)idd;
            String token;
            Rule rule = new Rule(id);
            if (!strings[1].equals(ifString)) {
                throw new IOException("can't read rule " + id);
            }
            int i = 1;
            do {
                token = strings[++i];
                if(!isString.equals(strings[i+1])) {
                    throw new IOException("can't read rule " + id);
                }
                if ("".equals(token)) {
                    throw new IOException("can't read Attribute");
                }
                String name = token;
                Attribute attribute = attributes.computeIfAbsent(name, k -> new Attribute(name));
                i+=2;
                String value = strings[i];
                attribute.add(value);
                rule.addCondition(attribute, value);
                i++;
                token = strings[i];
            } while (token.equals(andString));
            if (!token.equals(thenString)) {
                throw new IOException("can't read rule " + id);
            }
            if(token.equals(thenString)){
                i++;
                token = strings[i];
                String name = token;
                Attribute attribute = attributes.computeIfAbsent(name, k -> new Attribute(name));
                i+=2;
                String value = strings[i];
                attribute.add(value);

                rule.setTargetAttribute(attribute);
                rule.setTargetValue(value);
                attribute.getTargetRules().add(rule);
                targetAttributes.add(attribute);
                rules.put(id, rule);
            }
        }

        sc.close();
    }

    private Attribute readAttribute(String[] strings) throws IOException {
        String token = "";
        StringBuilder buffer = new StringBuilder();
        int i = 1;
        while (!thenString.equals(token) && !isString.equals(token)) {
            buffer.append(token);
            buffer.append(" ");
            token = strings[++i];
        }
        String name = buffer.toString().trim();
        if ("".equals(name)) {
            throw new IOException("can't read Attribute");
        }
        return attributes.computeIfAbsent(name, k -> new Attribute(name));
    }

    private String readValueToAttribute(Scanner sc, Attribute attribute) throws IOException {
        StringBuilder buffer = new StringBuilder();
        while (sc.hasNext()) {
            buffer.append(" ");
            buffer.append(sc.next());
        }
        String value = buffer.toString().trim();
        if ("".equals(value)) {
            throw new IOException("cant read Attribute: " + attributes + " because of wrong value");
        }
        attribute.add(value);
        return value;
    }

    void resetRules() {
        for (Rule r : rules.values()) {
            r.setAnalyzed(false);
            r.setCorrect(null);
        }
    }

    void initQuestions(FileInputStream fis) throws IOException {
        Scanner sc = new Scanner(fis, StandardCharsets.UTF_8.name());
        while (sc.hasNext()) {
            String[] strings = sc.nextLine().split(questionAttributeToken);
            String name = strings[0];
            String question = strings[1];
            Attribute attribute = attributes.get(name);
            if (attribute != null && !"".equals(question)) {
                attribute.setQuestion(question);
                continue;
            }
            throw new IOException("Can't read question for attribute " + name);
        }
    }

    Rule findNextRule(Attribute target) {
        for (Rule rule : target.getTargetRules()) {
            if (!rule.isAnalyzed()) {
                return rule;
            }
        }
        return null;
    }
}
