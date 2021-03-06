package com.oucs.mystores.ui.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.oucs.mystores.R
import com.oucs.mystores.databinding.ItemStoreBinding
import com.oucs.mystores.model.entities.Store

class StoreAdapter(
    private val onClickItem: OnClickItem): ListAdapter<Store,RecyclerView.ViewHolder>(
        StoreDiffCallback()
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_store,parent,false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val store=getItem(position)
        with(holder as ViewHolder){
            binding.storeName.text = store.name
            binding.isMyFavorite.isChecked = store.favorite
            binding.imagePhoto.load(store.imageURL){
                crossfade(false)
                placeholder(R.drawable.ic_store)
            }
        }
    }
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view)
        ,View.OnClickListener,View.OnLongClickListener{
        val binding = ItemStoreBinding.bind(view)
        init {
            view.setOnClickListener(this)
            binding.isMyFavorite.setOnClickListener {
                val favorite = binding.isMyFavorite.isChecked
                getItem(adapterPosition).favorite=favorite
                onClickItem.onClickMyFavoriteStore(getItem(adapterPosition))
            }
            view.setOnLongClickListener(this)
        }
        override fun onClick(v: View?) {
            onClickItem.onClickStore(getItem(adapterPosition).id)
        }

        override fun onLongClick(v: View?): Boolean {
            onClickItem.onDoubleClickStore(getItem(adapterPosition))
            return true
        }
    }
    interface OnClickItem{
        fun onClickStore(storeId:Long)
        fun onClickMyFavoriteStore(store: Store)
        fun onDoubleClickStore(store: Store)
    }
    class StoreDiffCallback:DiffUtil.ItemCallback<Store>(){
        override fun areItemsTheSame(oldItem: Store, newItem: Store): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Store, newItem: Store): Boolean {
            return oldItem.id == newItem.id
        }
    }
}