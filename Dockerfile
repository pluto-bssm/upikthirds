# 1. 빌드용 JDK 이미지
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Gradle wrapper
COPY gradlew .
COPY gradle gradle
RUN chmod +x gradlew

# 전체 소스 복사
COPY . .

# 실행 가능한 Boot JAR만 빌드 (테스트 제외)
RUN ./gradlew clean bootJar -x test

# 빌드된 실행 JAR 파일 복사 (plain JAR 제외)
RUN mkdir -p /app/target/ && \
    cp $(find /app/build/libs/ -name "*SNAPSHOT.jar" ! -name "*-plain.jar" -print -quit) /app/target/app.jar

# 2. 실행용 JRE 이미지
FROM eclipse-temurin:21-jre

WORKDIR /app

# 빌드된 단일 JAR 복사 (와일드카드 사용 안함)
COPY --from=build /app/target/app.jar app.jar

# Cloud Run 환경을 위한 PORT 환경 변수 설정
ENV PORT 8080

# 포트 노출
EXPOSE ${PORT}

# 실행 커맨드 (서버 포트 설정 포함)
ENTRYPOINT ["java", "-Dserver.port=${PORT}", "-jar", "app.jar"]
