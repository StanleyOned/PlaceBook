package com.stanledelacruz.placebook.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.OnConflictStrategy.IGNORE
import com.stanledelacruz.placebook.model.Bookmark

@Dao
interface BookmarkDao {

    @Query("Select * from Bookmark")
    fun loadAl(): LiveData<List<Bookmark>>


    @Query("SELECT * FROM bookmark WHERE id = :arg0")
    fun loadBookmark(bookmarkId: Long): Bookmark

    @Query("SELECT * FROM bookmark WHERE id = :arg0")
    fun loadLiveBookmark(bookmarkId: Long): LiveData<Bookmark>

    @Insert(onConflict = IGNORE)
    fun insertBookmark(bookmark: Bookmark): Long

    @Update(onConflict = REPLACE)
    fun updateBookmark(bookmark: Bookmark)

    @Delete
    fun deleteBookmark(bookmark: Bookmark)
}