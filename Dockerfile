# Build stage
FROM maven:3.9.0-eclipse-temurin-19-focal AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src/ /app/src/
RUN mvn package -DskipTests

# Production stage
FROM eclipse-temurin:19-jdk-focal
WORKDIR /app
RUN echo "DB_HOST=$DB_HOST" > env.properties && \
    echo "DB_NAME=$DB_NAME" >> env.properties && \
    echo "DB_USER=$DB_USER" >> env.properties && \
    echo "DB_PASSWORD=$DB_PASSWORD" >> env.properties
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]