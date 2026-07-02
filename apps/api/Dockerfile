FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends docker.io \
    && rm -rf /var/lib/apt/lists/* \
    && mkdir -p /tmp/ping

COPY --from=build /build/target/quarkus-app/lib/ ./lib/
COPY --from=build /build/target/quarkus-app/*.jar ./
COPY --from=build /build/target/quarkus-app/app/ ./app/
COPY --from=build /build/target/quarkus-app/quarkus/ ./quarkus/

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "quarkus-run.jar"]
