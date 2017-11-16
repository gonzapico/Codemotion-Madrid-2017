package xyz.gonzapico.codemotion2017

import android.app.Application
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.toast
import xyz.gonzapico.codemotion2017.data.CodemotionDataRepository
import java.util.UUID

/**
 * Created by gonzapico on 15/11/2017.
 */
class CodemotionApp : Application(), AnkoLogger {
  val repository: CodemotionDataRepository = CodemotionDataRepository(this)

  override fun onCreate() {
    super.onCreate()
    val userId = getUserId()

    repository.userId = userId
    repository.onError = { action ->
      when (action) {
        CodemotionDataRepository.Error.FAILED_TO_DELETE_RATING ->
          toast(R.string.msg_failed_to_delete_vote)

        CodemotionDataRepository.Error.FAILED_TO_POST_RATING
        ->
          toast(R.string.msg_failed_to_post_vote)

        CodemotionDataRepository.Error.FAILED_TO_GET_DATA
        ->
          toast(R.string.msg_failed_to_get_data)

        CodemotionDataRepository.Error.EARLY_TO_VOTE
        ->
          toast(R.string.msg_early_vote)

        CodemotionDataRepository.Error.LATE_TO_VOTE
        ->
          toast(R.string.msg_late_vote)
      }
    }

    launch(UI) {
      val dataLoaded = repository.loadLocalData()
      if (!dataLoaded) {
        repository.update()
      }
    }
  }

  private fun getUserId(): String {
    defaultSharedPreferences.getString(USER_ID_KEY, null)?.let {
      return it
    }

    val userId = "android-" + UUID.randomUUID().toString()
    defaultSharedPreferences
        .edit()
        .putString(USER_ID_KEY, userId)
        .apply()

    return userId
  }

  companion object {
    const val USER_ID_KEY = "UserId"
  }
}