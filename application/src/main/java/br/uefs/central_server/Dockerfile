FROM openjdk

RUN mkdir /central

WORKDIR /central

COPY /target /central

ENTRYPOINT ["java","-jar","main-jar-with-dependencies.jar"]