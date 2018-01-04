FROM openjdk:8

RUN mkdir -p /usr/src/potic-users && mkdir -p /opt

COPY build/distributions/* /usr/src/potic-users/

RUN unzip /usr/src/potic-users/potic-users-*.zip -d /opt/ && ln -s /opt/potic-users-* /opt/potic-users

WORKDIR /opt/potic-users

EXPOSE 8080
ENV ENVIRONMENT_NAME test
ENTRYPOINT [ "sh", "-c", "./bin/potic-users --spring.profiles.active=$ENVIRONMENT_NAME" ]
CMD []
