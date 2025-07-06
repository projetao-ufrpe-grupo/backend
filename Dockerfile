# Build stage
FROM maven:3.8-openjdk-17 AS build

WORKDIR /app
COPY ./src ./src
COPY pom.xml .

RUN mvn -f /app/pom.xml clean package -DskipTests=true \
    && cp /app/target/*.jar /app/app.jar

# -----------------------------------------------------

# Development stage
FROM maven:3.8-openjdk-17 AS development

WORKDIR /app
COPY pom.xml .
COPY src ./src

CMD ["mvn", "spring-boot:run", "-Dspring-boot.run.jvmArguments='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005'"]

# -----------------------------------------------------

# Package stage
FROM eclipse-temurin:17-jre-jammy AS production

COPY --from=build /app/app.jar /usr/local/lib/app.jar

EXPOSE 8080
CMD ["java", "-jar", "/usr/local/lib/app.jar"]