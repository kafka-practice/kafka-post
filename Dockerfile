# builder image
FROM amazoncorretto:17-al2-jdk AS builder

RUN mkdir /kafka-post
WORKDIR /kafka-post

COPY . .

RUN chmod +x gradlew
RUN ./gradlew clean bootJar

# runtime image
FROM amazoncorretto:17.0.12-al2

ENV TZ=Asia/Seoul
ENV PROFILE=${PROFILE}

RUN mkdir /kafka-post
WORKDIR /kafka-post

COPY --from=builder /kafka-post/build/libs/kafka-post-* /kafka-post/app.jar

CMD ["sh", "-c", " \
    java -Dspring.profiles.active=${PROFILE} \
         -jar /kafka-post/app.jar"]
