FROM openjdk:8

RUN mkdir -p /usr/src/pocket-square-users
RUN mkdir -p /usr/app

COPY build/distributions/* /usr/src/pocket-square-users/

RUN unzip /usr/src/pocket-square-users/pocket-square-users-*.zip -d /usr/app/
RUN ln -s /usr/app/pocket-square-users-* /usr/app/pocket-square-users

WORKDIR /usr/app/pocket-square-users

EXPOSE 8080
ENTRYPOINT ["./bin/pocket-square-users"]
CMD []
