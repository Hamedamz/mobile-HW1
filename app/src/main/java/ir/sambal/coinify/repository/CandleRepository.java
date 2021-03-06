package ir.sambal.coinify.repository;


import java.util.List;

import ir.sambal.coinify.Candle;
import ir.sambal.coinify.Coin;
import ir.sambal.coinify.TimestampUtils;
import ir.sambal.coinify.db.CandleDao;
import ir.sambal.coinify.db.CandleEntity;
import ir.sambal.coinify.network.CandleRequest;

public class CandleRepository {
    private final CandleDao db;
    private final CandleRequest network;

    public CandleRepository(CandleDao db, CandleRequest network) {
        this.db = db;
        this.network = network;
    }

    public void getCandles(Coin coin, CandlesResponseCallback callback) {
        Thread cacheThread = new Thread(() -> {
            List<CandleEntity> candleEntities = db.loadByCoin(coin.getId());
            Candle[] candles = new Candle[candleEntities.size()];

            for (int i = 0; i < candles.length; i++) {
                CandleEntity candleEntity = candleEntities.get(i);
                candles[i] = new Candle(candleEntity.startDate, candleEntity.priceHigh, candleEntity.priceLow, candleEntity.priceOpen, candleEntity.priceClose);
            }
            callback.setCandles(candles);
            if (candles.length < 30 || candles[30 - 1].getStartDate().before(TimestampUtils.daysBeforeNow(29))) {
                fetchFreshCandles(coin, callback);
            }
        });
        cacheThread.start();
    }

    public void fetchFreshCandles(Coin coin, CandlesResponseCallback callback) {
        Thread networkThread = new Thread(() -> {
            network.getCandles(coin, (candles) -> {
                CandleEntity[] candleEntities = new CandleEntity[candles.length];
                for (int i = 0; i < candles.length; i++) {
                    candleEntities[i] = new CandleEntity();
                    candleEntities[i].coinId = coin.getId();
                    candleEntities[i].startDate = candles[i].getStartDate();
                    candleEntities[i].priceClose = candles[i].getPriceClose();
                    candleEntities[i].priceOpen = candles[i].getPriceOpen();
                    candleEntities[i].priceLow = candles[i].getPriceLow();
                    candleEntities[i].priceHigh = candles[i].getPriceHigh();
                }
                db.insertAll(candleEntities);
                db.deleteOldCandles(coin.getId(), TimestampUtils.daysBeforeNow(30));
                callback.setCandles(candles);
            });
        });
        networkThread.start();
    }

    public interface CandlesResponseCallback {
        void setCandles(Candle... candles);
    }
}
