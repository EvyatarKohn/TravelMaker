package com.evya.myweatherapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.evya.myweatherapp.model.weathermodel.Weather

@Database(entities = [Weather::class], version = 1, exportSchema = false)
@TypeConverters(DataConverter::class)
abstract class FavoritesDB : RoomDatabase() {

    abstract fun attractionsDao(): FavoritesDao

    companion object {
        @Volatile
        private var INSTANCE: FavoritesDB? = null

        fun getDB(context: Context): FavoritesDB {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE =
                        Room.databaseBuilder(
                            context,
                            FavoritesDB::class.java,
                            "favorites.db"
                        ).build()
                }
            }
            return INSTANCE!!
        }
    }
}