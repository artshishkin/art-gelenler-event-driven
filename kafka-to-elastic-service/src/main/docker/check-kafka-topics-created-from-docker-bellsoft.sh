#!/bin/sh
# check-kafka-topics-created-bellsoft.sh

kafkacatResult=$(kcat -L -b ${KAFKA_BROKER})

echo "kafkacat result:" $kafkacatResult

while [[ ! $kafkacatResult == *"twitter-topic"* ]]; do
  >&2 echo "Kafka topic has not been created yet!"
  sleep 2
  kafkacatResult=$(kcat -L -b ${KAFKA_BROKER})
done

java -Djava.security.egd=file:/dev/./urandom org.springframework.boot.loader.JarLauncher