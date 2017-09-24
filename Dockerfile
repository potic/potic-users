FROM openjdk:8

RUN mkdir -p /usr/src/potic-users && mkdir -p /usr/app

COPY build/distributions/* /usr/src/potic-users/

RUN unzip /usr/src/potic-users/potic-users-*.zip -d /usr/app/ && ln -s /usr/app/potic-users-* /usr/app/potic-users

WORKDIR /usr/app/potic-users

EXPOSE 8080
ENV ENVIRONMENT_NAME test
ENTRYPOINT [ "sh", "-c", "./bin/potic-users --spring.profiles.active=$ENVIRONMENT_NAME" ]
CMD []
