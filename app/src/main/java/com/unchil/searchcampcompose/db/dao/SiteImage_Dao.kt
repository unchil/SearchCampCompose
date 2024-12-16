package com.unchil.searchcampcompose.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.unchil.searchcampcompose.db.entity.SiteImage_TBL
import kotlinx.coroutines.flow.Flow


@Dao
interface SiteImage_Dao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(it: SiteImage_TBL)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert_List(it: List<SiteImage_TBL>)

    @Query("SELECT * FROM SiteImage_TBL ORDER BY  serialnum")
    fun select_All_Flow(): Flow<List<SiteImage_TBL>>

    @Query("SELECT * FROM SiteImage_TBL WHERE  contentId =:contentId ORDER BY  serialnum")
    fun select_Flow(contentId:String): Flow<List<SiteImage_TBL>>


    @Query("DELETE FROM SiteImage_TBL")
    suspend fun trancate()

}