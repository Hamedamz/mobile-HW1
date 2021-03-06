package ir.sambal.coinify;

import java.util.Date;

public class Candle {
    private Date startDate;
    private double priceHigh;
    private double priceLow;
    private double priceOpen;
    private double priceClose;

    public Candle(Date startDate, double priceHigh, double priceLow, double priceOpen, double priceClose) {
        this.startDate = startDate;
        this.priceHigh = priceHigh;
        this.priceLow = priceLow;
        this.priceOpen = priceOpen;
        this.priceClose = priceClose;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public double getPriceHigh() {
        return priceHigh;
    }

    public void setPriceHigh(double priceHigh) {
        this.priceHigh = priceHigh;
    }

    public double getPriceLow() {
        return priceLow;
    }

    public void setPriceLow(double priceLow) {
        this.priceLow = priceLow;
    }

    public double getPriceOpen() {
        return priceOpen;
    }

    public void setPriceOpen(double priceOpen) {
        this.priceOpen = priceOpen;
    }

    public double getPriceClose() {
        return priceClose;
    }

    public void setPriceClose(double priceClose) {
        this.priceClose = priceClose;
    }
}
