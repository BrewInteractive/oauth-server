FROM eclipse-temurin:19-jdk-focal

RUN echo "DB_HOST=$DB_HOST" > env.properties && \
    echo "DB_NAME=$DB_NAME" >> env.properties && \
    echo "DB_USER=$DB_USER" >> env.properties && \
    echo "DB_PASSWORD=$DB_PASSWORD" >> env.properties

COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]