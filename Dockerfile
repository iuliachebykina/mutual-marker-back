FROM adoptopenjdk:11-jre-hotspot
COPY ./build/libs/mutual-marker-back-0.0.1.jar mutual-marker-back-0.0.1.jar
ENTRYPOINT ["java","-jar","/mutual-marker-back-0.0.1.jar"]