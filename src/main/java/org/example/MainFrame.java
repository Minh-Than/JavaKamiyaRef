package org.example;

import jakarta.inject.Inject;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class MainFrame extends JFrame {
    public String[] negIntFlags = {"None", "Allow -int on either axis", "No -int"};
    public String[] negSqrtFlags = {"None", "Allow -√2 on either axis", "No -√2"};
    public JComboBox<String> negIntCB = new JComboBox<>(negIntFlags);
    public JComboBox<String> negSqrtCB = new JComboBox<>(negSqrtFlags);
    public Map<Double, PresetData> presetDict = new Hashtable<>();
    public ArrayList<ResultData> resultData;
    private static int num;

    @Inject
    ApplicationModel applicationModel = new ApplicationModel();

    public MainFrame(){
        initializePresetData(presetDict);

        JFrame frame = new JFrame();
        frame.setPreferredSize(new Dimension(775, 650));
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new GridLayout());
        frame.setTitle("Kamiya Reference Finder");
        frame.setResizable(false);

        JSplitPane splitPane = new JSplitPane();
        JPanel topPanel = new JPanel();
        JPanel bottomPanel = new DrawPanel(applicationModel);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        JTextField sqrIntText = new JTextField();
        JTextField sqrSqrtText = new JTextField();
        JTextField prefIntText = new JTextField();
        JTextField prefSqrtText = new JTextField();

        JPanel flagPanel = new JPanel(new GridBagLayout());

        JPanel buttonPanel = new JPanel();
        JButton findButton = new JButton("Find");
        JButton nextButton = new JButton("Next");
        JButton backButton = new JButton("Previous");

        frame.getContentPane().add(splitPane);

        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);  // we want it to split the window vertically
        splitPane.setDividerLocation(-1);                     // the initial position of the divider is 200 (our window is 400 pixels high)
        splitPane.setTopComponent(topPanel);                  // at the top we want our "topPanel"
        splitPane.setBottomComponent(bottomPanel);            // and at the bottom we want our "bottomPanel"
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(inputPanel);
        topPanel.add(flagPanel);
        topPanel.add(buttonPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill=GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Square Length:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        inputPanel.add(sqrIntText, gbc);
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx=0;
        inputPanel.add(new JLabel("+"), gbc);
        gbc.gridx = 3; gbc.gridy = 0;gbc.weightx=1.;
        inputPanel.add(sqrSqrtText, gbc);
        gbc.gridx = 4; gbc.gridy = 0;
        inputPanel.add(new JLabel("√2"), gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Preferred ratio (optional):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        inputPanel.add(prefIntText, gbc);
        gbc.gridx = 2; gbc.gridy = 1;gbc.weightx=0;
        inputPanel.add(new JLabel("+"), gbc);
        gbc.gridx = 3; gbc.gridy = 1;
        inputPanel.add(prefSqrtText, gbc);
        gbc.gridx = 4; gbc.gridy = 1;
        inputPanel.add(new JLabel("√2"), gbc);

        gbc.gridx = 0; gbc.gridy = 0;
        flagPanel.add(new JLabel("Int flag:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        flagPanel.add(negIntCB, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        flagPanel.add(new JLabel("√2 flag:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        flagPanel.add(negSqrtCB, gbc);


        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(findButton);
        buttonPanel.add(nextButton);
        nextButton.setEnabled(false);
        buttonPanel.add(backButton);
        backButton.setEnabled(false);

        findButton.addActionListener(e -> {
            int sqrInt;
            int sqrSqrt;
            int prefInt = 0;
            int prefSqrt = 0;

            // No pref
            if (prefIntText.getText().isEmpty() && prefSqrtText.getText().isEmpty()) {
                try {
                    sqrInt = Integer.parseInt(sqrIntText.getText());
                    sqrSqrt = Integer.parseInt(sqrSqrtText.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "The input must only be integer. Please try again.", "Wrong Input Format", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } // Has no input
            else if (sqrIntText.getText().isEmpty() && sqrSqrtText.getText().isEmpty() && prefIntText.getText().isEmpty() && prefSqrtText.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Inputs are empty. Please try again.", "Empty Inputs", JOptionPane.WARNING_MESSAGE);
                return;
            } else { // Has pref
                try {
                    sqrInt = Integer.parseInt(sqrIntText.getText());
                    sqrSqrt = Integer.parseInt(sqrSqrtText.getText());
                    prefInt = Integer.parseInt(prefIntText.getText());
                    prefSqrt = Integer.parseInt(prefSqrtText.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "The input must only be integer. Please try Again.", "Wrong Input Format", JOptionPane.WARNING_MESSAGE);
                    return;
                }
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
            num = 0;

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
                                    if(negIntCB.getSelectedIndex() == 1){
                                        if((ixr1 < 0 || sqrInt - ixr1 < 0) && iyr1 < 0){
                                            continue;
                                        }
                                    }
                                    if(negIntCB.getSelectedIndex() == 2){
                                        if(ixr1 < 0 || sqrInt - ixr1 < 0 || iyr1 < 0){
                                            continue;
                                        }
                                    }

                                    // negative sqrt(2) flags
                                    if(negSqrtCB.getSelectedIndex() == 1){
                                        if((ixr2 < 0 || sqrInt - ixr2 < 0) && iyr2 < 0){
                                            continue;
                                        }
                                    }
                                    if(negSqrtCB.getSelectedIndex() == 2){
                                        if(ixr2 < 0 || sqrInt - ixr2 < 0 || iyr2 < 0){
                                            continue;
                                        }
                                    }
                                    num += 1;
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
            if (num != 0) {
                applicationModel.reset();
                applicationModel.setResultData(resultData);

                nextButton.setEnabled(true);

                bottomPanel.validate();
                bottomPanel.repaint();
            }
        });

        nextButton.addActionListener(e -> {
            backButton.setEnabled(true);
            if(applicationModel.getResultIndex() < applicationModel.getResultData().size() - 1){
                applicationModel.setResultIndex(applicationModel.getResultIndex() + 1);
            }
            if(applicationModel.getResultIndex() == applicationModel.getResultData().size() - 1){
                nextButton.setEnabled(false);
            }
            bottomPanel.validate();
            bottomPanel.repaint();
        });
        backButton.addActionListener(e -> {
            nextButton.setEnabled(true);
            if(applicationModel.getResultIndex() > 0){
                applicationModel.setResultIndex(applicationModel.getResultIndex() - 1);
            }
            if(applicationModel.getResultIndex() == 0){
                backButton.setEnabled(false);
            }
            bottomPanel.validate();
            bottomPanel.repaint();
        });

        frame.setVisible(true);
        frame.pack();
    }

    public static boolean isPowerOfTwo(int n){
        if (n == 0)
            return false;

        double v = Math.log(n) / Math.log(2);
        return (int)(Math.ceil(v))
                == (int)(Math.floor(v));
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static void setIndividualPreset(Map<Double, PresetData> presetDict, double key, String ratio, double doubleNum, int intNum){
        presetDict.put(key, new PresetData(ratio, doubleNum, intNum));
    }

    public static void initializePresetData(Map<Double, PresetData> presetDict){
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

    public static PresetData preset(Map<Double, PresetData> presetDict, double key){
        return presetDict.containsKey(key) ? presetDict.get(key) : new PresetData("NA", 0, 0);
    }

    public static String output(int n1, int n2){
        String result = n2 + "√2";

        if (n2 == 1) { result = "√2"; }
        if (n1 != 0) {
            result = n1 + "+" + result;
        }
        if (n2 == 0){ result = Integer.toString(n1); }

        return result;
    }
}
