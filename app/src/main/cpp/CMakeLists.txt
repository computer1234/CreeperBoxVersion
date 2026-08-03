# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html.
# For more examples on how to use CMake, see https://github.com/android/ndk-samples.

# Sets the minimum CMake version required for this project.
cmake_minimum_required(VERSION 3.22.1)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

# Declares the project name. The project name can be accessed via ${ PROJECT_NAME},
# Since this is the top level CMakeLists.txt, the project name is also accessible
# with ${CMAKE_PROJECT_NAME} (both CMake variables are in-sync within the top level
# build script scope).
project("creeperbox")

file(GLOB_RECURSE INCLUDES_FILES Include/*.cpp)
file(GLOB_RECURSE VERIFY_SRCS Verify/*.cpp)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.
#
# In this top level CMakeLists.txt, ${CMAKE_PROJECT_NAME} is used to define
# the target library name; in the sub-module's CMakeLists.txt, ${PROJECT_NAME}
# is preferred for the same purpose.
#
# In order to load a library into your app from Java/Kotlin, you must call
# System.loadLibrary() and pass the name of the library defined here;
# for GameActivity/NativeActivity derived applications, the same library name must be
# used in the AndroidManifest.xml file.
add_library(${CMAKE_PROJECT_NAME} SHARED
        # List C/C++ source files with relative paths to this CMakeLists.txt.
        native-lib.cpp
        Memory/GameData.cpp
        Memory/Hooks.cpp
        SDK/Actor.cpp
        SDK/BlockSource.cpp
        SDK/ClientInstance.cpp
        SDK/Level.cpp
        JavaData.cpp
        JNIFunc.cpp
        SDK/Packet.cpp
        Utils/JNIUtils.cpp
        EntityActorJNI.cpp
        BlockJNI.cpp
        InventoryJNI.cpp
        ItemJNI.cpp
        LevelJNI.cpp
        RenderJNI.cpp
        EntityJNI.cpp
        CreeperBoxJNI.cpp
        NeteaseManagerJNI.cpp
        ClientInstanceJNI.cpp
        SDK/Render2D.cpp
        SDK/Render3D.cpp
        SDK/Skin.cpp
        SDK/Inventory.cpp
        ${INCLUDES_FILES}
        ${VERIFY_SRCS}
        )

target_include_directories(${CMAKE_PROJECT_NAME} PRIVATE
        Include/
        Include/websocketpp/
        Include/asio/
        Include/openssl/
        Verify/
        ${CMAKE_CURRENT_SOURCE_DIR}
)

# OpenSSL 静态库
add_library(openssl_crypto STATIC IMPORTED)
set_target_properties(openssl_crypto PROPERTIES
        IMPORTED_LOCATION ${CMAKE_CURRENT_SOURCE_DIR}/Include/openssl/libcrypto.a)

add_library(openssl_ssl STATIC IMPORTED)
set_target_properties(openssl_ssl PROPERTIES
        IMPORTED_LOCATION ${CMAKE_CURRENT_SOURCE_DIR}/Include/openssl/libssl.a)


# Specifies libraries CMake should link to your target library. You
# can link libraries from various origins, such as libraries defined in this
# build script, prebuilt third-party libraries, or Android system libraries.
target_link_libraries(${CMAKE_PROJECT_NAME}
        # List libraries link to the target library
        android
        openssl_ssl
        openssl_crypto
        openssl_crypto
        log)

target_compile_options(${CMAKE_PROJECT_NAME} PRIVATE
        -fvisibility=hidden
        -fvisibility-inlines-hidden
        -ffunction-sections
        -fdata-sections
)

target_link_options(${CMAKE_PROJECT_NAME} PRIVATE
        -Wl,--gc-sections
        -Wl,--exclude-libs,ALL
        -Wl,--discard-all
        # 生成 Map 文件 (包含函数偏移地址) - 保留符号用于混淆器定位
        -Wl,-Map=${CMAKE_LIBRARY_OUTPUT_DIRECTORY}/libcreeperbox.map
)

# 编译后手动 strip (这样 map 文件会保留完整符号)
add_custom_command(TARGET ${CMAKE_PROJECT_NAME} POST_BUILD
        COMMAND ${CMAKE_STRIP} --strip-all $<TARGET_FILE:${CMAKE_PROJECT_NAME}>
        COMMENT "Stripping symbols from libcreeperbox.so (map file preserved)"
)
