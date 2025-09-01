FROM openjdk:21-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일 복사 (gradle build 후 생성되는 파일)
COPY build/libs/*.jar app.jar

# 포트 노출 (Spring Boot 기본 포트)
EXPOSE 8180

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
