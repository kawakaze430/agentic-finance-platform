FROM maven:3.9.11-eclipse-temurin-17 AS build
WORKDIR /source
COPY pom.xml ./
RUN mvn -B -ntp dependency:go-offline
COPY src ./src
RUN mvn -B -ntp verify

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN mkdir -p /app/data && chown -R 10001:10001 /app
COPY --from=build --chown=10001:10001 /source/target/agentic-finance-platform-1.0.0.jar /app/application.jar
USER 10001:10001
EXPOSE 8080
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+ExitOnOutOfMemoryError"
ENTRYPOINT ["java","-jar","/app/application.jar"]
