FROM openjdk

RUN mkdir /local

WORKDIR /local

COPY /target /local

ENTRYPOINT ["java","-jar","main-jar-with-dependencies.jar"]