package com.oucs.mystores.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oucs.mystores.model.entities.Store
import kotlinx.coroutines.launch

class EditStoreViewModel: ViewModel() {
    fun saveStore(id: Long,store: Store){
        viewModelScope.launch {
            if (id!=-1L){
                store.id = id
                StoreRepository.updateStore(store)
            }
            else{
                StoreRepository.addStore(store)
            }
        }
    }
    fun getStore(id:Long):LiveData<Store>{
        return StoreRepository.getStore(id)
    }
}