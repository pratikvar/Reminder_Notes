package com.skybase.remindernotes.repository.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id") var Id: Int?,
    @ColumnInfo(name = "title") var Title: String,
    @ColumnInfo(name = "body") var Body: String,
    @ColumnInfo(name = "color") var Color: String,
    @ColumnInfo(name = "reminder") var Reminder: Long? = null,
    @ColumnInfo(name = "isPinned") var IsPinned: Boolean = false,
    @ColumnInfo(name = "pinnedOn") var PinnedOn: Long? = null
) {
    constructor() : this(Id = null, Title = "", Body = "", Color = "")
}