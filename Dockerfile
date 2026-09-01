FROM docker.io/maven:3.9-eclipse-temurin-17-alpine

WORKDIR /app

# Copy entire repo
COPY . .

# Expose server port
ENV PORT=5050
EXPOSE 5050

# Run Server class directly using Maven
CMD ["java", "-jar", "target/robot-world-0.0.2-jar-with-dependencies.jar", "-p", "5050"]
