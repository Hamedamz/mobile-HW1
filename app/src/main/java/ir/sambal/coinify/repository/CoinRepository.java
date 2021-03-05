package ir.sambal.coinify.repository;

import androidx.lifecycle.ViewModel;

import java.util.List;

import ir.sambal.coinify.Coin;
import ir.sambal.coinify.db.CoinEntity;
import ir.sambal.coinify.db.CoinDao;
import ir.sambal.coinify.network.CoinRequest;

public class CoinRepository extends ViewModel {
    private final CoinDao db;
    private final CoinRequest network;

    public CoinRepository(CoinDao db, CoinRequest network) {
        this.db = db;
        this.network = network;
    }

    public void getCoins(int start, int limit, CoinsResponseCallback callback) {
        Thread cacheThread = new Thread(() -> {
            List<CoinEntity> coinEntities = db.getAll(start, start + limit);
            Coin[] coins = new Coin[coinEntities.size()];
            for (int i = 0; i < coinEntities.size(); i++) {
                CoinEntity coinEntity = coinEntities.get(i);
                Coin coin = new Coin(coinEntity.id, coinEntity.name, coinEntity.symbol,
                        coinEntity.price, coinEntity.percentChange1h, coinEntity.percentChange24h,
                        coinEntity.percentChange7d);
                coins[i] = coin;
            }
            callback.addCoins(coins);
        });
        cacheThread.start();
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
                    coinEntity.index = start + i;
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
}
