FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml ./
RUN mvn -q -DskipTests=true dependency:go-offline
COPY . .
RUN mvn -DskipTests=true package

# ===== RUN =====
FROM tomcat:11.0.4-jdk17-temurin
# Xoá app mặc định
RUN rm -rf /usr/local/tomcat/webapps/*
# Deploy WAR làm ROOT.war
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

# Thư mục để mount secret/file
RUN mkdir -p /secrets
ENV GOOGLE_APPLICATION_CREDENTIALS=/secrets/firebase.json

EXPOSE 8080
CMD ["catalina.sh", "run"]

root@rancher:~/SmartLearning-System/javaSpring#