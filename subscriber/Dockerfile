# syntax=docker/dockerfile:1

# Build stage: compile and package the shaded JAR
FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /app

# Leverage caching for dependencies
COPY pom.xml ./
RUN mvn -B -q -DskipTests=true dependency:go-offline

# Build the application
COPY src ./src
RUN mvn -B -q -DskipTests=true clean package

# Runtime stage: run with a slim JRE
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the shaded jar built in the previous stage
COPY --from=build /app/target/subscriber-1.0-SNAPSHOT.jar /app/app.jar

# Allow overriding JVM options at runtime
ENV JAVA_OPTS=""

# Expose an app port if applicable (uncomment and adjust if needed)
EXPOSE 50051

# Start the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
# docker buildx build -t alicejgibbons/subscriber:1.0 .
# docker push  alicejgibbons/subscriber:1.0
