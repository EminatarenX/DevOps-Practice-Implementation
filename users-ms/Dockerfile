FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . . 
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17
WORKDIR /app
COPY --from=builder /app/target/users-ms-0.0.1-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]