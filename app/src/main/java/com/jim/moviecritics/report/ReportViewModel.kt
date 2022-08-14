package com.jim.moviecritics.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.jim.moviecritics.R
import com.jim.moviecritics.data.Comment
import com.jim.moviecritics.data.Report
import com.jim.moviecritics.data.Result
import com.jim.moviecritics.data.source.Repository
import com.jim.moviecritics.login.UserManager
import com.jim.moviecritics.network.LoadApiStatus
import com.jim.moviecritics.util.Logger
import com.jim.moviecritics.util.Util.getString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ReportViewModel(
    private val repository: Repository,
    private val arguments: Comment
) : ViewModel() {

    private val _comment = MutableLiveData<Comment>().apply {
        value = arguments
    }

    val comment: LiveData<Comment>
        get() = _comment


    private val report = Report()

    val selectedReasonRadio = MutableLiveData<Int>()

    private val reportReason: String
        get() = when (selectedReasonRadio.value) {
            R.id.radios_report_reason_abuse -> getString(R.string.text_report_reason_abuse)
            R.id.radios_reason_spoilers -> getString(R.string.text_report_reason_spoilers_label)
            R.id.radios_report_reason_spam -> getString(R.string.text_report_reason_spam)
            R.id.radios_report_reason_plagiarism -> getString(R.string.text_report_reason_plagiarism)
            R.id.radios_report_reason_other -> getString(R.string.text_report_reason_other_label)
            else -> ""
        }

    val message = MutableLiveData<String>()

    private val _invalidReport = MutableLiveData<Int>()

    val invalidReport: LiveData<Int>
        get() = _invalidReport

    // Handle leave login
    private val _leave = MutableLiveData<Boolean?>()

    val leave: LiveData<Boolean?>
        get() = _leave

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]$this")
        Logger.i("------------------------------------")

        report.commentID = comment.value?.id.toString()
        report.imdbID = comment.value?.imdbID.toString()
        report.userID = UserManager.userID.toString()
    }

    fun leave() {
        _leave.value = true
    }

    fun onLeaveCompleted() {
        _leave.value = null
    }

    fun prepareReport() {
        when {
            reportReason.isEmpty() -> _invalidReport.value = INVALID_FORMAT_REASON_EMPTY
            message.value.isNullOrEmpty() -> _invalidReport.value = INVALID_FORMAT_MESSAGE_EMPTY
            !message.value.isNullOrEmpty() && reportReason.isNotEmpty() -> {
                report.reason = reportReason
                report.message = message.value.toString()
                Logger.i("pushReport(report) = $report")
                pushReport(report)
                leave()
            }
            else -> _invalidReport.value = NO_ONE_KNOWS
        }
    }

    private fun pushReport(report: Report) {
        coroutineScope.launch {

            report.createdTime = Timestamp.now()

            _status.value = LoadApiStatus.LOADING

            when (val result = repository.pushReport(report)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                }
                else -> {
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }

    companion object {
        const val INVALID_FORMAT_REASON_EMPTY = 0x11
        const val INVALID_FORMAT_MESSAGE_EMPTY = 0x12
        const val NO_ONE_KNOWS = 0x21
    }
}
