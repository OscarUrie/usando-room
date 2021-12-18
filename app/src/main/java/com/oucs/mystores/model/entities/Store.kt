package com.oucs.mystores.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "StoreEntity")
data class Store(
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0L,
    var name:String,
    var phone:String? = null,
    var webSite:String? = null,
    var imageURL:String,
    var favorite:Boolean = false
)
/*{
//no es necesario utlizar equals y hashcode cuando se usa ListAdapter
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Store

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

primera version de la tabla
data class Store(
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0L,
    var name:String,
    var phone:String? = null,
    var webSite:String? = null,
    var favorite:Boolean = false
)
* */