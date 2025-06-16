FROM openjdk:21
COPY target/bank-transaction-app-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]