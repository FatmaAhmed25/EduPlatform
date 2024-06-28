FROM openjdk:17-alpine
EXPOSE 8080
COPY ./target/backend.jar backend.jar
COPY ./src/main/resources/serviceAccountKey.json serviceAccountKey.json
ENTRYPOINT ["java", "-jar", "/backend.jar"]
