package com.starclient.app.core.minecraft

import android.app.NativeActivity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log

class MinecraftActivity : NativeActivity() {

    private external fun loadNativeEngine(mcLibPath: String): Boolean

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            val mcInfo = packageManager.getApplicationInfo("com.mojang.minecraftpe", 0)
            val success = loadNativeEngine(mcInfo.nativeLibraryDir)
            
            if (!success) {
                Log.e("StarClient", "Não foi possível carregar a libminecraftpe.so do sistema.")
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("StarClient", "Minecraft não encontrado no dispositivo.")
            finish()
            return
        }

        super.onCreate(savedInstanceState)
    }

    companion object {
        init {
            // Carrega a ponte nativa C++ primeiro
            System.loadLibrary("starbridge")
            
            // Carrega o preloader e mods
            System.loadLibrary("c++_shared")
            System.loadLibrary("PlayFabMultiplayer")
            System.loadLibrary("maesdk")
            System.loadLibrary("inbuiltmods")
            System.loadLibrary("gxcore")
        }
    }
}
