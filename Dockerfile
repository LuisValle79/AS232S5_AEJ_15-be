# Usamos una imagen base Alpine con JDK 17 (más ligera que slim)
FROM eclipse-temurin:17-jdk-alpine AS builder

# Instalamos maven
RUN apk add --no-cache maven

# Directorio dentro del contenedor
WORKDIR /app

# Copiamos los archivos de configuración de Maven
COPY pom.xml .
COPY .mvn .mvn

# Copiamos el código fuente
COPY src src

# Construimos la aplicación
RUN mvn clean package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:17-jre-alpine

# Reducimos la superficie de ataque usando un usuario no root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Directorio dentro del contenedor
WORKDIR /app

# Copiamos solo el jar generado
COPY --from=builder --chown=spring:spring /app/target/*.jar app.jar

# Exponemos el puerto (flexible según configuración)
EXPOSE 8080

# Optimizaciones de JVM para contenedores
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]