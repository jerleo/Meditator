package de.jerleo.meditator

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import de.jerleo.meditator.databinding.ActivityMainBinding

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: ChimeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Observe countdown text
        viewModel.remainingTime.observe(this, Observer { time ->
            binding.timerText.text = time
        })

        // Observe if chime was blocked due to missing sound
        viewModel.noSoundEvent.observe(this, Observer { show ->
            if (show) {
                Toast.makeText(this, getString(R.string.no_sound), Toast.LENGTH_LONG).show()
            }
        })

        // Add after other observers
        viewModel.ringtoneName.observe(this) { name ->
            binding.soundNameText.text = getString(R.string.notification_sound, name)
        }

        // Slider
        binding.slider.addOnChangeListener { _, value, _ ->
            viewModel.setIntervalMinutes(value.toInt())
            viewModel.prepareRingtone()
        }
        binding.slider.value = 5f

        // Stop button
        binding.stopButton.setOnClickListener {
            viewModel.stopTimer()
        }

        // Start countdown initially
        viewModel.startTimer()
    }
}