package com.stevdzasan.diaryapp.data.repository

import com.stevdzasan.diaryapp.model.Diary
import com.stevdzasan.diaryapp.util.RequestState
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

typealias Diaries = RequestState<Map<LocalDate, List<Diary>>>

interface MongoRepository {
    fun configureTheRealm()
    fun getAllDiaries(): Flow<Diaries>
}