FROM gradle:jdk21
WORKDIR /app
RUN "./gradlew installDist"
COPY ./build /app/build

FROM eclipse-temurin:21
WORKDIR /app
COPY --from=builder /app/build/install/Ghost ./
COPY ./config.json ./
CMD ["sh", "-c", "./bin/Ghost"]
