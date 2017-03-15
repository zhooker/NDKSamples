#include <jni.h>
#include <string>
#include <android/log.h>
#include <string.h>
#include "common.h"

extern "C" int hello(){
    printf("Hello World\n");
    return 888;
}