package com.starclient.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MinecraftLoadingActivity : AppCompatActivity() {

    private lateinit var statusTextView: TextView
    private lateinit var progressBar: ProgressBar

    // Nomes das bibliotecas sem o prefixo 'lib' e sem o sufixo '.so'
    private val libraries = listOf(
        "gxcore",
        "inbuiltmods",
        "maesdk",
        "PlayFabMultiplayer"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        statusTextView = findViewById(R.id.statusTextView)
        progressBar = findViewById(R.id.progressBar)
        progressBar.max = libraries.size

        startLibraryLoadingSequence(0)
    }

    private fun startLibraryLoadingSequence(index: Int) {
        if (index >= libraries.size) {
            statusTextView.text = "Iniciando motor do jogo..."
            Handler(Looper.getMainLooper()).postDelayed({
                launchGameActivity()
            }, 500)
            return
        }

        val libName = libraries[index]
        val displayFileName = "lib$libName.so"

        statusTextView.text = "Carregando biblioteca: $displayFileName"
        progressBar.progress = index + 1

        Handler(Looper.getMainLooper()).postDelayed({
            try {
                System.loadLibrary(libName)
            } catch (e: UnsatisfiedLinkError) {
                // Mantém o preloader rodando caso a biblioteca dependa de runtime externa
                e.printStackTrace()
            }

            startLibraryLoadingSequence(index + 1)
        }, 700)
    }

    private fun launchGameActivity() {
        try {
            val intent = Intent().apply {
                setClassName(packageName, "com.starclient.app.MainActivity")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            statusTextView.text = "Erro ao abrir atividade principal: ${e.message}"
        }
    }
}

