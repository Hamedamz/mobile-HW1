package ir.sambal.coinify.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CoinDao {
    @Query("SELECT * FROM coins order by `marketCap` desc limit :range offset :start")
    List<CoinEntity> getAll(int start, int range);

    @Query("SELECT * FROM coins WHERE id = :id limit 1")
    CoinEntity loadById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(CoinEntity... coins);

    @Delete
    void delete(CoinEntity coin);

    @Query("UPDATE coins set `imageURL`=:imageURL where id=:id")
    void updateImage(int id, String imageURL);
}
