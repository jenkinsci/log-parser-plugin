FROM maven:alpine

RUN apk update && apk upgrade && \
  apk --update add fontconfig ttf-dejavu bash git openssh openjdk8-jre 
RUN mkdir -p /data
WORKDIR /data
