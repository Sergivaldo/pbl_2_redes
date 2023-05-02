FROM openjdk

RUN mkdir /carro

WORKDIR /carro

COPY /target /carro

ENTRYPOINT ["java","-jar","main-jar-with-dependencies.jar"]