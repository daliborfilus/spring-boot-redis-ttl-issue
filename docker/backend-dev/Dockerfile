FROM adoptopenjdk:11-jdk-openj9
USER root
RUN mkdir -p /opt/app

ENV GRADLE_USER_HOME=/opt/.gradle
ENV JAVA_OPTS -ea -Dfile.encoding=UTF-8 -Djava.security.egd=file:/dev/./urandom -noverify -XX:TieredStopAtLevel=1

# create local user with the same UID as our volume is running
# this user will be used to run gradle
ARG LOCAL_UID=1000
RUN groupadd -g $LOCAL_UID app && \
    useradd -r -u $LOCAL_UID -g app app

WORKDIR /opt/backend

#RUN apt-get update && apt-get install -y sudo && rm -rf /var/lib/apt/lists/*

COPY ./docker/backend-dev/entrypoint.sh /
RUN chmod +x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]

VOLUME /opt/backend
VOLUME /opt/.gradle

EXPOSE 8080

#CMD ["./gradlew", "bootRun", "--no-daemon"]
CMD ["bootRun"]
