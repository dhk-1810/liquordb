# ----- 0. Global Build Arguments -----
#ARG PROJECT_NAME="liquordb"

# ----- 1. Build Stage: JDK 도구들로 실행 파일 생성 -----
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

# 설정 파일만 먼저 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x ./gradlew # 파일에 실행 권한 부여
RUN ./gradlew dependencies --no-daemon # 의존성만 먼저 다운로드하여 캐싱 효율 극대화. 도커 빌드시엔 일회성 백그라운드 프로세스인 daemon 불필요.

# 실제 소스코드 복사
COPY . .
# Gradle Wrapper를 실행 가능하게 하고, bootJar 태스크로 빌드 수행
RUN ./gradlew bootJar -x test --no-daemon

# ----- 2. Run Stage -----
# 실제 서버가 들어갈 가벼운 Java 실행환경 이미지
FROM amazoncorretto:17-al2-jdk
WORKDIR /app

ENV TZ=Asia/Seoul
# 1단계의 .jar 파일을 가져옴
COPY --from=builder /app/build/libs/*.jar /app.jar

# 애플리케이션 실행 포트
EXPOSE 8080

# 컨테이너 시작 시 실행될 최종 명령어
ENTRYPOINT ["java", "-Xmx384m", "-Duser.timezone=Asia/Seoul", "-jar", "/app.jar"]