package com.oucs.mystores.common

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.oucs.mystores.room.StoreDB

class StoreApplication:Application() {
    companion object{
        lateinit var db: StoreDB
    }

    override fun onCreate() {
        super.onCreate()
        //how to make the migration of version db
        val migration1to2 = object : Migration(1,2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE StoreEntity ADD COLUMN imageURL TEXT NOT NULL DEFAULT ''")
            }
        }
        db = Room
            .databaseBuilder(this, StoreDB::class.java, "StoreDB")
            .addMigrations(migration1to2)
            .build()
    }
}