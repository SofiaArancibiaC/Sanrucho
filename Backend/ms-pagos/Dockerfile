FROM eclipse-temurin:21-jre-alpine
LABEL maintainer="Sanrucho"
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 9004
ENTRYPOINT ["java", "-jar", "app.jar"]
