package com.stevdzasan.diaryapp.data.repository

import android.util.Log
import com.stevdzasan.diaryapp.model.Diary
import com.stevdzasan.diaryapp.util.Constants.APP_ID
import com.stevdzasan.diaryapp.util.RequestState
import com.stevdzasan.diaryapp.util.toInstant
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.ZoneId

object MongoDB : MongoRepository {
    private val app = App.create(APP_ID)
    private val user = app.currentUser
    private lateinit var realm: Realm

    init {
        configureTheRealm()
    }

    override fun configureTheRealm() {
        if (user != null) {
            val config = SyncConfiguration.Builder(user, setOf(Diary::class))
                .initialSubscriptions { sub ->
                    add(
                        query = sub.query<Diary>("ownerId == $0", user.identity),
                        name = "User's Diaries"
                    )
                }
                .log(LogLevel.ALL)
                .build()
            realm = Realm.open(config)
            Log.d("configureTheRealm", "${user.identity} + ${realm.configuration.schema} ")
            Log.d("configureTheRealm", "${user.identity} + ${realm.configuration.log} ")
            Log.d("configureTheRealm", "${user.identity} + ${realm.configuration.path} ")
        }
    }

    override fun getAllDiaries(): Flow<Diaries> {
        return if (user != null) {
            try {
                Log.d("getAllDiariesMain", "${realm.query<Diary>().first()}")
                realm.query<Diary>()
                    .sort(property = "date", sortOrder = Sort.DESCENDING)
                    .asFlow()
                    .map { result ->
                        Log.d("getAllDiaries", "${user.identity}+ ${result.list}")
                        RequestState.Success(
                            data = result.list.groupBy {
                                it.date.toInstant().atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            }
                        )
                    }
            } catch (e: Exception) {
                flow { emit(RequestState.Error(e)) }
            }
        } else {
            flow { emit(RequestState.Error(UserNotAuthenticatedException())) }
        }
    }
}

private class UserNotAuthenticatedException : Exception("User is not Logged in.")