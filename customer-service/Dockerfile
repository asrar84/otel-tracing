FROM adoptopenjdk/openjdk11:s390x-ubi-minimal-jre-11.0.16.1_1
ARG JAR_FILE=target/*.jar
ARG OTEL_JAVA_AGENT_JAR_FILE=opentelemetry-javaagent.jar
COPY ${JAR_FILE} application.jar
EXPOSE 8081
ENTRYPOINT ["java","-Dotel.service.name=customer-service","-jar","/application.jar"]