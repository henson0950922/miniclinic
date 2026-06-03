FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# 先單獨複製 pom.xml，加速依賴快取
COPY pom.xml .

# 再複製整個專案原始碼
COPY . .

# 使用非互動模式並跳過測試以縮短建置時間
RUN mvn -B clean package -DskipTests

# 執行階段只帶 JRE 與編譯好的一個 JAR
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/miniclinic-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
