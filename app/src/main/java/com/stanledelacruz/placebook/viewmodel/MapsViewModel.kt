package com.stanledelacruz.placebook.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.graphics.Bitmap
import android.util.Log
import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.model.LatLng
import com.stanledelacruz.placebook.model.Bookmark
import com.stanledelacruz.placebook.repository.BookmarkRepo

class MapsViewModel(application: Application): AndroidViewModel(application) {

    private val TAG = "MapsViewModel"

    private var bookmarkRepo: BookmarkRepo = BookmarkRepo(getApplication())
    private var bookmarks: LiveData<List<BookmarkMarkerView>>? = null
    data class BookmarkMarkerView(var id: Long? = null, var location: LatLng = LatLng(0.0, 0.0))


    fun addBookmarkFromPlace(place: Place, image: Bitmap) {
        val bookmark = bookmarkRepo.createBookmark()
        bookmark.placeId = place.id
        bookmark.name = place.name.toString()
        bookmark.longitude = place.latLng.longitude
        bookmark.latitude = place.latLng.latitude
        bookmark.phone = place.phoneNumber.toString()
        bookmark.address = place.address.toString()

        val newId = bookmarkRepo.addBookmark(bookmark)

        Log.i(TAG, "New Bookmark $newId added to the database.")
    }

    fun getBookmarkMarkerView(): LiveData<List<BookmarkMarkerView>>? {
        if (bookmarks == null) {
            mapBookmarksToMarkerView()
        }
        return bookmarks
    }

    private fun bookmarkToMarkerView(bookmark: Bookmark): MapsViewModel.BookmarkMarkerView {
        return  MapsViewModel.BookmarkMarkerView(bookmark.id, LatLng(bookmark.latitude, bookmark.longitude))
    }

    private fun mapBookmarksToMarkerView() {
        val allBookmark = bookmarkRepo.allBookmarks
        bookmarks = Transformations.map(allBookmark) { bookmarks ->
            val bookmarkMarkerViews = bookmarks.map { bookmark ->
                bookmarkToMarkerView(bookmark)
            }
            bookmarkMarkerViews
        }
    }
}