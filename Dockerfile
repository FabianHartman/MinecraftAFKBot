FROM maven:3.8.1-adoptopenjdk-16 as builder
WORKDIR /srv/build
COPY . .
RUN mvn package

FROM adoptopenjdk/openjdk16:alpine
COPY --from=builder /srv/build/target/MinecraftAFKBot-*.jar /usr/lib/afkboy/MinecraftAFKBot.jar
COPY ./docker/docker-entrypoint.sh /
COPY docker/afk-bot /usr/bin/afk-bot
RUN apk add bash jq
ENV MC_SERVER=127.0.0.1
ENV MC_PORT=25565
ENV MC_PROTOCOL=AUTOMATIC
ENV MC_ONLINE_MODE=true
ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["fishing-bot"]
