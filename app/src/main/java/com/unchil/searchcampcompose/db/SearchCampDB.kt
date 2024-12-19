package com.unchil.searchcampcompose.db


import android.content.Context
import androidx.compose.runtime.compositionLocalOf
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.unchil.searchcampcompose.db.dao.CampSite_Dao
import com.unchil.searchcampcompose.db.dao.CollectTime_Dao
import com.unchil.searchcampcompose.db.dao.CurrentWeather_Dao
import com.unchil.searchcampcompose.db.dao.NearCampSite_Dao
import com.unchil.searchcampcompose.db.dao.SiDo_Dao
import com.unchil.searchcampcompose.db.dao.SiGunGu_Dao
import com.unchil.searchcampcompose.db.dao.SiteImage_Dao
import com.unchil.searchcampcompose.db.entity.CURRENTWEATHER_TBL
import com.unchil.searchcampcompose.db.entity.CampSite_TBL
import com.unchil.searchcampcompose.db.entity.CollectTime_TBL
import com.unchil.searchcampcompose.db.entity.NearCampSite_TBL
import com.unchil.searchcampcompose.db.entity.SiDo_TBL
import com.unchil.searchcampcompose.db.entity.SiGunGu_TBL
import com.unchil.searchcampcompose.db.entity.SiteImage_TBL
import com.unchil.searchcampcompose.model.CollectTypeList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


val LocalSearchCampDB = compositionLocalOf <SearchCampDB> { error("Not Found  SearchCampDB")  }
@Database(
    entities = [
        SiDo_TBL::class,
        SiGunGu_TBL::class,
        CampSite_TBL::class,
        NearCampSite_TBL::class,
        CollectTime_TBL::class,
        SiteImage_TBL::class,
        CURRENTWEATHER_TBL::class],
    version = SearchCampDB.LATEST_VERSION,
    exportSchema = false,
    
)


abstract class SearchCampDB: RoomDatabase() {

    abstract  val sidoDao: SiDo_Dao
    abstract  val sigunguDao: SiGunGu_Dao
    abstract  val campSiteDao: CampSite_Dao
    abstract  val nearCampSiteDao: NearCampSite_Dao
    abstract  val collectTimeDao: CollectTime_Dao
    abstract  val siteimageDao: SiteImage_Dao
    abstract  val currentWeatherDao:CurrentWeather_Dao

    companion object {

        const val  LATEST_VERSION = 1

        val migration_1_2 = object : Migration(1,2) {
            override fun migrate(database: SupportSQLiteDatabase) {
               // database.execSQL("CREATE TABLE 'CollectTime_TBL' ('collectDataType' String, 'collectTime' Long, " + "PRIMARY KEY('collectDataType'))")
                database.execSQL("INSERT INTO CollectTime_TBL ('collectDataType', 'collectTime')  VALUES ( 'WEATHER', 0)")
            }
        }



        @Volatile
        private var INSTANCE: SearchCampDB? = null


        fun getInstance(context: Context): SearchCampDB =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                 SearchCampDB::class.java,
                "SearchCampDB"
            )
        //        .addMigrations(migration_1_2)
                .allowMainThreadQueries()
                 .fallbackToDestructiveMigration()
                .addCallback(object : Callback(){
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        initData(context.applicationContext)
                    }
                })
                .build()


        private fun initData(context:Context) =   CoroutineScope(Dispatchers.IO).launch {

            val collecttimeTblList = mutableListOf<CollectTime_TBL>()

            CollectTypeList.forEach{
                collecttimeTblList.add(
                    CollectTime_TBL(collectDataType = it.name, collectTime = 0)
                )
            }
            getInstance(context).collectTimeDao.insert_List(collecttimeTblList)
        }



    }

}