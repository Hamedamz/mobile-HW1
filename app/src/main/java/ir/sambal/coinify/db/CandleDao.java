package ir.sambal.coinify.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.Date;
import java.util.List;

@Dao
public interface CandleDao {
    @Query("SELECT * FROM candles WHERE coinId = :coinId order by startDate DESC")
    List<CandleEntity> loadByCoin(int coinId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(CandleEntity... candles);

    @Query("DELETE FROM candles WHERE coinId = :coinId and startDate < :date")
    void deleteOldCandles(int coinId, Date date);
}
