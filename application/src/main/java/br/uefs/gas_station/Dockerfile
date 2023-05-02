FROM openjdk

RUN mkdir /postto

WORKDIR /posto

COPY /target/posto-jar-with-dependencies.jar /carro
COPY /target/main-jar-with-dependencies.jar /carro

ENTRYPOINT ["java","-jar","main-jar-with-dependencies.jar"]