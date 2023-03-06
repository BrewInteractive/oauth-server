FROM eclipse-temurin:19-jdk-focal
VOLUME /tmp
COPY env.properties env.properties
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]