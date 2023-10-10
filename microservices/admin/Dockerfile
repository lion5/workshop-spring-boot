# builder stage
FROM gradle:8.3-jdk17 as builder

COPY . /app
WORKDIR /app
RUN gradle installBootDist

# Image stage
FROM alpine
RUN apk update && apk add bash
RUN apk add -U --no-cache openjdk17-jre

COPY --from=builder /app/build/install/admin-boot /admin

# waiting for eureka as a service registry
WORKDIR /admin
COPY ./wait-for-it.sh .
CMD ["./wait-for-it.sh", "eureka:8761", "-t", "300", "--", "./bin/admin"]