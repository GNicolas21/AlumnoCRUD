FROM amazoncorretto:25-alpine
COPY target/AlumnoCRUD.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
