package com.oucs.mystores.repo.volley

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class StoreApi(context: Context) {
    companion object{
        @Volatile
        private var instance:StoreApi? = null
        fun getInstance(context: Context) = instance?: synchronized(this){
            instance?:StoreApi(context).also { instance = it }
        }
    }
    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }
    fun <T> addRequestQueue(request:Request<T>){
        requestQueue.add(request)
    }
}