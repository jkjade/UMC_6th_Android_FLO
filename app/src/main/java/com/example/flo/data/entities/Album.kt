package com.example.flo.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "AlbumTable")
data class Album(
    // 강의에선 albumId가 id로 나옴 주의!
    @PrimaryKey(autoGenerate = false) var id: Int = 0,
    var title: String? = "",
    var singer: String? = "",
    var coverImg: Int? = null
)
