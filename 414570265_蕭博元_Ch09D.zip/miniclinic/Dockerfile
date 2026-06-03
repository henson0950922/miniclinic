FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# 明確複製 pom.xml
COPY pom.xml ./pom.xml

# 複製 src 與 .mvn（必須包含 mvnw 執行所需的 Maven wrapper）
COPY src ./src
COPY .mvn ./.mvn
COPY mvnw ./mvnw
COPY mvnw.cmd ./mvnw.cmd

# 使用非互動模式並跳過測試以縮短建置時間
RUN mvn -B clean package -DskipTests

# 執行階段只帶 JRE 與編譯好的一個 JAR
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/miniclinic-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
