package ir.sambal.coinify.repository;


import java.util.Date;
import java.util.List;
import java.util.Map;

import ir.sambal.coinify.Coin;
import ir.sambal.coinify.TimestampUtils;
import ir.sambal.coinify.db.CoinEntity;
import ir.sambal.coinify.db.CoinDao;
import ir.sambal.coinify.network.CoinRequest;

public class CoinRepository {
    private final CoinDao db;
    private final CoinRequest network;

    public CoinRepository(CoinDao db, CoinRequest network) {
        this.db = db;
        this.network = network;
    }

    private Coin coinEntityToCoin(CoinEntity coinEntity) {
        return new Coin(coinEntity.id, coinEntity.name, coinEntity.symbol,
                coinEntity.price, coinEntity.percentChange1h, coinEntity.percentChange24h,
                coinEntity.percentChange7d, coinEntity.marketCap, coinEntity.updatedAt);
    }

    // We have coin in db
    public void getCoin(int coinId, CoinResponseCallback callback) {
        Thread cacheThread = new Thread(() -> {
            CoinEntity coinEntity = db.loadById(coinId);
            Coin coin = coinEntityToCoin(coinEntity);
            callback.success(coin);
        });
        cacheThread.start();
    }


    public void getCoins(int start, int limit, CoinsResponseCallback callback) {
        Thread cacheThread = new Thread(() -> {
            List<CoinEntity> coinEntities = db.getAll(start - 1, limit);
            boolean needNetwork = coinEntities.size() < limit;
            Date invalidationDate = TimestampUtils.daysBeforeNow(1);

            Coin[] coins = new Coin[coinEntities.size()];
            for (int i = 0; i < coinEntities.size(); i++) {
                CoinEntity coinEntity = coinEntities.get(i);
                Coin coin = coinEntityToCoin(coinEntity);
                coins[i] = coin;
                if (coinEntity.updatedAt.before(invalidationDate)) {
                    needNetwork = true;
                }
            }
            if (needNetwork) {
                loadFreshCoins(start, limit, callback);
            }
            callback.addCoins(coins);
        });
        cacheThread.start();
    }

    public void fetchCoinImageURL(Coin coin, ImageURLResponseCallback callback) {
        if (coin.getImageURL() == null || !coin.getImageURL().isEmpty()) {
            return;
        }
        new Thread(() -> {
            network.requestCoinDetails(coin, new CoinRequest.CoinDetailsResponseCallback() {
                @Override
                public void onSuccess(Map<String, Object> details) {
                    db.updateImage(coin.getId(), (String) details.get("logoURL"));
                    callback.getImageURL((String) details.get("logoURL"));
                }

                @Override
                public void onError() {

                }
            });
        }).start();
    }

    public void loadFreshCoins(int start, int limit, CoinsResponseCallback callback) {
        Thread networkThread = new Thread(() -> network.requestCoinData(start, limit, new CoinRequest.CoinsResponseCallback() {
            @Override
            public void onSuccess(Coin... coins) {
                CoinEntity[] coinEntities = new CoinEntity[coins.length];
                for (int i = 0; i < coins.length; i++) {
                    // TODO: kasif
                    CoinEntity coinEntity = new CoinEntity();
                    Coin coin = coins[i];
                    coinEntity.id = coin.getId();
                    coinEntity.imageURL = coin.getImageURL();
                    coinEntity.name = coin.getName();
                    coinEntity.price = coin.getPrice();
                    coinEntity.percentChange1h = coin.getPercentChange1h();
                    coinEntity.percentChange7d = coin.getPercentChange7d();
                    coinEntity.percentChange24h = coin.getPercentChange24h();
                    coinEntity.symbol = coin.getSymbol();
                    coinEntity.marketCap = coin.getMarketCap();
                    coinEntity.updatedAt = coin.getLastUpdated();
                    coinEntities[i] = coinEntity;
                }
                db.insertAll(coinEntities);
                callback.addCoins(coins);
            }

            @Override
            public void onError() {
                // nothing or retry?!
            }
        }));
        networkThread.start();
    }

    public interface CoinsResponseCallback {
        void addCoins(Coin... coins);
    }

    public interface CoinResponseCallback {
        void success(Coin coin);
    }

    public interface ImageURLResponseCallback {
        void getImageURL(String imageURL);
    }
}
