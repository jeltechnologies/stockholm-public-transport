# Builder stage
FROM maven:3.9-eclipse-temurin-25 AS builder

WORKDIR /app

COPY stockholm-public-transport/mvnw .
COPY stockholm-public-transport/pom.xml .

# Cache dependencies
RUN mvn dependency:go-offline -B

COPY stockholm-public-transport/src ./src

RUN mvn package -DskipTests -B

# Runtime stage - updated to Java 25
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 18107

ENTRYPOINT ["java", "-jar", "app.jar"]
