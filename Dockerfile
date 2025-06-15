# Build stage
FROM maven:3.8-openjdk-17 AS build

WORKDIR /app
COPY ./src ./src
COPY pom.xml .

RUN mvn -f /app/pom.xml clean package -DskipTests=true \
    && cp /app/target/*.jar /app/app.jar

# -----------------------------------------------------

# Package stage
FROM openjdk:21-ea-17-slim-buster AS production

COPY --from=build /app/app.jar /usr/local/lib/app.jar

EXPOSE 8080
CMD ["java", "-jar", "/usr/local/lib/app.jar"]