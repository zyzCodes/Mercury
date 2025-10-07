# Use an official Gradle image with JDK 21 to build the application
FROM gradle:jdk21 AS build

# Set the working directory inside the container
WORKDIR /home/gradle/project

# Copy the build configuration files
COPY build.gradle settings.gradle ./

# Copy the source code
COPY src ./src

# Build the application and create an executable JAR file
RUN gradle clean build -x test --no-daemon

# Use a minimal image with just the Java Runtime Environment
FROM eclipse-temurin:21-jre-jammy

# Set the working directory
WORKDIR /app

# Copy the executable JAR from the build stage to the final image
COPY --from=build /home/gradle/project/build/libs/GoalsManager-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the application will run on
EXPOSE 8080

# Command to run the application when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]

