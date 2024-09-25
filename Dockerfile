# Use a lightweight JDK 21 base image
FROM openjdk:21-jdk

# Set the working directory
WORKDIR /app

# Copy the build artifacts to the container
COPY ./build/libs/smartfactory-0.0.1-SNAPSHOT.jar /app/smartfactory.jar

# Expose the port your application will run on
EXPOSE 8080

# Define the entry point for the container
CMD ["java", "-jar", "smartfactory.jar"]
