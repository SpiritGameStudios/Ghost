FROM gradle:jdk21 AS builder
WORKDIR /app
COPY . /app/
RUN gradle installDist

FROM eclipse-temurin:21
WORKDIR /app
COPY --from=builder /app/build/install/Ghost ./
COPY ./config.json ./
CMD ["sh", "-c", "./bin/Ghost"]
