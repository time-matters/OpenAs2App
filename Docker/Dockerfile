FROM davidcaste/alpine-java-unlimited-jce
# FROM openjdk:7

RUN apk update
RUN apk add zip

# install open-as2
RUN mkdir /as2
COPY OpenAS2Server-2.10.0.zip /as2/OpenAS2Server-2.10.0.zip
RUN unzip /as2/OpenAS2Server-2.10.0.zip -d /as2
RUN rm /as2/OpenAS2Server-2.10.0.zip

# install aws credentials
RUN mkdir /.aws
COPY credentials /.aws/credentials
COPY config.xml /as2/config/config.xml

RUN chmod +x /as2/bin/*

# test if server starts
# RUN /as2/bin/start-openas2.sh

