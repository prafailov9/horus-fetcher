# build from maven first, then copy the output in the container
FROM maven:3-eclipse-temurin-26 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q package -DskipTests


FROM eclipse-temurin:26-jre
WORKDIR /app
ENV JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
COPY --from=build /build/target/horus-fetcher-1.0-SNAPSHOT.jar hfr.jar
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/hfr.jar"]
