[![CircleCI](https://circleci.com/gh/artshishkin/art-gelenler-event-driven.svg?style=svg)](https://circleci.com/gh/artshishkin/art-gelenler-event-driven)
[![codecov](https://codecov.io/gh/artshishkin/art-gelenler-event-driven/branch/main/graph/badge.svg?token=U5YRYVEM7N)](https://codecov.io/gh/artshishkin/art-gelenler-event-driven)
[![Tested on JVM 11 (Ubuntu)](https://github.com/artshishkin/art-gelenler-event-driven/actions/workflows/ubuntu-java11.yml/badge.svg)](https://github.com/artshishkin/art-gelenler-event-driven/actions/workflows/ubuntu-java11.yml)
[![Tested on JVM 17 (Ubuntu)](https://github.com/artshishkin/art-gelenler-event-driven/actions/workflows/ubuntu-java17.yml/badge.svg)](https://github.com/artshishkin/art-gelenler-event-driven/actions/workflows/ubuntu-java17.yml)
[![Tested on JVM 17 (MacOS)](https://github.com/artshishkin/art-gelenler-event-driven/actions/workflows/mac-java17.yml/badge.svg)](https://github.com/artshishkin/art-gelenler-event-driven/actions/workflows/mac-java17.yml)
[![GitHub issues](https://img.shields.io/github/issues/artshishkin/art-gelenler-event-driven)](https://github.com/artshishkin/art-gelenler-event-driven/issues)
![Spring Boot version][springver]
![Twitter4J][twitter4j]
![Kafka][kafka]
![Elasticsearch][elasticsearch]
![OpenAPI][open-api]
![Docker][docker]
![Testcontainers version][testcontainersver]
![Keycloak Container][keycloak-container-ver]
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

### Section 5: kafka-to-elastic-service:  How to use Kafka consumers and Elastic Index API

#### 36. Running elastic search with docker

1. Start elastic cluster
   - `docker-compose -f common.yml -f elastic_cluster.yml up`
2. In case of error in Windows
   - `max virtual memory areas vm.max_map_count [65530] is too low, increase to at least [262144]`
   - use commands for Windows
   - `wsl -d docker-desktop`
   - `sysctl -w vm.max_map_count=262144`
   - or permanently add `vm.max_map_count = 262144` to __/etc/sysctl.conf__

### Section 9: Securing the services: Spring security OAuth2, OpenID connect, Keycloak and JWT

#### 68. Run and configure Keycloak authorization server with OIDC for authentication

- [Configure Keycloak](/instructions/configure-keycloak.md)


[springver]: https://img.shields.io/badge/dynamic/xml?label=Spring%20Boot&query=%2F%2A%5Blocal-name%28%29%3D%27project%27%5D%2F%2A%5Blocal-name%28%29%3D%27parent%27%5D%2F%2A%5Blocal-name%28%29%3D%27version%27%5D&url=https%3A%2F%2Fraw.githubusercontent.com%2Fartshishkin%2Fart-gelenler-event-driven%2Fmain%2Fpom.xml&logo=Spring&labelColor=white&color=grey
[licence]: https://img.shields.io/github/license/artshishkin/art-gelenler-event-driven.svg
[twitter4j]: https://img.shields.io/badge/dynamic/xml?label=Twitter4J&query=%2F%2A%5Blocal-name%28%29%3D%27project%27%5D%2F%2A%5Blocal-name%28%29%3D%27properties%27%5D%2F%2A%5Blocal-name%28%29%3D%27twitter4j.version%27%5D&url=https%3A%2F%2Fraw.githubusercontent.com%2Fartshishkin%2Fart-gelenler-event-driven%2Fmain%2Fpom.xml&labelColor=white&color=grey&logo=twitter
[kafka]: https://img.shields.io/static/v1?label=&message=Kafka&labelColor=grey&color=white&cacheSeconds=60&logo=ApacheKafka
[elasticsearch]: https://img.shields.io/static/v1?label=&message=Elasticsearch&labelColor=black&color=white&cacheSeconds=60&logo=elasticsearch
[open-api]: https://img.shields.io/static/v1?label=&message=OpenAPI%203.0&labelColor=white&color=white&logo=openapiinitiative
[docker]: https://img.shields.io/static/v1?label=&message=Docker&labelColor=white&color=white&logo=docker
[testcontainersver]: https://img.shields.io/badge/dynamic/xml?label=Testcontainers&query=%2F%2A%5Blocal-name%28%29%3D%27project%27%5D%2F%2A%5Blocal-name%28%29%3D%27properties%27%5D%2F%2A%5Blocal-name%28%29%3D%27testcontainers.version%27%5D&url=https%3A%2F%2Fraw.githubusercontent.com%2Fartshishkin%2Fart-gelenler-event-driven%2Fmain%2Fpom.xml&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAB4AAAAjCAIAAAAMti2GAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad5mH3gAAAPCSURBVEhL7ZZNbBRlGMffj9nZabddtqW1yldDoSUSP1ICaoKlUJtw0BDTNCQcOBDjxSOnxpvGaLhw9KYSLxxAOGDCQa1iNBFogYCBdLds60KXfmw3W9idz/fDZ3bGsl1md6cYb/4Om5ln3uf/Pvt/n3nfwVJKFI6fHmXh952XNnm3DQklnbeso1fGby3n4Pq19o7zB4fao1HvUR0aS5+8fvWr5NQLmhYlBG4tIRZN84O+Xaf3vekNqEU96a9TybHJ682UxhQFY+xHEYKUEmM656f27juxs8+PPkOw9GQud/y3KwXLTKhRUiFaiZCyYFvtUe3bgcE9Gzv8aAXV0kXHOfbrL78vzIMDStmB+rCyP/u7Xjx74GBLJOJHy6yR/vjGxJf37nZomkapHwqHyXnOND96effne/b6oVXpszPpk9f+UAluUSKVtoYHdIrMsYU8/cZbx7b3QATPrKyMjP+YNQ3op1q2hgcWADp1U6z5wtAwzXx49Gbx8RYbI4yh/ucr2QPSCUbxaCSzbKfmS6QV00Jn83Rvm90UiTAJf8wfuG6kQhFz8ExG5PMypkbKPSAkRyi9pSXTHUeEECbWOYGEVsISZ+flbJZzKQmFf4/89gIXFC71KJ3q2bDUFaMCYR5mAgkuKgRDmdMZrpsCCl+19GnnQoBId4J8XE32thUTGly76xI0ARhXdgDrJZz6i+efCGhXAm1QsVTVLwU8oZAl5Fxnc7onwTTFnaBa3a1UMDz7UGRzHNToWlGP4PcNRilC2gTf39Y6tzUOacT3p2wrwguLMj3HGXcLf1bUI1jaA54pTBY1OrUzke+MwWQgVCi4tj4x1tgaSD1pAFJhASiTSwk1tXtjOsVyK4KSalsDaSDtARqUI0GQ4DLQ1kBCSftIt1vDsx7pdfK/dBXQWv8JsD0QXXDEGWwVfuxfA1LCcnTGyfkd/Z9s3mXZpsFZ4E4UHvcMc5he1D870H/uvYGnx+6R6clLy1kSgXMsaAFgj2oiyveLqCn4RLY4d4rG+6/0XDwy6EXWnOizlj6YvJYxS6qiwrbjRz1qS3MhDcPsbt/w8+jQ9kSrH62S9vgu/2g0fQsuNFrx0RQkDbkly4ED8dy7+0f7uv3oPwRIe4w9nDqVTSJF1bC7a1RJQxYslDSssbdf/2Kg30upoqY0AF9Gh6cnxgsLVImqmKxK21zYJWO4d+vlkUN1vrDqSXvc0R8PpyYWbUNt1ZRLSzpyuuKxH0YOvdrZ5o+oBUiH4ZulB+j2ZfTpmTN/3vdDjWhc9XOC0N95QCMLG07m0AAAAABJRU5ErkJggg==&labelColor=white&color=grey
[keycloak-container-ver]: https://img.shields.io/badge/dynamic/yaml?label=Keycloak&query=$.KEYCLOAK_VERSION&url=https%3A%2F%2Fraw.githubusercontent.com%2Fartshishkin%2Fart-gelenler-event-driven%2Fmain%2Fdocker-compose%2Fenv-versions.yaml&logoWidth=40&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAFoAAAAjCAYAAAAUhR0LAAAABGdBTUEAALGPC/xhBQAAAAlwSFlzAAASdAAAEnQB3mYfeAAAA99JREFUaEPtmkGSmzAQRecoPoqPwlF8FC9SOYfXOUMOMfssSP8ufaXVtEAyMMBUftWrGKkR6EWWcXk+xoa8Xq/xdruP9/t6MM7z+UM5Mj9/fY4fz1fBTdgrTaIjYVtwZP6L/qKcSvTj8VAiSVuAsY+Kin6cQfSfz1BOZgjaEoNhrg7gOocE8xOxd/n8IYOwV76VaKxSz1K7Xt+yU6qih2G6ZVCevtXwtpMVAFSoAwNbfD/BdTaJSKptBb3te2QiGo9yYCJEuMmeCvQGjWjcYFln+jAWQI0Q/afwmqsC0TJ+JK63vTWYV2smoou3kSMLNiKB7m2m7mH6AG4IwoGtI6/fIhpIv6c5Mk5NXG97S7BoeF5Lvpdo3JcRkIV2trfEn7uUUrQTYMFN6OC8gEggUb3tz9tHgjVZ8Aw98R94+mH3RvtSUGclg6UnlkK0TszIIk8ZJEsGOBbyzUmNF8Q+1lIypLPPnxMRpfZYxteWnnadS2q3sJ5BmxcN5nJJ0XoPZoLvbhG97QyObT/hvIjNP9FOrkUvbC+ehNmaiaDUjjrWAwoH/pyIKHZyIAta2Y53ylw7Yz8IZxFn+VpWFF8TvqWyaLxOeCFVzDmAqxpg7PAcIv1R/IS+WrQ6cTVz4HpZNLETzYLx7IxiXCAJ8rVz6I0lKBngWJ8ugnMU6Yui9+Qmslm7zKtWn4Ma078EtpFLij46Ks7JrJHfJeEEE1m0FEMM91Y8J6to+TcTnA9QN+CiZixKV9Gos2PwdQLR7Qb3cDBWYAt2yyn2aAsFqSSZKKBoSI+kKG4ctOV3BcBxAiu9Ok4CeWeSh5AWEhxNnzouIHoyoROylPx4NxEk8D+BYri3Uniu9a8FnqurkVsGkD5i62sgZ1/R/qkkyuQLS4Ru/jJpwlWNdhCdgy85xRcdvE5gjweRWA+CiUQTPAUyt5YUorECI2m6MmXSFMVVzRWudfiXyHEWDHBD0s5zi9oFEH1q8RM8CfDQkkuI7nmc+mpaU4qWWLkWlScTt8KIfsKaWl2BVjSOExy/AO0VmGiSR6PzbsxUdJqg/xVEfzmRwfWDSfoBVrWubGlDP6n9CsM6P7aXGiWa6JG0bhnMZUSfafvAPHozEc1EP84SnGTx8nCcb8zURJJ7fpy1kz2KdyQjcBBH9lovpWDhzwi0n0T9Cd3XG3P0qn5XMlIXLTnjXyphq4ok7IpsgfiAX5PLiUawskIhO9HzdFHLrGgmkrQFa7L7t8UNVrFNk2g8EZzx76P32rO3FMxcWjSzhXDs/VtsEbU0idaYb36r2SsyNmRhRWJr8Xs5jtGOftTtKbbMOP4F/8/qedWTQ2AAAAAASUVORK5CYII=&labelColor=white&color=grey 
