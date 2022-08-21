package com.realityexpander.mediaplayer

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import com.realityexpander.mediaplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    val mediaPlayer = MediaPlayer()
    lateinit var runnable: Runnable
    lateinit var handler: Handler
    var isSongLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)
//
//        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }

        binding.playBtn.setOnClickListener {
            if(!isSongLoaded) {
                binding.playBtn.setImageResource(android.R.drawable.ic_media_pause)
                playSong()
                return@setOnClickListener
            }

            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                binding.playBtn.setImageResource(android.R.drawable.ic_media_play)
            } else {
                mediaPlayer.start()
                binding.playBtn.setImageResource(android.R.drawable.ic_media_pause)
            }
        }


        handler = Handler(Looper.getMainLooper())

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                    binding.seekBar.progress = progress
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                handler.removeCallbacks(runnable)
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                handler.postDelayed(runnable, 1000)
            }
        })


    }

    fun playSong() {
        val uri = Uri.parse("https://files.freemusicarchive.org//storage-freemusicarchive-org//tracks//Pwgnnzp2ZsICaklopTbKD24keSTqsptGRvZSmY2J.mp3")

        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                .build()
        )
        mediaPlayer.reset()

        try {
            mediaPlayer.setDataSource(this, uri)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            binding.seekBar.max = mediaPlayer.duration
            mediaPlayer.start()
            updateSeekbar()
        }

        mediaPlayer.setOnBufferingUpdateListener { mp, percent ->
            val ratio = percent / 100.0
            val bufferingLevel = (mp.duration * ratio).toInt()
            binding.seekBar.secondaryProgress = bufferingLevel
        }

        isSongLoaded = true
    }

    fun updateSeekbar() {
        val curPosition = mediaPlayer.currentPosition
        binding.seekBar.setProgress(curPosition)

        runnable = Runnable { updateSeekbar() }
        handler.postDelayed(runnable, 100)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
//    }
}