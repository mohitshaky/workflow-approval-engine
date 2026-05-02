FROM openjdk:11-jre-slim
WORKDIR /app
COPY target/workflow-engine-1.0.0.jar app.jar
RUN mkdir -p /app/logs
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
