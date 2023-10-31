# builder stage
FROM gradle:8.3-jdk17 as builder

COPY . /app
WORKDIR /app
RUN gradle installBootDist

# Image stage
FROM alpine
RUN apk update && apk add bash
RUN apk add -U --no-cache openjdk17-jre

COPY --from=builder /app/build/install/eureka-boot /eureka
WORKDIR /eureka

# check for the config server to start up in the docker compose use case
COPY ./wait-for-it.sh .
CMD ["./wait-for-it.sh", "config:8888", "-t", "300", "--", "./bin/eureka"]