# ---- Stage 1: Build ----
# Use a specific, reproducible Maven image with Java 21 to build the application.
# Using a full JDK image here is acceptable as it's only for the build stage.
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set the working directory
WORKDIR /app

# Copy pom.xml first to leverage Docker's layer caching for dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Build the application, skipping tests as they should be run in a previous CI step
RUN mvn clean package -DskipTests


# ---- Stage 2: Production Run ----
# Use a minimal, secure "distroless" image containing only the JRE.
# This drastically reduces image size and attack surface.
FROM gcr.io/distroless/java21-debian12

# Create a dedicated, unprivileged group and user to run the application securely
#RUN groupadd --system appgroup && useradd --system --gid appgroup appuser

# Set the working directory
WORKDIR /app

# Copy the built JAR from the 'build' stage using a wildcard for robustness
COPY --from=build /app/target/*.jar ./app.jar

# Set ownership of the application directory to the new user
#RUN chown -R appuser:appgroup /app

# Switch to the non-root user
#USER appuser

# Expose the application port (for documentation purposes)
EXPOSE 8080

# Add a health check (requires Spring Boot Actuator dependency).
# Note: Distroless images do not have a shell or tools like curl/wget by default.
# For a robust health check here, you would typically COPY a static curl binary
# from the build stage or use a small, self-contained health check utility.
# HEALTHCHECK --interval=30s --timeout=5s --start-period=15s \
#   CMD ["/path/to/your/healthcheck-tool", "http://localhost:8080/actuator/health"]

# The command to run the application with production-ready JVM options.
# -XX:MaxRAMPercentage=80.0 is a modern setting to respect container memory limits.
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=80.0", "-jar", "app.jar"]