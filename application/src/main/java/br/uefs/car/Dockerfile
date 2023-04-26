FROM openjdk

RUN mkdir /carro

WORKDIR /carro

COPY /target/carro-jar-with-dependencies.jar /carro
COPY /target/main-jar-with-dependencies.jar /carro

ENTRYPOINT ["java","-jar","main-jar-with-dependencies.jar"]