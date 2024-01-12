FROM openjdk:17-alpine

COPY "build/libs/hs-notifications-*.jar" application.jar

ENTRYPOINT ["java", "-jar", "application.jar"]
