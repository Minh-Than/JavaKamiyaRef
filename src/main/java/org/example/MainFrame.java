package org.example;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class MainFrame extends JFrame {
    private JPanel panel1;
    private JPanel topPanel;
    private JPanel bottomPanel;
    private JPanel inputPanel;
    private JPanel flagPanel;
    private JPanel buttonPanel;
    private JPanel findPanel;
    private JButton findButton;
    private JButton nextButton;
    private JButton backButton;
    private JButton clearButton;
    private JTextField sqrIntText;
    private JTextField prefIntText;
    private JTextField sqrSqrtText;
    private JTextField prefSqrtText;
    private JComboBox<String> negIntCB;
    private JComboBox<String> negSqrtCB;

    public Map<Double, PresetData> presetDict = new Hashtable<>();
    public ArrayList<ResultData> resultData;

    private final ApplicationModel applicationModel;

    public MainFrame(ApplicationModel applicationModel) {
        this.applicationModel = applicationModel;

        $$$setupUI$$$();
        initializePresetData(presetDict);
        setUpGlobalKeyBindings();

        nextButton.setEnabled(false);
        backButton.setEnabled(false);
        clearButton.setEnabled(false);

        findButton.addActionListener(e -> {
            int sqrInt;
            int sqrSqrt;
            int prefInt = Integer.parseInt(prefIntText.getText().isEmpty() ? "0" : prefIntText.getText());
            int prefSqrt = Integer.parseInt(prefSqrtText.getText().isEmpty() ? "0" : prefSqrtText.getText());

            if (sqrIntText.getText().isEmpty() && sqrSqrtText.getText().isEmpty() && prefIntText.getText().isEmpty() && prefSqrtText.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Inputs are empty. Please try again.", "Empty Inputs", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                sqrInt = Integer.parseInt(sqrIntText.getText());
                sqrSqrt = Integer.parseInt(sqrSqrtText.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "The input must only be integer. Please try again.", "Wrong Input Format", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // If is 2^n + 0*√2 (only power of 2)
            if (isPowerOfTwo(sqrInt) && sqrSqrt == 0) {
                JOptionPane.showMessageDialog(null, "Just divide in half once or multiple times you massive knob head.", "Dumb Bitch Message", JOptionPane.WARNING_MESSAGE);
                return;
            }


            resultData = new ArrayList<>();
            int resultCount = 0;
            double papersize = round(sqrInt + (sqrSqrt * Math.sqrt(2)), 5);
            double x1, x2, y;
            PresetData result1;
            PresetData result2;

            for (int ixr1 = 0; ixr1 < Math.ceil(papersize); ixr1++) {
                for (int ixr2 = 0; ixr2 < Math.ceil(papersize / Math.sqrt(2)); ixr2++) {
                    for (int iyr1 = 0; iyr1 < Math.ceil(papersize); iyr1++) {
                        for (int iyr2 = 0; iyr2 < Math.ceil(papersize / Math.sqrt(2)); iyr2++) {
                            x1 = round(ixr1 + (ixr2 * Math.sqrt(2)), 5);
                            x2 = round(papersize - x1, 5);
                            y = round(iyr1 + (iyr2 * Math.sqrt(2)), 5);

                            if ((x1 != 0) && (y != 0) && !(x1 == papersize) && !(x1 > papersize) && !(y > papersize)) {
                                result1 = preset(presetDict, round(x1 / y, 5));
                                result2 = preset(presetDict, round(x2 / y, 5));

                                if (!result1.getRatio().equals("NA") && (!result2.getRatio().equals("NA"))) {
                                    if (Double.toString(round(x1 / y, 5)).equals(Double.toString(round(x2 / y, 5)))) {
                                        continue;
                                    }

                                    // negative integer flags
                                    if (negIntCB.getSelectedIndex() == 1 && ((ixr1 < 0 || sqrInt - ixr1 < 0) && iyr1 < 0)) {
                                        continue;
                                    }
                                    if (negIntCB.getSelectedIndex() == 2 && (ixr1 < 0 || sqrInt - ixr1 < 0 || iyr1 < 0)) {
                                        continue;
                                    }

                                    // negative sqrt(2) flags
                                    if (negSqrtCB.getSelectedIndex() == 1 && ((ixr2 < 0 || sqrInt - ixr2 < 0) && iyr2 < 0)) {
                                        continue;
                                    }
                                    if (negSqrtCB.getSelectedIndex() == 2 && (ixr2 < 0 || sqrInt - ixr2 < 0 || iyr2 < 0)) {
                                        continue;
                                    }

                                    ResultData tempRes = new ResultData(round(x1 / papersize, 5),
                                            round(y / papersize, 5),
                                            output(ixr1, ixr2),
                                            output(sqrInt - ixr1, sqrSqrt - ixr2),
                                            output(iyr1, iyr2),
                                            result1.getRatio(),
                                            result2.getRatio(),
                                            "".concat(Double.toString(round(x1 / y, 5)).replace(".", "_") + "_l.png"),
                                            "".concat(Double.toString(round(x2 / y, 5)).replace(".", "_") + "_r.png"),
                                            result1.getInt() + result2.getInt());

                                    if (((ixr1 == prefInt) && (ixr2 == prefSqrt)) || ((iyr1 == prefInt) && (iyr2 == prefSqrt))) {
                                        tempRes.setOrder(tempRes.getOrder() / 10);
                                    }
                                    resultData.add(tempRes);
                                    resultCount++;

                                    if (resultCount > 1) {
                                        for (int i = resultCount - 1; i > 0; i--) {
                                            if (resultData.get(i).getOrder() < resultData.get(i - 1).getOrder()) {
                                                resultData.set(i, resultData.get(i - 1));
                                                resultData.set(i - 1, tempRes);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (resultCount != 0) {
                applicationModel.reset();
                applicationModel.setResultData(resultData);

                nextButton.setEnabled(true);
                backButton.setEnabled(true);
                clearButton.setEnabled(true);

                bottomPanel.validate();
                bottomPanel.repaint();
            }
        });
        nextButton.addActionListener(e -> {
            applicationModel.setResultIndex(applicationModel.getResultIndex() + 1);
            if (applicationModel.getResultIndex() >= applicationModel.getResultData().size()) {
                applicationModel.setResultIndex(0);
            }
            bottomPanel.validate();
            bottomPanel.repaint();
        });
        backButton.addActionListener(e -> {
            applicationModel.setResultIndex(applicationModel.getResultIndex() - 1);
            if (applicationModel.getResultIndex() <= -1) {
                applicationModel.setResultIndex(applicationModel.getResultData().size() - 1);
            }
            bottomPanel.validate();
            bottomPanel.repaint();
        });
        clearButton.addActionListener(e -> {
            applicationModel.reset();
            nextButton.setEnabled(false);
            backButton.setEnabled(false);
            clearButton.setEnabled(false);
            bottomPanel.validate();
            bottomPanel.repaint();
        });
    }

    public static boolean isPowerOfTwo(int n) {
        if (n == 0)
            return false;

        double v = Math.log(n) / Math.log(2);
        return (int) (Math.ceil(v))
                == (int) (Math.floor(v));
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static void setIndividualPreset(Map<Double, PresetData> presetDict, double key, String ratio, double doubleNum, int intNum) {
        presetDict.put(key, new PresetData(ratio, doubleNum, intNum));
    }

    public static void initializePresetData(Map<Double, PresetData> presetDict) {
        setIndividualPreset(presetDict, 1, "1:1", 1, 1);
        setIndividualPreset(presetDict, 0.5, "1:2", 0.5, 2);
        setIndividualPreset(presetDict, 2, "2:1", 2, 2);
        setIndividualPreset(presetDict, 0.25, "1:4", 0.25, 3);
        setIndividualPreset(presetDict, 0.75, "3:4", 0.75, 3);
        setIndividualPreset(presetDict, 1.33333, "4:3", 1.33333, 3);
        setIndividualPreset(presetDict, 4, "4:1", 4, 3);
        setIndividualPreset(presetDict, 0.125, "1:8", 0.125, 4);
        setIndividualPreset(presetDict, 0.375, "3:8", 0.375, 4);
        setIndividualPreset(presetDict, 0.625, "5:8", 0.625, 4);
        setIndividualPreset(presetDict, 0.875, "7:8", 0.875, 4);
        setIndividualPreset(presetDict, 1.14286, "8:7", 1.14286, 4);
        setIndividualPreset(presetDict, 1.6, "8:5", 1.6, 4);
        setIndividualPreset(presetDict, 2.66667, "8:3", 2.66667, 4);
        setIndividualPreset(presetDict, 8, "8:1", 8, 4);
        setIndividualPreset(presetDict, 0.0625, "1:16", 0.0625, 5);
        setIndividualPreset(presetDict, 0.1875, "3:16", 0.1875, 5);
        setIndividualPreset(presetDict, 0.3125, "5:16", 0.3125, 5);
        setIndividualPreset(presetDict, 0.4375, "7:16", 0.4375, 5);
        setIndividualPreset(presetDict, 0.5625, "9:16", 0.5625, 5);
        setIndividualPreset(presetDict, 0.6875, "11:16", 0.6875, 5);
        setIndividualPreset(presetDict, 0.8125, "13:16", 0.8125, 5);
        setIndividualPreset(presetDict, 0.9375, "15:16", 0.9375, 5);
        setIndividualPreset(presetDict, 1.06667, "16:15", 1.06667, 5);
        setIndividualPreset(presetDict, 1.23077, "16:13", 1.23077, 5);
        setIndividualPreset(presetDict, 1.45455, "16:11", 1.45455, 5);
        setIndividualPreset(presetDict, 1.77778, "16:9", 1.77778, 5);
        setIndividualPreset(presetDict, 3.2, "16:5", 3.2, 5);
        setIndividualPreset(presetDict, 5.33333, "16:3", 5.33333, 5);
        setIndividualPreset(presetDict, 16, "16:1", 16, 5);
        setIndividualPreset(presetDict, 0.17678, "1:4√2", 0.17678, 6);
        setIndividualPreset(presetDict, 0.53033, "3:4√2", 0.53033, 6);
        setIndividualPreset(presetDict, 0.35355, "1:2√2", 0.35355, 5);
        setIndividualPreset(presetDict, 0.70711, "1:√2", 0.70711, 4);
        setIndividualPreset(presetDict, 1.41421, "√2:1", 1.41421, 4);
        setIndividualPreset(presetDict, 2.82843, "2√2:1", 2.82843, 5);
        setIndividualPreset(presetDict, 1.88562, "4√2:3", 1.88562, 6);
        setIndividualPreset(presetDict, 5.65685, "4√2:1", 5.65685, 6);
        setIndividualPreset(presetDict, 0.2612, "1:2+√2", 0.2612, 3);
        setIndividualPreset(presetDict, 0.41421, "1:1+√2", 0.41421, 2);
        setIndividualPreset(presetDict, 0.58579, "2:2+√2", 0.58579, 3);
        setIndividualPreset(presetDict, 1.54692, "4+2√2:3+√2", 1.54692, 5);
        setIndividualPreset(presetDict, 0.64645, "3+√2:4+2√2", 0.64645, 5);
        setIndividualPreset(presetDict, 4.82843, "2+2√2:1", 4.82843, 4);
        setIndividualPreset(presetDict, 0.20711, "1:2+2√2", 0.20711, 4);
        setIndividualPreset(presetDict, 1.70711, "2+√2:2", 1.70711, 3);
        setIndividualPreset(presetDict, 2.41421, "1+√2:1", 2.41421, 2);
        setIndividualPreset(presetDict, 3.41421, "2+√2:1", 3.41421, 4);
        setIndividualPreset(presetDict, 1.2612, "2+2√2:1+2√2", 1.2612, 4);
        setIndividualPreset(presetDict, 0.79289, "1+2√2:2+2√2", 0.79289, 4);
        setIndividualPreset(presetDict, 1.20711, "1+√2:2", 1.20711, 4);
        setIndividualPreset(presetDict, 0.82843, "2:1+√2", 0.82843, 4);
        setIndividualPreset(presetDict, 0.17157, "1:3+2√2", 0.17157, 4);
        setIndividualPreset(presetDict, 5.82843, "3+2√2:1", 5.82843, 4);
        setIndividualPreset(presetDict, 1.17157, "4:2+√2", 1.17157, 5);
        setIndividualPreset(presetDict, 0.85355, "2+√2:4", 0.85355, 5);
        setIndividualPreset(presetDict, 0.14645, "1:4+2√2", 0.14645, 5);
        setIndividualPreset(presetDict, 6.82843, "4+2√2:1", 6.82843, 5);
    }

    public static PresetData preset(Map<Double, PresetData> presetDict, double key) {
        return presetDict.containsKey(key) ? presetDict.get(key) : new PresetData("NA", 0, 0);
    }

    public static String output(int n1, int n2) {
        String result = n2 + "√2";

        if (n2 == 1) {
            result = "√2";
        }
        if (n1 != 0) {
            result = n1 + "+" + result;
        }
        if (n2 == 0) {
            result = Integer.toString(n1);
        }

        return result;
    }

    private void setUpGlobalKeyBindings() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                int keyCode = e.getKeyCode();

                if (keyCode == KeyEvent.VK_RIGHT) {
                    nextButton.doClick();
                    return true;
                } else if (keyCode == KeyEvent.VK_LEFT) {
                    backButton.doClick();
                    return true;
                }
            }

            return false;  // allow the event to be processed by other listeners
        });
    }

    private void createUIComponents() {
        panel1 = new JPanel();
        this.add(panel1);
        bottomPanel = new DrawPanel(applicationModel);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setPreferredSize(new Dimension(775, 650));
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setOrientation(0);
        splitPane1.setVisible(true);
        panel1.add(splitPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        splitPane1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        topPanel = new JPanel();
        topPanel.setLayout(new GridLayoutManager(1, 6, new Insets(10, 10, 10, 10), -1, -1));
        splitPane1.setLeftComponent(topPanel);
        inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        topPanel.add(inputPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Square length:");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Target ratio (optional):");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(label2, gbc);
        sqrIntText = new JTextField();
        sqrIntText.setPreferredSize(new Dimension(60, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(sqrIntText, gbc);
        prefIntText = new JTextField();
        prefIntText.setPreferredSize(new Dimension(60, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(prefIntText, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("+");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(label3, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("+");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(label4, gbc);
        sqrSqrtText = new JTextField();
        sqrSqrtText.setPreferredSize(new Dimension(60, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(sqrSqrtText, gbc);
        prefSqrtText = new JTextField();
        prefSqrtText.setPreferredSize(new Dimension(60, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(prefSqrtText, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("√2");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(label5, gbc);
        final JLabel label6 = new JLabel();
        label6.setText("√2");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(label6, gbc);
        flagPanel = new JPanel();
        flagPanel.setLayout(new GridBagLayout());
        topPanel.add(flagPanel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Int flag:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        flagPanel.add(label7, gbc);
        final JLabel label8 = new JLabel();
        label8.setText("√2 flag:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        flagPanel.add(label8, gbc);
        negIntCB = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("None");
        defaultComboBoxModel1.addElement("Allow -int on an axis");
        defaultComboBoxModel1.addElement("No -int");
        negIntCB.setModel(defaultComboBoxModel1);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        flagPanel.add(negIntCB, gbc);
        negSqrtCB = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("None");
        defaultComboBoxModel2.addElement("Allow -√2 on an axis");
        defaultComboBoxModel2.addElement("No -√2");
        negSqrtCB.setModel(defaultComboBoxModel2);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        flagPanel.add(negSqrtCB, gbc);
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        topPanel.add(buttonPanel, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        nextButton = new JButton();
        nextButton.setText("Next");
        buttonPanel.add(nextButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        backButton = new JButton();
        backButton.setText("Back");
        buttonPanel.add(backButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        clearButton = new JButton();
        clearButton.setText("Clear");
        buttonPanel.add(clearButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        findPanel = new JPanel();
        findPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        topPanel.add(findPanel, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        findButton = new JButton();
        findButton.setLabel("Find");
        findButton.setText("Find");
        findPanel.add(findButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator1 = new JSeparator();
        separator1.setOpaque(true);
        separator1.setOrientation(1);
        topPanel.add(separator1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        topPanel.add(spacer1, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        splitPane1.setRightComponent(bottomPanel);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

}
