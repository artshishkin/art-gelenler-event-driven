[![CircleCI](https://circleci.com/gh/artshishkin/art-gelenler-event-driven.svg?style=svg)](https://circleci.com/gh/artshishkin/art-gelenler-event-driven)
[![codecov](https://codecov.io/gh/artshishkin/art-gelenler-event-driven/branch/main/graph/badge.svg?token=U5YRYVEM7N)](https://codecov.io/gh/artshishkin/art-gelenler-event-driven)
![Java CI with Maven](https://github.com/artshishkin/art-gelenler-event-driven/workflows/Java%20CI%20with%20Maven/badge.svg)
[![GitHub issues](https://img.shields.io/github/issues/artshishkin/art-gelenler-event-driven)](https://github.com/artshishkin/art-gelenler-event-driven/issues)
![Spring Boot version][springver]
![Twitter4J][twitter4j]
![Kafka][kafka]
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

#### 19. Integrate Kafka modules with Microservice: Use Kafka as event store for service

1. Kafkacat as consumer
   - `docker run --rm --tty --network host confluentinc/cp-kafkacat kafkacat -b localhost:29092 -C -t twitter-topic`
   - `docker container ls -a --filter ancestor=confluentinc/cp-kafkacat` - list useless containers
   - `docker container rm $(docker container ls -a --filter ancestor=confluentinc/cp-kafkacat -q)` - remove all containers by image name

### Section 4: Externalizing configuration with Spring Cloud Config Server

#### 25. Using remote GitHub repository

1. Create private repository `art-gelenler-event-driven-config-repo` 
2. Create github access token
   - Settings -> Developer settings -> Personal Access Tokens -> [Create new](https://github.com/settings/tokens/new)
   - Select scopes: `repo`
   - Generate token
   - Copy token
3. Create environment variable
   - `GITHUB_ACCESS_TOKEN = {Paste token here}`
4. Use `separate-remote-git-repo` spring profile to start application

#### 28. Using JCE to encrypt sensitive data

1. Install [SDKMAN](https://sdkman.io/install) (I used WSL Ubuntu in Windows)
2. Install Spring Boot
   - `sdk install springboot`
3. Check installations
   - `ls ~/.sdkman/candidates/`
   - `cd ~/.sdkman/candidates/springboot`
4. Install Spring Cloud CLI
   - `spring install org.springframework.cloud:spring-cloud-cli:3.1.1`
5. Encrypt password
   - `spring encrypt PLAIN_TEXT --key KEY`
6. Set ENCRYPT_KEY through Environment variable

[springver]: https://img.shields.io/badge/dynamic/xml?label=Spring%20Boot&query=%2F%2A%5Blocal-name%28%29%3D%27project%27%5D%2F%2A%5Blocal-name%28%29%3D%27parent%27%5D%2F%2A%5Blocal-name%28%29%3D%27version%27%5D&url=https%3A%2F%2Fraw.githubusercontent.com%2Fartshishkin%2Fart-gelenler-event-driven%2Fmaster%2Fpom.xml&logo=Spring&labelColor=white&color=grey
[licence]: https://img.shields.io/github/license/artshishkin/art-gelenler-event-driven.svg
[twitter4j]: https://img.shields.io/badge/dynamic/xml?label=Twitter4J&query=%2F%2A%5Blocal-name%28%29%3D%27project%27%5D%2F%2A%5Blocal-name%28%29%3D%27properties%27%5D%2F%2A%5Blocal-name%28%29%3D%27twitter4j.version%27%5D&url=https%3A%2F%2Fraw.githubusercontent.com%2Fartshishkin%2Fart-gelenler-event-driven%2Fmain%2Fpom.xml&labelColor=white&color=grey&logo=twitter
[kafka]: https://img.shields.io/static/v1?label=&message=Kafka&labelColor=grey&color=white&cacheSeconds=60&logo=ApacheKafka
