# === 第一階段：編譯環境 ===
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# 根據你 GitHub 的真實路徑，精準複製子目錄內的東西
COPY "414570265_蕭博元_Ch09D.zip/miniclinic/pom.xml" ./
COPY "414570265_蕭博元_Ch09D.zip/miniclinic/src" ./src

# 執行編譯打包（跳過測試避免資料庫干擾）
RUN mvn clean package -DskipTests

# === 第二階段：運行環境 ===
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 從第一階段將打包好的 jar 檔複製過來
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]