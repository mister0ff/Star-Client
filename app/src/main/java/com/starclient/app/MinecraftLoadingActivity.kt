package com.starclient.app

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
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

        // ---------- Layout raiz (fundo escuro estilo "stone") ----------
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(64, 64, 64, 64)
            background = buildStoneBackground()
        }

        // Título
        val titleText = TextView(this).apply {
            text = "STAR CLIENT"
            setTextColor(Color.parseColor("#55FF55"))
            textSize = 28f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            setShadowLayer(6f, 3f, 3f, Color.parseColor("#1B1B1B"))
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 80)
        }

        // Status
        statusTextView = TextView(this).apply {
            text = "Carregando..."
            setTextColor(Color.WHITE)
            textSize = 14f
            typeface = Typeface.MONOSPACE
            setShadowLayer(3f, 2f, 2f, Color.BLACK)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 32)
        }

        // Barra de progresso estilo "bloco pixelado"
        progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = libraries.size
            progressDrawable = buildMinecraftProgressDrawable()
            layoutParams = LinearLayout.LayoutParams(700, 40)
        }

        root.addView(titleText)
        root.addView(statusTextView)
        root.addView(progressBar)

        setContentView(root)

        // ---------- Lógica original ----------
        startLibraryLoadingSequence(0)
    }

    // Fundo em degradê "pedra" escura
    private fun buildStoneBackground(): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                Color.parseColor("#2B2B2B"),
                Color.parseColor("#1A1A1A")
            )
        )
    }

    // Barra de progresso com visual "pixelado" (sem cantos arredondados + borda)
    private fun buildMinecraftProgressDrawable(): LayerDrawable {
        // Fundo da barra (vazio)
        val backgroundDrawable = GradientDrawable().apply {
            setColor(Color.parseColor("#1A1A1A"))
            setStroke(4, Color.parseColor("#0D0D0D"))
            cornerRadius = 0f
        }

        // Preenchimento (progresso) - verde grama
        val progressShape = GradientDrawable().apply {
            setColor(Color.parseColor("#6B8E3D"))
            cornerRadius = 0f
        }
        val progressBorder = GradientDrawable().apply {
            setColor(Color.TRANSPARENT)
            setStroke(3, Color.parseColor("#3A5220"))
            cornerRadius = 0f
        }
        val progressLayer = LayerDrawable(arrayOf(progressShape, progressBorder))
        val clipProgress = ClipDrawable(progressLayer, Gravity.START, ClipDrawable.HORIZONTAL)

        val layers = arrayOf<android.graphics.drawable.Drawable>(
            backgroundDrawable,
            clipProgress
        )
        val layerDrawable = LayerDrawable(layers)
        layerDrawable.setId(0, android.R.id.background)
        layerDrawable.setId(1, android.R.id.progress)
        return layerDrawable
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
