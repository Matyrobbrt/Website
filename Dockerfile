FROM openjdk:20
COPY build/libs/Website-0.0.1-SNAPSHOT.jar /website.jar
VOLUME ["/home/site"]
WORKDIR /home/site
ENTRYPOINT ["java", "-Xmx2G", "-jar", "/website.jar"]