package com.oucs.mystores.repo.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.oucs.mystores.model.entities.Store

@Database(entities = [Store::class],version = 2,exportSchema = false)
abstract class StoreDB: RoomDatabase() {
    abstract fun storeDao(): StoreDao
}