package com.example.videoplayerapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView

class MainActivity : AppCompatActivity() {

    private lateinit var playerView: PlayerView
    private var player: ExoPlayer? = null
    private var isFullscreen = false
    private lateinit var btnFullscreen: ImageButton
    private lateinit var btnSelectVideo: Button

    private val PICK_VIDEO_REQUEST = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView = findViewById(R.id.playerView)
        btnFullscreen = findViewById(R.id.btnFullscreen)
        btnSelectVideo = findViewById(R.id.btnSelectVideo)  // New button

        // Initialize ExoPlayer
        player = ExoPlayer.Builder(this).build()
        playerView.player = player

        // Load default video (online sample)
        val mediaItem = MediaItem.fromUri(
            Uri.parse("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
        )
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()

        // Fullscreen toggle
        btnFullscreen.setOnClickListener {
            if (isFullscreen) {
                // Exit fullscreen
                supportActionBar?.show()
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                isFullscreen = false
                btnFullscreen.setImageResource(R.drawable.ic_fullscreen)
            } else {
                // Enter fullscreen
                supportActionBar?.hide()
                window.decorView.systemUiVisibility =
                    (View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
                isFullscreen = true
                btnFullscreen.setImageResource(R.drawable.ic_fullscreen_exit)
            }
        }

        // Open file picker to select local video
        btnSelectVideo.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "video/*"
            startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == Activity.RESULT_OK) {
            val selectedUri: Uri? = data?.data
            if (selectedUri != null) {
                player?.setMediaItem(MediaItem.fromUri(selectedUri))
                player?.prepare()
                player?.play()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
}