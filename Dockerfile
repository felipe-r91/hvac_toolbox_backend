# ---------- Build stage ----------
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy only files needed to resolve dependencies first
COPY pom.xml ./
RUN mvn -B -q -DskipTests dependency:go-offline

# Copy source after dependency cache step
COPY src ./src

# Build the jar
RUN mvn -B -q -DskipTests package


# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre

WORKDIR /app

# Create a non-root user
RUN useradd -r -u 1001 springuser

# Copy built jar
COPY --from=builder /app/target/*.jar app.jar

# Switch user
USER springuser

# Render injects PORT
EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-jar", "/app/app.jar"]