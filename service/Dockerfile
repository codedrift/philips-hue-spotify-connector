FROM gradle:4.2 AS appserver
USER root
WORKDIR /usr/src/app
COPY . .
ENV GRADLE_USER_HOME=/tmp/.gradle
RUN gradle -g /tmp --version
RUN gradle buildJar --stacktrace

FROM java:8-jdk-alpine
WORKDIR /app
COPY --from=appserver /usr/src/app/build/libs/application.jar .
ENTRYPOINT ["java", "-jar", "/app/application.jar"]
