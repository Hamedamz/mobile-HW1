package ir.sambal.coinify.repository;


import android.os.Handler;

import java.util.List;

import ir.sambal.coinify.Candle;
import ir.sambal.coinify.Coin;
import ir.sambal.coinify.TimestampUtils;
import ir.sambal.coinify.db.CandleDao;
import ir.sambal.coinify.db.CandleEntity;
import ir.sambal.coinify.network.CandleRequest;
import ir.sambal.coinify.thread.ThreadPoolManager;

public class CandleRepository {
    private final CandleDao db;
    private final CandleRequest network;

    private final ThreadPoolManager threadPoolManager;
    private final Handler resultHandler;

    public CandleRepository(CandleDao db, CandleRequest network, ThreadPoolManager threadPoolManager, Handler resultHandler) {
        this.db = db;
        this.network = network;
        this.threadPoolManager = threadPoolManager;
        this.resultHandler = resultHandler;
    }

    public void getCandles(Coin coin, CandlesResponseCallback callback) {
        threadPoolManager.addRunnable(() -> {
            List<CandleEntity> candleEntities = db.loadByCoin(coin.getId());
            Candle[] candles = new Candle[candleEntities.size()];

            for (int i = 0; i < candles.length; i++) {
                CandleEntity candleEntity = candleEntities.get(i);
                candles[i] = new Candle(candleEntity.startDate, candleEntity.priceHigh, candleEntity.priceLow, candleEntity.priceOpen, candleEntity.priceClose);
            }
            boolean needNetwork = candles.length < 30 || candles[0].getStartDate().before(TimestampUtils.daysBeforeNow(1));

            resultHandler.post(() -> callback.setCandles(candles, !needNetwork));
            if (needNetwork) {
                fetchFreshCandles(coin, callback);
            }
        });
    }

    public void fetchFreshCandles(Coin coin, CandlesResponseCallback callback) {
        threadPoolManager.addRunnable(() -> network.getCandles(coin, new CandleRequest.CandlesLoadedCallback() {
            @Override
            public void success(Candle... candles) {
                if (candles.length == 0) {
                    callback.error(CandlesResponseCallback.NO_CANDLES_FOUND);
                    return;
                }
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
                resultHandler.post(() -> callback.setCandles(candles, true));
            }

            @Override
            public void error(CandleRequest.RequestError error) {
                int errorNumber = GlobalRepositoryErrors.UNKNOWN;
                switch (error) {
                    case CONNECTION_ERROR:
                        errorNumber = GlobalRepositoryErrors.CONNECTION_ERROR;
                        break;
                    case SERVER_ERROR:
                        errorNumber = GlobalRepositoryErrors.SERVER_ERROR;
                        break;
                }
                int finalErrorNumber = errorNumber;
                resultHandler.post(() -> callback.error(finalErrorNumber));
            }
        }));
    }

    public interface CandlesResponseCallback {
        int NO_CANDLES_FOUND = -1;

        void setCandles(Candle[] candles, boolean isFinal);

        default void error(int errorNumber) {
        }
    }
}
