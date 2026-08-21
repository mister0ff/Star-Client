package com.starclient.app

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadingLayout = findViewById(R.id.loadingLayout)
        contentLayout = findViewById(R.id.contentLayout)
        statusText = findViewById(R.id.statusText)
        playButton = findViewById(R.id.playButton)

        checkAndRequestStoragePermission()

        // Exibe o menu diretamente após checar requisitos
        loadingLayout.visibility = View.GONE
        contentLayout.visibility = View.VISIBLE
        statusText.text = "Star Client Pronto"

        playButton.setOnClickListener {
            if (!isMinecraftInstalled()) {
                Toast.makeText(this, "Erro: Minecraft Bedrock não está instalado!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (!hasStoragePermission()) {
                Toast.makeText(this, "Conceda a permissão de armazenamento!", Toast.LENGTH_SHORT).show()
                checkAndRequestStoragePermission()
                return@setOnClickListener
            }

            val intent = Intent(this, MinecraftActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isMinecraftInstalled(): Boolean {
        return try {
            packageManager.getPackageInfo("com.mojang.minecraftpe", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
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
