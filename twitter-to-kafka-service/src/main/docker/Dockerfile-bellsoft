FROM bellsoft/liberica-openjdk-alpine:11.0.16 as builder
WORKDIR application
ADD maven/${project.build.finalName}.jar ${project.build.finalName}.jar
RUN java -Djarmode=layertools -jar ${project.build.finalName}.jar extract

FROM bellsoft/liberica-openjdk-alpine:11.0.16

ENV SERVER_PORT 8080
EXPOSE 8080
RUN apk add --update jq && \
    rm -rf /var/cache/apk/*

HEALTHCHECK  --interval=3s --timeout=2s --start-period=5s --retries=30 \
    CMD wget localhost:8080/actuator/health -q -O - | jq --exit-status -n 'inputs | if has("status") then .status=="UP" else false end' > /dev/null || exit 1

LABEL PROJECT_NAME=${project.artifactId} \
      PROJECT=${project.id}

WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
RUN true #Fake layer to fix GitHub issue `layer does not exist`
COPY --from=builder application/application/ ./
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "org.springframework.boot.loader.JarLauncher"]