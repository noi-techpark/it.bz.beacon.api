FROM maven:3-jdk-8-alpine

ARG JENKINS_GROUP_ID=2000
ARG JENKINS_USER_ID=2000

RUN addgroup -g $JENKINS_GROUP_ID jenkins && \
    adduser -D -u $JENKINS_USER_ID -G jenkins jenkins

COPY infrastructure/docker/entrypoint-java.sh /entrypoint-java.sh

ENTRYPOINT [ "/entrypoint-java.sh" ]
