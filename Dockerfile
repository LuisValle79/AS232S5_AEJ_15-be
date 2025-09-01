# Usamos una imagen base Alpine con JDK 17 (más ligera que slim)
FROM eclipse-temurin:17-jre-alpine

# Reducimos la superficie de ataque usando un usuario no root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Directorio dentro del contenedor
WORKDIR /app

# Copiamos solo el jar generado por Maven
COPY --chown=spring:spring target/*.jar app.jar

# Optimizaciones de JVM para contenedores
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]