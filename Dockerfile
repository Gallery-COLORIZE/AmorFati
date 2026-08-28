# 1. Build Stage
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /workspace

# Gradle 래퍼 및 빌드 설정 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# 의존성 사전 캐싱
RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon || true

# 소스 코드 복사 및 실행 가능한 bootJar 빌드 (테스트는 CI 단계에서 선행 검증)
COPY src src
RUN ./gradlew bootJar --no-daemon -x test && \
    cp $(ls build/libs/*.jar | grep -v plain) app.jar

# 2. Runtime Stage
FROM eclipse-temurin:25-jre
WORKDIR /app

# 보안을 위한 비루트 사용자 설정
RUN groupadd -r amorfati && useradd -r -g amorfati amorfati

# 빌드된 애플리케이션 JAR 복사
COPY --from=builder /workspace/app.jar app.jar
RUN chown -R amorfati:amorfati /app

USER amorfati

EXPOSE 8080

# 컨테이너 메모리 최적화 옵션 적용
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
