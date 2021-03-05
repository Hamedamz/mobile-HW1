package ir.sambal.coinify.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CoinDao {
    @Query("SELECT * FROM coins where `index` >= :start and `index` < :end order by `index`")
    List<CoinEntity> getAll(int start, int end);

    @Query("SELECT * FROM coins WHERE id = :id")
    List<CoinEntity> loadById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(CoinEntity... coins);

    @Delete
    void delete(CoinEntity coin);
}
