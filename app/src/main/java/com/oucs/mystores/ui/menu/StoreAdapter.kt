package com.oucs.mystores.ui.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.oucs.mystores.R
import com.oucs.mystores.databinding.ItemStoreBinding
import com.oucs.mystores.model.Store

class StoreAdapter(
    private var storeList: MutableList<Store>
    ,private val onClickItem: OnClickItem
): RecyclerView.Adapter<StoreAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_store,parent,false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val store=storeList[position]
        with(holder){
            binding.storeName.text = store.name
            binding.isMyFavorite.isChecked = store.favorite
            binding.imagePhoto.load(store.imageURL){
                crossfade(false)
                placeholder(R.drawable.ic_store)
            }
        }
    }
    override fun getItemCount(): Int {
        return storeList.size
    }

    fun deleteStore(store: Store) {
        val index = storeList.indexOf(store)
        if (index!=-1){
            storeList.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view)
        ,View.OnClickListener,View.OnLongClickListener{
        val binding = ItemStoreBinding.bind(view)
        init {
            view.setOnClickListener(this)
            binding.isMyFavorite.setOnClickListener {
                val favorite = binding.isMyFavorite.isChecked
                storeList[adapterPosition].favorite=favorite
                onClickItem.onClickMyFavoriteStore(storeList[adapterPosition])
            }
            view.setOnLongClickListener(this)
        }
        override fun onClick(v: View?) {
            onClickItem.onClickStore(storeList[adapterPosition].id)
        }

        override fun onLongClick(v: View?): Boolean {
            onClickItem.onDoubleClickStore(storeList[adapterPosition])
            return true
        }
    }
    interface OnClickItem{
        fun onClickStore(storeId:Long)
        fun onClickMyFavoriteStore(store: Store)
        fun onDoubleClickStore(store: Store)
    }
}