package com.oucs.mystores.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
//import com.android.volley.Request
//import com.android.volley.toolbox.JsonObjectRequest
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
import com.oucs.mystores.common.StoreApplication
import com.oucs.mystores.common.StoreExceptions
import com.oucs.mystores.common.TypeError
import com.oucs.mystores.model.entities.Store
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//import com.oucs.mystores.repo.volley.Common

object StoreRepository {
    val stores:LiveData<MutableList<Store>> = liveData {
        val storesLiveData = StoreApplication.db.storeDao().getStoreList()
        emitSource(storesLiveData.map { store ->
            store.sortedBy {
                it.name
            }.toMutableList()
        })
    }
    suspend fun addStore(store: Store) = withContext(Dispatchers.IO){
        StoreApplication.db.storeDao().addStore(store)
    }

    suspend fun deleteStore(store: Store) = withContext(Dispatchers.IO){
        val result = StoreApplication.db.storeDao().deleteStore(store)
        errorResult(result,TypeError.DELETE)
    }

    suspend fun updateStore(store: Store) = withContext(Dispatchers.IO){
        val result = StoreApplication.db.storeDao().updateStore(store)
        errorResult(result,TypeError.UPDATE)
    }

    fun getStore(id:Long):LiveData<Store>{
        val store:LiveData<Store> = liveData {
            emitSource(StoreApplication.db.storeDao().getStoreById(id))
        }
        return store
    }
    private fun errorResult(result:Int, type:TypeError){
        if (result == 0) throw StoreExceptions(type)
    }
}