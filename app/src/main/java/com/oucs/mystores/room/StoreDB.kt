package com.oucs.mystores.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.oucs.mystores.model.Store

@Database(entities = [Store::class],version = 2,exportSchema = false)
abstract class StoreDB: RoomDatabase() {
    abstract fun storeDao():StoreDao
}