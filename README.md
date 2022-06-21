[![CircleCI](https://circleci.com/gh/artshishkin/art-gelenler-event-driven.svg?style=svg)](https://circleci.com/gh/artshishkin/art-gelenler-event-driven)
[![codecov](https://codecov.io/gh/artshishkin/art-gelenler-event-driven/branch/main/graph/badge.svg?token=U5YRYVEM7N)](https://codecov.io/gh/artshishkin/art-gelenler-event-driven)
![Java CI with Maven](https://github.com/artshishkin/art-gelenler-event-driven/workflows/Java%20CI%20with%20Maven/badge.svg)
[![GitHub issues](https://img.shields.io/github/issues/artshishkin/art-gelenler-event-driven)](https://github.com/artshishkin/art-gelenler-event-driven/issues)
![Spring Boot version][springver]
![Project licence][licence]

# art-gelenler-event-driven
Tutorial -  Event-Driven Microservices: Spring Boot, Kafka and Elastic - from Ali Gelenler (Udemy)

### Section 3: First service: twitter-to-kafka-service - How to use Twitter4j & Kafka producers

#### 13. Running Apache Kafka cluster with docker: Kafka, Zookeeper and Schema Registry

1. Start kafka cluster
   - `docker-compose -f common.yml -f kafka_cluster.yml up`
2. Kafkacat command line
   - `docker run --tty --network host confluentinc/cp-kafkacat kafkacat -b localhost:29092 -L`
   - `-L` - list
   - `-b` - brokers
   - [Kafkacat docker commands](https://hub.docker.com/r/confluentinc/cp-kafkacat)

[springver]: https://img.shields.io/badge/dynamic/xml?label=Spring%20Boot&query=%2F%2A%5Blocal-name%28%29%3D%27project%27%5D%2F%2A%5Blocal-name%28%29%3D%27parent%27%5D%2F%2A%5Blocal-name%28%29%3D%27version%27%5D&url=https%3A%2F%2Fraw.githubusercontent.com%2Fartshishkin%2Fart-gelenler-event-driven%2Fmaster%2Fpom.xml&logo=Spring&labelColor=white&color=grey
[licence]: https://img.shields.io/github/license/artshishkin/art-gelenler-event-driven.svg

