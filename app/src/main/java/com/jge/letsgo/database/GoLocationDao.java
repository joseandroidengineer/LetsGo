package com.jge.letsgo.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.jge.letsgo.models.GoLocation;

import java.util.List;

@Dao
public interface GoLocationDao {
    @Query("SELECT * from goLocation ORDER BY id")
    LiveData<List<GoLocation>> loadAllLocations();

    @Insert
    void insertGoLocation(GoLocation goLocation);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(GoLocation goLocation);

    @Delete
    void deleteGoLocation(GoLocation goLocation);

    @Query("SELECT * FROM goLocation WHERE id = :id")
    LiveData<GoLocation> loadGoLocationById(int id);
}
