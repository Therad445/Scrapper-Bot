FROM openjdk:23-slim

# Install netcat for port check
RUN apt-get update \
 && apt-get install -y --no-install-recommends netcat-openbsd \
 && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY target/scrapper-1.0.jar scrapper.jar
EXPOSE 8081

HEALTHCHECK --interval=10s --timeout=5s --start-period=10s --retries=3 \
  CMD nc -z localhost 8081 || exit 1

ENTRYPOINT ["java", "-jar", "scrapper.jar"]
