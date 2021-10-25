package com.oucs.mystores.room

import androidx.room.*
import com.oucs.mystores.model.Store

@Dao
interface StoreDao {
    @Query("SELECT * FROM StoreEntity")
    fun getStoreList():MutableList<Store>
    @Query("SELECT * FROM StoreEntity WHERE id = :id")
    fun getStoreById(id:Long):Store
    @Insert
    fun addStore(store: Store)
    @Update
    fun updateStore(store: Store)
    @Delete
    fun deleteStore(store: Store)
}