# builder stage
FROM gradle:6.7.0-jdk11 as builder

COPY . /app
WORKDIR /app
RUN gradle installBootDist

# Image stage
FROM alpine
RUN apk add -U --no-cache openjdk11-jre

COPY --from=builder /app/build/install/config-boot /config

WORKDIR /config
ENTRYPOINT ["./bin/config"]