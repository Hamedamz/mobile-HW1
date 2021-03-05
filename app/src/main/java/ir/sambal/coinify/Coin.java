package ir.sambal.coinify;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import ir.sambal.coinify.network.CandleRequest;

public class Coin {
    private int id;
    private String name;
    private String symbol;
    private int price;
    private int percentChange1h;
    private int percentChange24h;
    private int percentChange7d;
    private String imageURL;

    private List<Candle> weekCandles;
    private List<Candle> monthCandles;

    public Coin(int id, String name, String symbol, int price, int percentChange1h, int percentChange24h, int percentChange7d) {
        this.setId(id);
        this.setName(name);
        this.setSymbol(symbol);
        this.setPrice(price);
        this.setPercentChange1h(percentChange1h);
        this.setPercentChange24h(percentChange24h);
        this.setPercentChange7d(percentChange7d);
        weekCandles = new ArrayList<>();
        monthCandles = new ArrayList<>();
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
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

    public int getPercentChange1h() {
        return percentChange1h;
    }

    public void setPercentChange1h(int percentChange1h) {
        this.percentChange1h = percentChange1h;
    }

    public int getPercentChange24h() {
        return percentChange24h;
    }

    public void setPercentChange24h(int percentChange24h) {
        this.percentChange24h = percentChange24h;
    }

    public int getPercentChange7d() {
        return percentChange7d;
    }

    public void setPercentChange7d(int percentChange7d) {
        this.percentChange7d = percentChange7d;
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

    public List<Candle> getCandles(CandleRequest.Range range) {
        switch (range) {
            case weekly:
                return weekCandles;
            case oneMonth:
                return monthCandles;
        }
        return null;
    }
}
