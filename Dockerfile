FROM amazoncorretto:24-jdk

WORKDIR /app

COPY /target/spring-ai-pr-reviewer-0.0.1-SNAPSHOT.jar spring-ai-pr-reviewer-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "/app/spring-ai-pr-reviewer-0.0.1-SNAPSHOT.jar"]