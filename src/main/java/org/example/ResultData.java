package org.example;

public class ResultData {
    private double x;
    private double y;
    private String x1;
    private String x2;
    private String ystr;
    private String ratio1;
    private String ratio2;
    private String png1;
    private String png2;
    private int order;

    public ResultData(double x, double y, String x1, String x2, String ystr, String ratio1, String ratio2, String png1, String png2, int order) {
        this.x = x;
        this.y = y;
        this.x1 = x1;
        this.x2 = x2;
        this.ystr = ystr;
        this.ratio1 = ratio1;
        this.ratio2 = ratio2;
        this.png1 = png1;
        this.png2 = png2;
        this.order = order;
    }

    public double getx() {
        return this.x;
    }

    public double gety() {
        return this.y;
    }

    public String getx1() {
        return this.x1;
    }

    public String getx2() {
        return this.x2;
    }

    public String getystr() {
        return this.ystr;
    }

    public String getRatio1() {
        return this.ratio1;
    }

    public String getRatio2() {
        return this.ratio2;
    }

    public String getPng1() {
        return this.png1;
    }

    public String getPng2() {
        return this.png2;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }
}
