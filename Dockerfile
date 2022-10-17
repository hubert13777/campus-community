FROM java:8
MAINTAINER hubert
VOLUME log
ADD campus-community-0.5.1-SNAPSHOT.jar app.jar
RUN sh -c 'touch app.jar'
RUN echo "Asia/Shanghai" > /etc/timezone
ENV JAVA_OPTS=""
CMD java -jar app.jar --spring.profiles.active=pro