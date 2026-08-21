package com.starclient.app

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val MINECRAFT_PACKAGE = "com.mojang.minecraftpe"
    private val SPLASH_DELAY_MS = 1600L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loadingLayout = findViewById<android.widget.LinearLayout>(R.id.loadingLayout)
        val contentLayout = findViewById<android.widget.LinearLayout>(R.id.contentLayout)
        val statusText = findViewById<TextView>(R.id.statusText)
        val playButton = findViewById<Button>(R.id.playButton)

        // Tela de carregamento (splash) antes de liberar o botão Jogar
        Handler(Looper.getMainLooper()).postDelayed({
            loadingLayout.visibility = android.view.View.GONE
            contentLayout.visibility = android.view.View.VISIBLE

            if (isMinecraftInstalled()) {
                statusText.text = "Minecraft Bedrock encontrado ✔"
                playButton.isEnabled = true
            } else {
                statusText.text = "Minecraft Bedrock não está instalado"
                playButton.isEnabled = false
            }
        }, SPLASH_DELAY_MS)

        playButton.setOnClickListener {
            launchMinecraft()
        }
    }

    private fun isMinecraftInstalled(): Boolean {
        return try {
            packageManager.getPackageInfo(MINECRAFT_PACKAGE, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun launchMinecraft() {
        val intent = packageManager.getLaunchIntentForPackage(MINECRAFT_PACKAGE)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Não foi possível abrir o Minecraft", Toast.LENGTH_SHORT).show()
        }
    }
}

