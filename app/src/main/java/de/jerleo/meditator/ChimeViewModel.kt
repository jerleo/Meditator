package de.jerleo.meditator

import android.annotation.SuppressLint
import android.app.Application
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ChimeViewModel(application: Application) : AndroidViewModel(application) {

    private var intervalMinutes = 5
    private var timer: CountDownTimer? = null
    private var remainingMillis: Long = intervalMinutes * 60 * 1000L

    private val _remainingTime = MutableLiveData<String>()
    val remainingTime: LiveData<String> = _remainingTime

    private val _noSoundEvent = MutableLiveData<Boolean>()
    val noSoundEvent: LiveData<Boolean> = _noSoundEvent

    private var ringtone: Ringtone? = null
    private val _ringtoneName = MutableLiveData<String>()
    val ringtoneName: LiveData<String> = _ringtoneName

    init {
        prepareRingtone()
    }

    fun prepareRingtone() {
        val soundUri: Uri? = RingtoneManager.getActualDefaultRingtoneUri(
            getApplication(), RingtoneManager.TYPE_NOTIFICATION
        )

        if (soundUri == null) {
            _noSoundEvent.value = true
            _ringtoneName.value = "None"
        } else {
            ringtone = RingtoneManager.getRingtone(getApplication(), soundUri)
            val title =
                RingtoneManager.getRingtone(getApplication(), soundUri).getTitle(getApplication())
            _ringtoneName.value = title!!
        }
    }

    fun setIntervalMinutes(minutes: Int) {
        intervalMinutes = minutes
        stopTimer()
        startTimer()
    }

    fun startTimer() {
        stopTimer()
        remainingMillis = intervalMinutes * 60 * 1000L

        timer = object : CountDownTimer(remainingMillis, 1000) {
            @SuppressLint("DefaultLocale")
            override fun onTick(millisUntilFinished: Long) {
                remainingMillis = millisUntilFinished
                val minutes = millisUntilFinished / 60000
                val seconds = (millisUntilFinished / 1000) % 60
                _remainingTime.value = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                _remainingTime.value = "00:00"
                ringtone?.play() ?: _noSoundEvent.postValue(true)
                startTimer()
            }
        }.start()
    }

    fun stopTimer() {
        timer?.cancel()
        timer = null
    }
}