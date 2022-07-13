package com.jim.moviecritics.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jim.moviecritics.data.Comment
import com.jim.moviecritics.data.source.ApplicationRepository
import com.jim.moviecritics.report.ReportViewModel


class CommentViewModelFactory(
    private val applicationRepository: ApplicationRepository,
    private val comment: Comment
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(ReportViewModel::class.java) ->
                    ReportViewModel(applicationRepository, comment)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}