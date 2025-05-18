# Primeira etapa: Construir a aplicação
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests


# Etapa 2: Construção da imagem final
FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=build /app/target/order-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]