FROM openjdk:11
ADD target/redis-test.jar redis-test.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "redis-test.jar"]