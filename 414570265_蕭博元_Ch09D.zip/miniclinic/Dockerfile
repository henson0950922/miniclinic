# === 第一階段：編譯環境 ===
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# 1. 為了防止 .dockerignore 誤殺，我們不用 COPY . .
# 2. 直接精準複製編譯必備的檔案
COPY pom.xml ./
COPY src ./src

# 如果你專案根目錄有 .mvn 夾或 mvnw，也可以順便複製（沒有也沒關係）
# COPY .mvn ./.mvn
# COPY mvnw ./

# 執行編譯並跳過測試
RUN mvn clean package -DskipTests

# === 第二階段：運行環境 ===
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 從第一階段將打包好的 jar 檔複製過來
COPY --from=build /app/target/*.jar app.jar

# 確保 SQLite 有地方放（使用記憶體或臨時目錄，防止唯讀報錯）
# 這裡先保留給 Java 執行
EXPOSE 8080

CMD ["java", "-jar", "app.jar"]