FROM maven:3.9-eclipse-temurin-8 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM tomcat:9.0-jdk8-temurin

RUN rm -rf /usr/local/tomcat/webapps/*

COPY --from=build /app/target/hostel.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 10000

CMD ["sh", "-c", "sed -i 's/port=\"8080\"/port=\"${PORT:-10000}\"/' /usr/local/tomcat/conf/server.xml && sed -i 's/port=\"8005\"/port=\"-1\"/' /usr/local/tomcat/conf/server.xml && catalina.sh run"]