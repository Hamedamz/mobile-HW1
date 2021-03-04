package ir.sambal.coinify;

public class Candle {
    private double priceHigh;
    private double priceLow;
    private double priceOpen;
    private double priceClose;

    public double getPriceHigh() {
        return priceHigh;
    }

    public Candle(double priceHigh, double priceLow, double priceOpen, double priceClose) {
        this.priceHigh = priceHigh;
        this.priceLow = priceLow;
        this.priceOpen = priceOpen;
        this.priceClose = priceClose;
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
