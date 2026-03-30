FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml ./
COPY src ./src

RUN mvn -B clean package

FROM tomcat:10.1.42-jdk21-temurin
ENV CATALINA_OPTS="-Djava.security.egd=file:/dev/./urandom"

RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /workspace/target/balance-portal-servlet.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
