FROM openjdk:23-slim
WORKDIR /app
COPY target/bot-1.0.jar bot.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "bot.jar"]
