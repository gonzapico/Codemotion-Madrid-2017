package xyz.gonzapico.codemotion2017.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.warn
import ru.gildor.coroutines.retrofit.awaitResult
import xyz.gonzapico.codemotion2017.data.CodemotionAPI.Companion.DATE_FORMAT
import xyz.gonzapico.codemotion2017.ifFailed
import xyz.gonzapico.codemotion2017.ifSucceeded
import java.io.File

/**
 * Created by gonzapico on 15/11/2017.
 */

class CodemotionDataRepository(private val context: Context) : AnkoLogger {

  lateinit var userId: String
  var onError: ((action: Error) -> Unit)? = null

  private val gson: Gson by lazy {
    GsonBuilder()
        .setDateFormat(DATE_FORMAT)
        .create()
  }

  private val kotlinConfApi: CodemotionAPI by lazy {
    CodemotionAPI.create()
  }

  private val favoritePreferences: SharedPreferences by lazy {
    context.getSharedPreferences(FAVORITES_PREFERENCES_NAME, Context.MODE_PRIVATE)
  }

  private val ratingPreferences: SharedPreferences by lazy {
    context.getSharedPreferences(VOTES_PREFERENCES_NAME, Context.MODE_PRIVATE)
  }

  private val _data: MutableLiveData<CodemotionAPIResponse> = MutableLiveData()

  private val _isUpdating = MutableLiveData<Boolean>()
  val isUpdating: LiveData<Boolean> = _isUpdating

  private fun updateLocalData(allData: CodemotionAPIResponse) {
    val allDataFile = File(context.filesDir, CACHED_DATA_FILE_NAME)
    allDataFile.delete()
    allDataFile.createNewFile()
    allDataFile.writeText(gson.toJson(allData))
    _data.value = allData
  }

  fun loadLocalData(): Boolean {
    val allDataFile = File(context.filesDir, CACHED_DATA_FILE_NAME)
    if (!allDataFile.exists()) {
      return false
    }

    val allData = gson.fromJson<CodemotionAPIResponse>(allDataFile.readText(),
        CodemotionAPIResponse::class.java) ?: return false

    _data.value = allData


    return true
  }

  suspend fun update() {
    if (_isUpdating.value == true) {
      return
    }

    _isUpdating.value = true

    kotlinConfApi
        .agenda()
        .awaitResult()
        .ifSucceeded { allData ->
          updateLocalData(allData)
        }
        .ifFailed {
          warn("Failed to get data from server")
          onError?.invoke(Error.FAILED_TO_GET_DATA)
        }

    _isUpdating.value = false
  }

  companion object {
    const val FAVORITES_PREFERENCES_NAME = "favorites"
    const val VOTES_PREFERENCES_NAME = "votes"
    const val FAVORITES_KEY = "favorites"
    const val CACHED_DATA_FILE_NAME = "data.json"

    const val HTTP_COME_BACK_LATER = 477
    const val HTTP_TOO_LATE = 478
  }

  enum class Error {
    FAILED_TO_POST_RATING,
    FAILED_TO_DELETE_RATING,
    FAILED_TO_GET_DATA,
    EARLY_TO_VOTE,
    LATE_TO_VOTE
  }
}