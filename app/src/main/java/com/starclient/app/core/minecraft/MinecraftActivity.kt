package com.starclient.app.core.minecraft

import android.app.NativeActivity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log

class MinecraftActivity : NativeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            // Obter o caminho real das bibliotecas nativas do Minecraft instalado
            val mcInfo = packageManager.getApplicationInfo("com.mojang.minecraftpe", 0)
            val mcNativeDir = mcInfo.nativeLibraryDir
            val mcSourceDir = mcInfo.publicSourceDir

            // Passa os caminhos para o ambiente nativo antes de iniciar a NativeActivity
            intent.putExtra("mc_native_dir", mcNativeDir)
            intent.putExtra("mc_source_dir", mcSourceDir)

        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("StarClient", "Minecraft não instalado no dispositivo!")
            finish()
            return
        }

        super.onCreate(savedInstanceState)
    }

    companion object {
        init {
            // Ordem de carregamento com tratamento de falhas para preloader nativo
            val preloaderLibs = listOf(
                "c++_shared",
                "PlayFabMultiplayer",
                "maesdk",
                "inbuiltmods",
                "gxcore"
            )

            for (lib in preloaderLibs) {
                try {
                    System.loadLibrary(lib)
                } catch (e: UnsatisfiedLinkError) {
                    Log.w("StarClient", "Aviso ao carregar $lib: ${e.message}")
                }
            }
        }
    }
}
