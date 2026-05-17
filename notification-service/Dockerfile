FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml ./
COPY shared-kernel/pom.xml shared-kernel/pom.xml
COPY appointment-service/pom.xml appointment-service/pom.xml
COPY notification-service/pom.xml notification-service/pom.xml
COPY patient-history-service/pom.xml patient-history-service/pom.xml
RUN mvn -q -pl notification-service -am dependency:go-offline
COPY . .
RUN mvn -q -pl notification-service -am package -DskipTests

FROM gcr.io/distroless/java21-debian12:nonroot
WORKDIR /app
COPY --from=build /workspace/notification-service/target/*.jar /app/app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
