#!/bin/bash
set -Eeuo pipefail

# ==========================================
# Android Native Build System
# ==========================================

PROJECT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_DIR"

ANDROID_JAR="$HOME/android.jar"
R8_JAR="$HOME/r8.jar"

KEYSTORE_DIR="$HOME/AndroidKeys"
KEYSTORE="$KEYSTORE_DIR/my-release-key.jks"
KEY_ALIAS="my-alias"

PACKAGE_NAME="com.example.mycgame"
LIB_NAME="mygame"
ABI="arm64-v8a"

trap 'echo "خطأ: فشل البناء عند السطر $LINENO"; exit 1' ERR

echo "=========================================="
echo " Android Native Build System"
echo "=========================================="

for FILE in "$ANDROID_JAR" "$R8_JAR"; do
    if [ ! -f "$FILE" ]; then
        echo "خطأ: فایل پیدا نشد: $FILE"
        exit 1
    fi
done

for TOOL in clang javac java aapt keytool apksigner; do
    if ! command -v "$TOOL" >/dev/null 2>&1; then
        echo "خطا: ابزار $TOOL نصب نیست یا در PATH قرار ندارد."
        exit 1
    fi
done

if [ ! -f "native_game.c" ]; then
    echo "خطا: native_game.c پیدا نشد."
    exit 1
fi

if [ ! -f "src/com/example/mycgame/MainActivity.java" ]; then
    echo "خطا: MainActivity.java پیدا نشد."
    exit 1
fi

if [ ! -f "AndroidManifest.xml" ] || [ ! -d "res" ]; then
    echo "خطا: فایل Manifest یا پوشه res پیدا نشد."
    exit 1
fi

mkdir -p "lib/$ABI" "obj" "$KEYSTORE_DIR"

echo "[CLEAN] Removing previous build outputs..."
rm -rf obj/*
rm -f "lib/$ABI/lib$LIB_NAME.so" classes.dex app.unsigned.apk MyCGame.apk MyCGame.apk.idsig

echo "[1/6] Compiling C code..."
clang -shared -fPIC native_game.c -o "lib/$ABI/lib$LIB_NAME.so" -landroid -llog

echo "[2/6] Compiling all Java source files..."
mapfile -d '' JAVA_FILES < <(find src -type f -name "*.java" -print0)
if [ "${#JAVA_FILES[@]}" -eq 0 ]; then
    echo "خطا: هیچ فایل Java پیدا نشد."
    exit 1
fi

javac -classpath "$ANDROID_JAR" -sourcepath src -d obj "${JAVA_FILES[@]}"

echo "[3/6] Converting Java bytecode to DEX..."
mapfile -d '' CLASS_FILES < <(find obj -type f -name "*.class" -print0)
if [ "${#CLASS_FILES[@]}" -eq 0 ]; then
    echo "خطا: هیچ فایل class تولید نشده است."
    exit 1
fi

java -cp "$R8_JAR" com.android.tools.r8.D8 --lib "$ANDROID_JAR" --output "$PROJECT_DIR" "${CLASS_FILES[@]}"
if [ ! -f classes.dex ]; then
    echo "خطا: تولید classes.dex ناموفق بود."
    exit 1
fi

echo "[4/6] Packaging Android resources..."
aapt package -f -M AndroidManifest.xml -S res -I "$ANDROID_JAR" -F app.unsigned.apk

echo "[5/6] Adding DEX and native library..."
aapt add app.unsigned.apk classes.dex
aapt add app.unsigned.apk "lib/$ABI/lib$LIB_NAME.so"

echo "[6/6] Signing APK..."
if [ ! -f "$KEYSTORE" ]; then
    echo "Creating a new signing key..."
    echo "Keystore: $KEYSTORE"
    read -rsp "Enter a new keystore password (minimum 6 characters): " KEYSTORE_PASS
    echo
    if [ "${#KEYSTORE_PASS}" -lt 6 ]; then
        echo "خطا: رمز کلید باید حداقل ۶ کاراکتر باشد."
        exit 1
    fi
    export APK_KEYSTORE_PASS="$KEYSTORE_PASS"
    keytool -genkeypair -v -keystore "$KEYSTORE" -storepass "$KEYSTORE_PASS" -keypass "$KEYSTORE_PASS" -keyalg RSA -keysize 2048 -validity 10000 -alias "$KEY_ALIAS" -dname "CN=Termux, OU=Dev, O=MargYoorna, L=City, ST=State, C=US"
else
    echo "Using existing signing key..."
    read -rsp "Enter existing keystore password: " KEYSTORE_PASS
    echo
    export APK_KEYSTORE_PASS="$KEYSTORE_PASS"
fi

apksigner sign --ks "$KEYSTORE" --ks-key-alias "$KEY_ALIAS" --ks-pass env:APK_KEYSTORE_PASS --key-pass env:APK_KEYSTORE_PASS --out MyCGame.apk app.unsigned.apk
unset APK_KEYSTORE_PASS KEYSTORE_PASS

echo "Verifying APK signature..."
apksigner verify --verbose MyCGame.apk

echo
echo "=========================================="
echo "SUCCESS!"
echo "APK: $PROJECT_DIR/MyCGame.apk"
echo "=========================================="

cp -v "$PWD/MyCGame.apk" "$HOME/storage/downloads/MyCGame.apk"
echo "تم نقل الملف إلى مجلد التنزيلات بنجاح!"
