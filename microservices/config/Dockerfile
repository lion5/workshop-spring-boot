# builder stage
FROM gradle:8.3-jdk17 as builder

COPY . /app
WORKDIR /app
RUN gradle installBootDist

# Image stage
FROM alpine
RUN apk add -U --no-cache openjdk17-jre

COPY --from=builder /app/build/install/config-boot /config

WORKDIR /config
ENTRYPOINT ["./bin/config"]