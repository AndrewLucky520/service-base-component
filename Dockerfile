FROM openjdk:8-jre
MAINTAINER QiMing Mei <meiqiming@talkweb.com.cn>

ARG JAR_FILE
ENV JAVA_OPTS="-server -Xms1024m -Xmx1024m"

#没有注册中心的可以去掉--eureka.client.serviceUrl.defaultZone=${EUREKA_SERVER_ADDRESS}
ENTRYPOINT java ${JAVA_OPTS} -jar /usr/share/myservice/myservice.jar --spring.profiles.active=${PROFILE} \
 --eureka.instance.hostname=${DOMAIN_NAME} --eureka.instance.prefer-ip-address=false \
 --server.port=${CONTAINER_PORT} --eureka.client.serviceUrl.defaultZone=${EUREKA_SERVER_ADDRESS}

ADD target/${JAR_FILE} /usr/share/myservice/myservice.jar