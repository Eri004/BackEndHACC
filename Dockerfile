####
# Dockerfile para Railway - Quarkus 3.36 + Java 21
#
# Build:
#   railway up (Railway detecta el Dockerfile automaticamente)
#
# Variables de entorno necesarias en Railway:
#   - PGHOST, PGPORT, PGDATABASE, PGUSER, PGPASSWORD (plugin Postgres)
#   - CORS_ALLOWED_ORIGINS (URL del frontend en Vercel)
#   - PORT (Railway lo inyecta automaticamente)
###

FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Cache de dependencias
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline

# Codigo fuente
COPY src ./src
RUN ./mvnw -B -DskipTests package

# Runtime
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/quarkus-app /app/quarkus-app

ENV JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/app/quarkus-app/quarkus-run.jar"

EXPOSE 8080
ENTRYPOINT ["/opt/java/openjdk/bin/java", "-jar", "/app/quarkus-app/quarkus-run.jar"]
