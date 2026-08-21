package com.starclient.app

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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

        checkAndRequestStoragePermission()
        startLibraryLoadingSequence(0)

        playButton.setOnClickListener {
            if (hasStoragePermission()) {
                val intent = Intent(this, MinecraftActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Conceda a permissão de armazenamento para jogar!", Toast.LENGTH_LONG).show()
                checkAndRequestStoragePermission()
            }
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
        }, 200)
    }

    private fun hasStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            true
        }
    }

    private fun checkAndRequestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)
            }
        }
    }
}
