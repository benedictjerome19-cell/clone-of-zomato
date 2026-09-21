# Stage 1: Build the WAR file using Maven and JDK 17
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Deploy the compiled WAR onto Apache Tomcat
FROM tomcat:9-jdk17
COPY --from=build /app/target/benedictjeromemart.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]