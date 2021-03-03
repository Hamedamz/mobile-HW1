package ir.sambal.coinify;

import java.util.ArrayList;
import java.util.List;

public class Coin {
    private String name;
    private String symbol;
    private int price;
    private int percent_change_1h;
    private int percent_change_24h;
    private int percent_change_7d;
    private String imageURL;

    private List<Candle> weekCandles;
    private List<Candle> monthCandles;

    public Coin(String name, String symbol, int price, int percent_change_1h, int percent_change_24h, int percent_change_7d) {
        this.setName(name);
        this.setSymbol(symbol);
        this.setPrice(price);
        this.setPercent_change_1h(percent_change_1h);
        this.setPercent_change_24h(percent_change_24h);
        this.setPercent_change_7d(percent_change_7d);
        weekCandles = new ArrayList<>();
        monthCandles = new ArrayList<>();
    }

    public void setImageURL(String url) {
        this.imageURL = url;
    }

    public String getName() {
        return this.name;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPercent_change_1h() {
        return percent_change_1h;
    }

    public void setPercent_change_1h(int percent_change_1h) {
        this.percent_change_1h = percent_change_1h;
    }

    public int getPercent_change_24h() {
        return percent_change_24h;
    }

    public void setPercent_change_24h(int percent_change_24h) {
        this.percent_change_24h = percent_change_24h;
    }

    public int getPercent_change_7d() {
        return percent_change_7d;
    }

    public void setPercent_change_7d(int percent_change_7d) {
        this.percent_change_7d = percent_change_7d;
    }

    public String getImageURL() {
        return imageURL;
    }

    public List<Candle> getWeekCandles() {
        return weekCandles;
    }

    public void setWeekCandles(List<Candle> weekCandles) {
        this.weekCandles = weekCandles;
    }

    public List<Candle> getMonthCandles() {
        return monthCandles;
    }

    public void setMonthCandles(List<Candle> monthCandles) {
        this.monthCandles = monthCandles;
    }
}
