package org.example;

public class PresetData {
    String ratio;
    double doubleNum;
    int intNum;

    public PresetData(String ratio, double doubleNum, int intNum) {
        this.ratio = ratio;
        this.doubleNum = doubleNum;
        this.intNum = intNum;
    }

    public String getRatio() {
        return this.ratio;
    }

    public int getInt() {
        return this.intNum;
    }
}
