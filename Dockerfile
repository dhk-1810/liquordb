# ----- 0. Global Build Arguments -----
#ARG PROJECT_NAME="liquordb"

# ----- 1. Build Stage -----
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon # 의존성만 먼저 다운로드하여 캐싱 효율 극대화

# 프로젝트 정보 환경 변수 설정
# ENV JVM_OPTS=""

# 빌드 컨텍스트(현재 디렉터리)를 이미지 내부 /app으로 복사
COPY . .
RUN ./gradlew bootJar -x test --no-daemon # Gradle Wrapper를 실행 가능하게 하고, bootJar 태스크로 빌드 수행

# ----- 2. Run Stage -----
FROM amazoncorretto:17-al2-jdk
WORKDIR /app

ENV TZ=Asia/Seoul

# 최상단 ARG의 값을 이 스테이지로 가져옴.
#ARG PROJECT_NAME

# 실행 환경 변수 재정의
#ENV PROJECT_NAME=${PROJECT_NAME}

COPY --from=builder /app/build/libs/*.jar /app.jar

# 애플리케이션 실행 포트
EXPOSE 8080

# 컨테이너 시작 시 실행될 명령어
ENTRYPOINT ["java", "-Xmx384m", "-Duser.timezone=Asia/Seoul", "-jar", "/app.jar"]