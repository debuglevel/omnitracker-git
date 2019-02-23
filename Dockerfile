#FROM openjdk:8-jdk-alpine AS builder
#COPY . /src/
#WORKDIR /src/
#RUN ./gradlew build

## Building stage
FROM openjdk:8-jdk-alpine AS builder
WORKDIR /src/

# cache gradle
COPY gradle /src/gradle
COPY gradlew /src/
# run "gradle --version" to let gradle-wrapper download gradle
RUN ./gradlew --version

# build source
COPY . /src/
RUN ./gradlew build

## Final image
FROM openjdk:8-jre-alpine
RUN apk add bash
RUN mkdir /app
COPY --from=builder /src/build/libs/*.jar /app/omnitracker-git.jar
COPY docker-entrypoint.sh /app/
WORKDIR /app

#CMD ["java", "-jar", "/app/omnitracker-git.jar"]
CMD ["./docker-entrypoint.sh"]