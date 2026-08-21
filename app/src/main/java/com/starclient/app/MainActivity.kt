package com.starclient.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.starclient.app.core.minecraft.MinecraftActivity

class MainActivity : AppCompatActivity() {

    private lateinit var loadingLayout: LinearLayout
    private lateinit var contentLayout: LinearLayout
    private lateinit var statusText: TextView
    private lateinit var playButton: Button

    private val libraries = listOf(
        "gxcore",
        "inbuiltmods",
        "maesdk",
        "PlayFabMultiplayer"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadingLayout = findViewById(R.id.loadingLayout)
        contentLayout = findViewById(R.id.contentLayout)
        statusText = findViewById(R.id.statusText)
        playButton = findViewById(R.id.playButton)

        startLibraryLoadingSequence(0)

        playButton.setOnClickListener {
            val intent = Intent(this, MinecraftActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun startLibraryLoadingSequence(index: Int) {
        if (index >= libraries.size) {
            loadingLayout.visibility = View.GONE
            contentLayout.visibility = View.VISIBLE
            statusText.text = "Pronto para jogar!"
            return
        }

        val libName = libraries[index]

        Handler(Looper.getMainLooper()).postDelayed({
            try {
                System.loadLibrary(libName)
            } catch (e: UnsatisfiedLinkError) {
                e.printStackTrace()
            }
            startLibraryLoadingSequence(index + 1)
        }, 300)
    }
}
