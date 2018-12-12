package com.stanledelacruz.placebook.repository

import android.arch.lifecycle.LiveData
import android.content.Context
import com.stanledelacruz.placebook.db.BookmarkDao
import com.stanledelacruz.placebook.db.PlaceBookDatabase
import com.stanledelacruz.placebook.model.Bookmark

class BookmarkRepo(private val context: Context) {
    private var db: PlaceBookDatabase = PlaceBookDatabase.getInstance(context)
    private var bookmarkDao: BookmarkDao = db.bookmarkDao()

    fun addBookmark(bookmark: Bookmark): Long? {
        val newId = bookmarkDao.insertBookmark(bookmark)
        bookmark.id = newId
        return newId
    }

    fun createBookmark(): Bookmark {
        return Bookmark()
    }

    val allBookmarks: LiveData<List<Bookmark>>
        get() {
            return bookmarkDao.loadAl()
        }
}