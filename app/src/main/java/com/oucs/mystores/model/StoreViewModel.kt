package com.oucs.mystores.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oucs.mystores.common.StoreExceptions
import com.oucs.mystores.common.TypeError
import com.oucs.mystores.model.entities.Store
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class StoreViewModel:ViewModel() {
    private val typeError:MutableLiveData<TypeError> = MutableLiveData()
    private val showProgress:MutableLiveData<Boolean> = MutableLiveData()
    private val storeListLiveData = StoreRepository.stores
    fun getStores():LiveData<MutableList<Store>>{
        return storeListLiveData
    }
    fun deleteStore(store: Store){
        /*viewModelScope.launch {
            StoreRepository.deleteStore(store)
        }*/
        executeAction {
            StoreRepository.deleteStore(store)
        }
    }
    fun updateStore(store: Store){
        /*viewModelScope.launch {
            StoreRepository.updateStore(store)
        }*/
        executeAction {
            StoreRepository.updateStore(store)
        }
    }
    fun getTypeError():MutableLiveData<TypeError> = typeError
    fun getProgress():LiveData<Boolean> = showProgress
    private fun executeAction(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            showProgress.value = true
            try {
                block()
            }
            catch (e:StoreExceptions){
                //e.printStackTrace()
                typeError.value = e.typeError
            }
            finally {
                showProgress.value = false
            }
        }
    }
}