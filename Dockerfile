FROM amazoncorretto:11-alpine-jdk
COPY ./build/libs/mutual-marker-back-0.0.1.jar mutual-marker-back-0.0.1.jar
ENTRYPOINT ["java","-jar","/mutual-marker-back-0.0.1.jar"]