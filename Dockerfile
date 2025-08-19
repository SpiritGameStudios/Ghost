# escape=\
# syntax=docker/dockerfile:1

FROM openjdk:21-jdk-slim

RUN mkdir -p /bot/plugins
RUN mkdir -p /bot/data
RUN mkdir -p /dist/out

VOLUME [ "/bot/data" ]
VOLUME [ "/bot/plugins" ]

COPY [ "build/distributions/Ghost-1.0.0.tar", "/dist" ]

RUN tar -xf /dist/Ghost-1.0.0.tar -C /dist/out
RUN chmod +x /dist/out/Ghost-1.0.0/bin/Ghost

RUN rm /dist/Ghost-1.0.0.tar

WORKDIR /bot

ENV JAVA_OPTS="-XX:+UseZGC -XX:+ZGenerational -XX:+DisableExplicitGC"
ENTRYPOINT [ "/dist/out/Ghost-1.0.0/bin/Ghost" ]
