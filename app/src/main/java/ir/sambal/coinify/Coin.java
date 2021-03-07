package ir.sambal.coinify;


import java.util.ArrayList;
import java.util.Date;
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

    private List<Candle> candles;
    private double marketCap;
    private Date lastUpdated;

    public Coin(int id, String name, String symbol, int price, int percentChange1h, int percentChange24h, int percentChange7d, double marketCap, Date lastUpdated) {
        this.marketCap = marketCap;
        this.lastUpdated = lastUpdated;
        this.setId(id);
        this.setName(name);
        this.setSymbol(symbol);
        this.setPrice(price);
        this.setPercentChange1h(percentChange1h);
        this.setPercentChange24h(percentChange24h);
        this.setPercentChange7d(percentChange7d);
        candles = new ArrayList<>();
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

    public double getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(double marketCap) {
        this.marketCap = marketCap;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public List<Candle> getCandles() {
        return candles;
    }

    public void setCandles(List<Candle> candles) {
        this.candles = candles;
    }


    public List<Candle> getCandles(CandleRequest.Range range) {
        switch (range) {
            case weekly:
                return candles.subList(0, Math.max(0, Math.min(7, candles.size())));
            case oneMonth:
                return candles.subList(0, Math.max(0, Math.min(30, candles.size())));
        }
        return null;
    }
}
