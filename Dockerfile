#
# build with maven image:
#
FROM maven:3.8-eclipse-temurin-11 AS build
COPY apps /home/app/apps
COPY drivers /home/app/drivers
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -q -f /home/app/pom.xml clean package

FROM eclipse-temurin:11
COPY --from=build /home/app/target/thinq-mqtt-proxy.jar /home/app/thinq-mqtt-proxy.jar
WORKDIR /home/app
EXPOSE 8080
ENTRYPOINT ["java","-jar","/home/app/thinq-mqtt-proxy.jar"]
CMD ["run"]
