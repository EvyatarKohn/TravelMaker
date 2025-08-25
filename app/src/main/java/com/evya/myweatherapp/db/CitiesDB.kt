package com.evya.myweatherapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.evya.myweatherapp.model.weathermodel.Weather

@Database(entities = [Weather::class], version = 10, exportSchema = false)
@TypeConverters(DataConverter::class)
abstract class CitiesDB : RoomDatabase() {

    abstract fun attractionsDao(): CitiesDao

    companion object {
        @Volatile
        private var INSTANCE: CitiesDB? = null

        fun getDB(context: Context): CitiesDB {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE =
                        Room.databaseBuilder(
                            context,
                            CitiesDB::class.java,
                            "cities.db"
                        )
//                            .addMigrations(MIGRATION_FORM_1_TO_2)
                            .fallbackToDestructiveMigration()
                            .build()
                }
            }
            return INSTANCE!!
        }
    }
}