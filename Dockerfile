FROM openjdk:20
COPY build/libs/Website-0.0.1-SNAPSHOT.jar /website.jar
VOLUME ["/home/site"]
WORKDIR /home/site
EXPOSE map[3000/tcp:{}]
ENTRYPOINT ["java", "-Xmx2G", "-jar", "/website.jar"]
