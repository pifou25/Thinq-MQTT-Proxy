#
# Build stage
#
FROM maven:3.8-eclipse-temurin-11 AS build
COPY . /home/app
RUN mvn -q -f /home/app/pom.xml clean package
# RUN mvn -f /home/app/pom.xml dependency:tree -Ddetail=true

#
# Package stage
#
FROM arm32v7/openjdk:11-jre-slim
COPY --from=build /home/app/target/thinq-mqtt-proxy.jar /usr/local/lib/thinq-mqtt-proxy.jar
WORKDIR /usr/local/lib
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/thinq-mqtt-proxy.jar","run"]
