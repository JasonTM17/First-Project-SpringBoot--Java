# Start with a base image containing Java runtime and Maven
FROM maven:3.8.4-openjdk-17 as build

# Copy the project files into the container
COPY src /nguyenson/spring-mvc/src
COPY pom.xml /nguyenson/spring-mvc

# Set the working directory
WORKDIR /nguyenson/spring-mvc

# Build the application as a WAR file and skip tests
RUN mvn clean package -DskipTests

FROM openjdk:17-slim

# Copy WAR file to the webapps directory in Tomcat
# Ensure that the path to the WAR file matches the actual file name generated
COPY --from=build /nguyenson/spring-mvc/target/*.war /nguyenson/spring-mvc/app.war

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/nguyenson/spring-mvc/app.war"]

