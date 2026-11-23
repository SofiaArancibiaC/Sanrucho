# ---------- Etapa 1: compilar con Maven ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# El contexto es la carpeta Backend completa (ver docker-compose.yml).
# .dockerignore excluye target/, .git y otros archivos innecesarios.
COPY . .

# Compila este modulo y sus dependencias (parent + common) desde cero.
# Asi el JAR siempre incluye las clases generadas por MapStruct/Lombok.
RUN mvn -B -pl ms-usuarios -am clean package -DskipTests

# ---------- Etapa 2: imagen final ----------
FROM eclipse-temurin:21-jre-alpine
LABEL maintainer="Sanrucho"
WORKDIR /app
COPY --from=build /workspace/ms-usuarios/target/*.jar app.jar
EXPOSE 9001
ENTRYPOINT ["java", "-jar", "app.jar"]
