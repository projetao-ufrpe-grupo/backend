# Build stage
FROM maven:3.8-openjdk-17 AS build

WORKDIR /app
COPY ./src ./src

COPY pom.xml .
RUN mvn -f /app/pom.xml clean package -DskipTests=true

# -----------------------------------------------------

    
# Package stage
FROM openjdk:21-ea-17-slim-buster as production
COPY --from=build /app/target/java-spring-boot-boilerplate-0.0.1-SNAPSHOT.jar /usr/local/lib/app.jar

EXPOSE 8080
CMD ["java", "-jar", "/usr/local/lib/app.jar"]
