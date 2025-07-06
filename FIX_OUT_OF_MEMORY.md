# Sửa lỗi OutOfMemoryError khi build

## Đã thực hiện các bước sau:

### 1. Tăng bộ nhớ JVM trong `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8
org.gradle.daemon=true
org.gradle.configureondemand=true
```

### 2. Dừng và khởi động lại Gradle daemon:
```bash
./gradlew --stop
```

### 3. Clean dự án:
```bash
./gradlew clean
```

## Bây giờ thử build lại:

```bash
# Build một phiên bản để test
./gradlew assembleDevDebug

# Hoặc build tất cả
./gradlew assembleDebug
```

## Nếu vẫn gặp lỗi, thử thêm:

### 1. Tăng thêm bộ nhớ (nếu máy có đủ RAM):
Trong `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx6144m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8
```

### 2. Build từng flavor riêng lẻ:
```bash
./gradlew assembleDevDebug
./gradlew assembleStagingDebug  
./gradlew assembleProdDebug
```

### 3. Trong Android Studio:
- File → Settings → Build, Execution, Deployment → Compiler
- Tăng "Heap size" lên 4096 MB hoặc cao hơn

### 4. Tạm thời disable một số tính năng để giảm bộ nhớ:
Trong `app/build.gradle.kts`:
```kotlin
buildTypes {
    debug {
        isMinifyEnabled = false  // Tắt minify cho debug
        isShrinkResources = false
    }
}
```

### 5. Nếu sử dụng máy có ít RAM (<8GB):
- Đóng các ứng dụng khác
- Restart Android Studio
- Build từ command line thay vì từ IDE