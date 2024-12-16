package com.unchil.searchcampcompose.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.unchil.searchcampcompose.db.entity.CURRENTWEATHER_TBL

import kotlinx.coroutines.flow.Flow


@Dao
interface CurrentWeather_Dao {

    @Query("DELETE FROM CURRENTWEATHER_TBL")
    suspend fun trancate()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(it: CURRENTWEATHER_TBL)


    @Query( "SELECT * FROM CURRENTWEATHER_TBL" )
    fun select_Flow(): Flow<CURRENTWEATHER_TBL>


}