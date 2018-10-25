FROM maven:alpine

RUN apk add ttf-dejavu
RUN mkdir -p /data
WORKDIR /data
