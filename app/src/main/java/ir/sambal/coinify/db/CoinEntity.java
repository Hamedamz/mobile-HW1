package ir.sambal.coinify.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "coins")
public class CoinEntity {
    @PrimaryKey
    public int id;

    public String name;

    public String symbol;
    public int price;
    public int percentChange1h;
    public int percentChange24h;
    public int percentChange7d;
    public String imageURL;

    public int index;
}
