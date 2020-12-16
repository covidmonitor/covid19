package pl.covid19.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AreaDB::class, GOVPLXDB::class, FazyDB::class], version = 1, exportSchema = false)
abstract class CovidDatabase : RoomDatabase() {

    abstract val covidDao: DatabaseDao


    companion object {
        @Volatile
        private var INSTANCE: pl.covid19.database.CovidDatabase? = null

        fun getInstance(context: Context): pl.covid19.database.CovidDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                        CovidDatabase::class.java,
                            "covidDatabase"
                    )
                            // https://medium.com/androiddevelopers/understanding-migrations-with-room-f01e04b07929
                            .fallbackToDestructiveMigration()
                            .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
