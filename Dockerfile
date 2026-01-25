# Dockerfile para inmobiliaria-backend (Spring Boot, Java 11)
FROM eclipse-temurin:11-jre as runtime

WORKDIR /app

# Copia el JAR generado por Maven (ajusta el nombre si es necesario)
COPY target/inmobiliaria-backend-0.0.1-SNAPSHOT.jar app.jar

# Puerto expuesto por la app
EXPOSE 8080

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "app.jar"]
