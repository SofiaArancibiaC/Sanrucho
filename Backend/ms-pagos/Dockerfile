# ---------- Etapa 1: compilar con Maven ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# El contexto es la carpeta Backend completa (ver docker-compose.yml).
COPY . .

RUN mvn -B -pl ms-pagos -am clean package -DskipTests

# ---------- Etapa 2: imagen final ----------
FROM eclipse-temurin:21-jre-alpine
LABEL maintainer="Sanrucho"
WORKDIR /app
COPY --from=build /workspace/ms-pagos/target/*.jar app.jar
EXPOSE 9004
ENTRYPOINT ["java", "-jar", "app.jar"]
