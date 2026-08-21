#include <jni.h>
#include <dlfcn.h>
#include <string>
#include <android/log.h>

#define LOG_TAG "StarBridge"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jboolean JNICALL
Java_com_starclient_app_core_minecraft_MinecraftActivity_loadNativeEngine(
        JNIEnv* env,
        jobject thiz,
        jstring mcLibPath) {

    const char* path = env->GetStringUTFChars(mcLibPath, nullptr);
    
    // Caminho completo para a biblioteca do Minecraft instalado
    std::string fmodPath = std::string(path) + "/libfmod.so";
    std::string mcPath = std::string(path) + "/libminecraftpe.so";

    // 1. Carrega o FMOD primeiro (dependência de áudio)
    void* fmodHandle = dlopen(fmodPath.c_str(), RTLD_NOW | RTLD_GLOBAL);
    if (!fmodHandle) {
        LOGE("Falha ao carregar libfmod.so: %s", dlerror());
    }

    // 2. Carrega o motor principal do Minecraft no namespace global
    void* mcHandle = dlopen(mcPath.c_str(), RTLD_NOW | RTLD_GLOBAL);
    if (!mcHandle) {
        LOGE("Falha ao carregar libminecraftpe.so: %s", dlerror());
        env->ReleaseStringUTFChars(mcLibPath, path);
        return JNI_FALSE;
    }

    LOGI("Motor do Minecraft injetado com sucesso!");
    env->ReleaseStringUTFChars(mcLibPath, path);
    return JNI_TRUE;
}

