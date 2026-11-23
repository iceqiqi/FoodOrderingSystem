# 用户服务 / OAuth2 认证服务器（端口 8004）
# 前置：先在项目根目录执行  mvn clean package -DskipTests  生成 target/user-1.0-SNAPSHOT.jar
FROM eclipse-temurin:8-jre

ENV TZ=Asia/Shanghai \
    LANG=C.UTF-8 \
    LC_ALL=C.UTF-8

WORKDIR /app

COPY target/user-1.0-SNAPSHOT.jar app.jar

EXPOSE 8004

ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-Duser.timezone=GMT+8", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]
