#
# build with maven image:
# docker run -it --rm --name my-maven-project -v "$PWD":/usr/src/mymaven -w /usr/src/mymaven maven:3.8-openjdk-11 mvn clean install
#
FROM maven:3.8-eclipse-temurin-11 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -q -f /home/app/pom.xml clean package
# RUN mvn -f /home/app/pom.xml dependency:tree -Ddetail=true

#
# run with java image:
# docker run --rm -ti --name thinq-mqtt -v "$PWD":/opt/app -w /opt/app eclipse-temurin:11 java -jar target/thinq-mqtt-proxy.jar init
#
FROM eclipse-temurin:11
COPY --from=build /home/app/target/thinq-mqtt-proxy.jar /home/app/thinq-mqtt-proxy.jar
WORKDIR /home/app
COPY state-example.json state.json
EXPOSE 8080
ENTRYPOINT ["java","-jar","/home/app/thinq-mqtt-proxy.jar"]
CMD ["java","-jar","/home/app/thinq-mqtt-proxy.jar","run"]


# docker run -it --rm --name my-maven-project -v C:\Users\LENOVO\Documents\GitHub\Thinq-MQTT-Proxy:/usr/src/mymaven -w /usr/src/mymaven maven:3.8-openjdk-11 mvn clean install
# docker run --rm -ti --name thinq-mqtt -v C:\Users\LENOVO\Documents\GitHub\Thinq-MQTT-Proxy\target:/opt/app -w /opt/app eclipse-temurin:11 java -jar thinq-mqtt-proxy.jar init
