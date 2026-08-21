#include <jni.h>
#include <dlfcn.h>
#include <string>
#include <android/log.h>

#define LOG_TAG "StarBridge"

extern "C" JNIEXPORT jboolean JNICALL
Java_com_starclient_app_core_minecraft_MinecraftActivity_loadNativeEngine(JNIEnv* env, jobject thiz, jstring mcLibPath) {
    const char* path = env->GetStringUTFChars(mcLibPath, nullptr);
    if (path == nullptr) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Falha ao obter string do path");
        return JNI_FALSE;
    }

    std::string fullPath = std::string(path) + "/libminecraftpe.so";
    __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "Tentando carregar lib de: %s", fullPath.c_str());

    void* handle = dlopen(fullPath.c_str(), RTLD_NOW | RTLD_GLOBAL);

    env->ReleaseStringUTFChars(mcLibPath, path);

    if (!handle) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Falha ao carregar lib: %s", dlerror());
        return JNI_FALSE;
    }

    __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "Lib carregada com sucesso!");
    return JNI_TRUE;
}
