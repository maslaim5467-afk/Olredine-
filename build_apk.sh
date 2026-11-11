#!/bin/bash
set -e

ANDROID_JAR="$(pwd)/android.jar"

if [ ! -f "$ANDROID_JAR" ]; then
    echo "خطا: فایل android.jar پیدا نشد!"
    exit 1
fi

if [ ! -f r8.jar ]; then
    echo "خطا: فایل r8.jar پیدا نشد!"
    exit 1
fi

echo "[1/6] Compiling C Code to Shared Library (.so)..."
clang -shared -fPIC native_game.c -o lib/arm64-v8a/libmygame.so -landroid -llog

echo "[2/6] Compiling Java Code..."
javac -classpath "$ANDROID_JAR" -d obj src/com/example/mycgame/MainActivity.java

echo "[3/6] Converting Java Bytecode to Android DEX via Java..."
java -cp r8.jar com.android.tools.r8.D8 --classpath "$ANDROID_JAR" --output . $(find obj -name "*.class")

echo "[4/6] Packaging Resources and Assets with AAPT..."
aapt package -f -M AndroidManifest.xml -S res -I "$ANDROID_JAR" -F app.unsigned.apk

echo "[5/6] Adding Compiled DEX and Native Libraries into APK..."
aapt add app.unsigned.apk classes.dex
aapt add app.unsigned.apk lib/arm64-v8a/libmygame.so

echo "[6/6] Generating Key and Signing APK..."
if [ ! -f my-release-key.jks ]; then
    keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-alias -storepass 123456 -keypass 123456 -dname "CN=Termux, OU=Dev, O=MargYoorna, L=City, ST=State, C=US"
fi

apksigner sign --ks my-release-key.jks --ks-pass pass:123456 --out MyCGame.apk app.unsigned.apk

echo "=========================================="
echo "SUCCESS! APK built at: $(pwd)/MyCGame.apk"
echo "=========================================="
