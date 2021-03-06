package ir.sambal.coinify.db;

import androidx.room.Entity;
import androidx.room.Index;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

@Entity(tableName = "candles", primaryKeys = {"coinId", "startDate"},
        indices = {@Index(value = {"coinId"})})
public class CandleEntity {
    public int coinId;
    @NotNull
    public Date startDate = new Date();
    public double priceHigh;
    public double priceLow;
    public double priceOpen;
    public double priceClose;
}
