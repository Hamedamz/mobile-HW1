package ir.sambal.coinify;

public class Coin {
    public String name;
    public String symbol;
    public int price;
    public int percent_change_1h;
    public int percent_change_24h;
    public int percent_change_7d;
    public String imageURL;

    public Coin(String name, String symbol, int price, int percent_change_1h, int percent_change_24h, int percent_change_7d) {
        this.name = name;
        this.symbol = symbol;
        this.price = price;
        this.percent_change_1h = percent_change_1h;
        this.percent_change_24h = percent_change_24h;
        this.percent_change_7d = percent_change_7d;
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
}
