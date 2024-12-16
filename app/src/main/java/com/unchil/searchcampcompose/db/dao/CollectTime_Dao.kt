package com.unchil.searchcampcompose.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.unchil.searchcampcompose.db.entity.CollectTime_TBL


@Dao
interface CollectTime_Dao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(it: CollectTime_TBL)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert_List(it: List<CollectTime_TBL>)

    @Query("SELECT collectTime FROM CollectTime_TBL WHERE  collectDataType = :type  LIMIT 1")
    fun select(type:String): Long

    @Query("UPDATE CollectTime_TBL  SET collectTime = :time WHERE collectDataType = :type")
    suspend fun update(type:String, time:Long)

    @Query("DELETE FROM CollectTime_TBL")
    suspend fun trancate()
}