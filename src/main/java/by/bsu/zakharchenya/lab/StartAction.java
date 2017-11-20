package by.bsu.zakharchenya.lab;

import by.bsu.zakharchenya.lab.entity.Attribute;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;


class StartAction implements ActionListener {

    private JScrollPane masterComponent = null;
    private JTextPane textArea = new JTextPane();

    private MainAlgorithm mainAlgorithm = null;
    private KnowledgeBase base = new KnowledgeBase();

    StartAction() {
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        StyledDocument doc = textArea.getStyledDocument();
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        textArea.setEditable(false);
        masterComponent = new JScrollPane(textArea);

        FileInputStream fisR = null;
        FileInputStream fisQ = null;
        try {
            fisR = new FileInputStream(Run.pathToRules);
            fisQ = new FileInputStream("data/qq.txt");
            base.initBase(fisR);
            base.initQuestions(fisQ);
            mainAlgorithm = new MainAlgorithm(this, base);
        } catch (IOException e) {
            writeLine(e.getMessage());
        } finally {
            try {
                if (fisR != null) {
                    fisR.close();
                }
            } catch (IOException e) {
                writeLine(e.getMessage());
            }
            try {
                if (fisQ != null) {
                    fisQ.close();
                }
            } catch (IOException e) {
                writeLine(e.getMessage());
            }
        }
    }

    JComponent getMasterComponent() {
        return masterComponent;
    }

    void writeLine(final String line) {
        SwingUtilities.invokeLater(() -> textArea.setText(textArea.getText() + line + "\n"));
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        base.resetRules();

        textArea.setText("");

        Attribute[] choices = new Attribute[base.targetAttributes.size()];
        base.targetAttributes.toArray(choices);

        final Attribute input = (Attribute) JOptionPane.showInputDialog(getMasterComponent(), "Choose:",
                "Choose target", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);

        if (input == null) {
            writeLine("User cancelled input!");
            return;
        }

        new Thread(() -> mainAlgorithm.startAlgo(input)).start();
    }
}
