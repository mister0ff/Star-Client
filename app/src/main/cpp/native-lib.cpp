#include <jni.h>
#include <dlfcn.h>
#include <android/log.h>

#define LOG_TAG "StarBridge"

extern "C" JNIEXPORT jboolean JNICALL
Java_com_starclient_app_core_minecraft_MinecraftActivity_loadNativeEngine(JNIEnv* env, jobject thiz, jstring mcLibPath) {
    const char* path = env->GetStringUTFChars(mcLibPath, nullptr);
    
    __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "Tentando carregar lib de: %s", path);

    void* handle = dlopen((std::string(path) + "/libminecraftpe.so").c_str(), RTLD_NOW | RTLD_GLOBAL);
    
    if (!handle) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "CRASH: %s", dlerror());
        env->ReleaseStringUTFChars(mcLibPath, path);
        return JNI_FALSE;
    }

    // Se chegou aqui, a lib carregou. O crash agora pode ser falta de hook
    __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "Lib carregada com sucesso!");
    
    env->ReleaseStringUTFChars(mcLibPath, path);
    return JNI_TRUE;
}
