FROM openjdk:23-jdk-slim
ARG JAR_FILE=target/ExchangeGG-1.0.0.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]