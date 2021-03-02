package pl.covid19.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.covid19.repository.CovidRepository

@Database(entities = [AreaDB::class, GOVPLXDB::class, FazyDB::class, VersionDB::class], version = 3, exportSchema = true)
abstract class CovidDatabase : RoomDatabase() {

    abstract val covidDao: DatabaseDao


    companion object {
        @Volatile
        private var INSTANCE: pl.covid19.database.CovidDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
             val createSql ="CREATE TABLE IF NOT EXISTS `version` (`typ` TEXT NOT NULL, `version` TEXT NOT NULL, `decription` TEXT NOT NULL, `dateod` TEXT NOT NULL, PRIMARY KEY(`typ`, `dateod`))"
                database.execSQL(createSql)
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                val createSql ="DELETE FROM govplx"
                database.execSQL(createSql)
            }
        }

        fun getInstance(context: Context): pl.covid19.database.CovidDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext,CovidDatabase::class.java,"covidDatabase")
                            // https://medium.com/androiddevelopers/understanding-migrations-with-room-f01e04b07929
                            //.fallbackToDestructiveMigration()
                            .addMigrations(MIGRATION_1_2)
                            .addMigrations(MIGRATION_2_3)
                            .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

}
