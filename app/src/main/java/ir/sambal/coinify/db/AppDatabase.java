package ir.sambal.coinify.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {CoinEntity.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CoinDao coinDao();

    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null)
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "cache")
                    .fallbackToDestructiveMigration().build();
        return instance;
    }
}
