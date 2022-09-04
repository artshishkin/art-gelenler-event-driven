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
![PostgreSQL Container][postgres-container-ver]
![Kafka Container][kafka-container-ver]
![Elasticsearch Container][elasticsearch-container-ver]
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
[postgres-container-ver]: https://img.shields.io/badge/dynamic/yaml?label=PosgtreSQL&query=$.POSTGRES_VERSION&url=https%3A%2F%2Fraw.githubusercontent.com%2Fartshishkin%2Fart-gelenler-event-driven%2Fmain%2Fdocker-compose%2Fenv-versions.yaml&logoWidth=40&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAFQAAAAjCAYAAAAKTC24AAAABGdBTUEAALGPC/xhBQAAAAlwSFlzAAASdAAAEnQB3mYfeAAABA5JREFUaEPVmU123CAQhOcoPkqO4qP4KFnlHF7nNNlnMelqU7xSq0Ew1l/qvYpGCBD90SDJeTyDfv3+83z/+fn8Ycbx4/PTy2aFNuhD+3n+ne/nu8I4HnZ/9Zv5KFWgfuN3uyH8IWaZWcES2FuphyPAxTra3m31tM7R8jEUkKcBrYEXOJl/mCmH2KqfQMvqaH9H6hKgWzBxnUqvZ7Y2HzLw1T3sHBNztBwo7ncW0M3stGvMNizpbt1oq4s2UHofO/e99UjZvu17ud2Hfj/wng+HpEFGW9DUFEza2nBCEFi8jrJZob/orXJ/IKoP0iMLUs0Mm85OdWdSppe9wfC2GE8xl/Bs+RF68CmdWrLLQaiz+i1bfS7tOIHTGQqgBUwENFs+KmwTo9oEShGsy4JyMFtg7bpnuCwx9KPtuAKGdTJQj7N4RJtLfiWB091/DVqcBGgB1I7TDyUARVsJtIKbLB9RbLulzYcSxXdPHDWrNNvUujeiPs8BUIEuoA8KbaJfKd8S6ilMeOsNweL6CkxhqCHvGHUSAK0tIy5ltltMoPXXE1ZP9rrD3+qZco6F/dKsT6EsAoV76r+HloAXdeyIQAmstWXwOiZMs1rr43dPXg/1i19d2rPlFM71Og0ealX/S6kA9X1L6xSoUCvDucQ5GdwrK1Ari4OJioFUEN8s59hb5ZSPdcSI147QF9ACYWULGiIUvcZv8eaWUdpC+M0M9SWP+nK9pTjws4FiC4h1esb9HKjPRARSDKVArQ3UA8qsBMT4UCLgnrwfjK24AtqjvPO2UIU6cn3LzgntMmBwXLZ6bZFxUr6wtaH4m339D/KxCrCea9b7v6YMKAytgNpvlEGtp7zb6jFLWR8T0PrTHZ6qyJKrnQHrWbeKCjQFg0wqL+QRKLUozyx1oXiueiWYS4y47IgEYKJQFWhrL6z0+aQ3c7k3989gnUH9HbUa+A29pQoUSuFYWZ0Fg+r7apa1PWsfHd09Q3vJQC2ANvfDBhCUdffQYp+ElsrkQBhwFsgt3ItBtAAKQKuss/M4Mw5XQCzqR8tkODA7r5OAvu1Izb73nWnslyNaAIUQYIVagocABf9H5DBw3Tz86qRbBG3XCJryCS0B3M2jWgGFCAJHyANVGMUEMvouivreRrI7KgvmajPOEaVAobQTA1GhiEaBjigL6EqPLnWqCXRGewLFZGWBXWG8dczqdkChLLiz/QpM6JZAr87SV2FCtwQKtf5afqhtvPGv9rPaBWj3E/RFoNDZX07pg3hSuwD1JdoAOvK51tPhX082xu9mpWoXoFAE6UZ2dt45R3XUnronSGo3oKsstd97LCHVHmCzP7ntqd2AQhgoligeUntkZlPlAwMZhvvFvRbnPo4C70iASz2f/wC11/BeQd5XfwAAAABJRU5ErkJggg==&labelColor=white&color=grey
[kafka-container-ver]: https://img.shields.io/badge/dynamic/yaml?label=Kafka&query=$.KAFKA_VERSION&url=https%3A%2F%2Fraw.githubusercontent.com%2Fartshishkin%2Fart-gelenler-event-driven%2Fmain%2Fdocker-compose%2Fenv-versions.yaml&logoWidth=40&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAE8AAAAjCAYAAADVAlenAAAABGdBTUEAALGPC/xhBQAAAAlwSFlzAAASdAAAEnQB3mYfeAAABA9JREFUaEPtmTFPGzEUx31VhEQXxMCIRAcYCgMdwlKhJAMR6sAMHWgH6MdA6tB+CUCoQqJiZKRLwggZykIHGIoQIwtLxBCJ+u/Y6bPvfGdf7pKL2p9k/O7ZQcn/nv3e+YJnDrMQBIHoY6YUhu+tR/b+16W86jLD2+8Ple5FDryQvQZEU8IB2Jubm/LqP4qQeFQ0yuHhoXXsXyUy8sDa2ppYrqOwZHvcyn5AaOJNTk6KvlKpsJOTE2EDKuDV1ZW0isXGG8aqfJMzW55oCWNqaoo9PDwI8ZrNpvR2UUv2+vqazc7OCjtvkARMNsoTsX6IqFGakEb2aOIhqhYWFoRNow0o8Ux/bnQeWfCFZ08SPTCRPYPPZ17+vNCW7fz8vLS6YtFmw5yHdnx8LEf7hAih4et3pHbGxfcglDDiIssWjSbr6+vSGh1q385YkyecgPeuRGZbiESFMq8BrfvUOJ1jE9YLW/b09TsA4RSuAqZ+wqDimONxn/MhTcIwgT8JfM58OvnIt4ADnjjj6Fu8RqPBqtWqsBW+4mHJmBs9vjj2IHMb8/HXXy4K8cy9TP1/BcZp5CmeE5JNpHg0qih0Kp1D/dvb22xvb0/YruL5Zs+s/IpX/OZFrfij14vS6mJGsbbnobazCQcwphqF+pVw7XZb9E6QH6bRp793afNLbP8OS5k23AQIrdDEq9Vq0uqCyHGNHpPx8XFpFZ8Zm3omfB4iVAnYE29/f19aYdGiRFQ+tLu7O+FbWVmJnJuILUv26ReXvNiO9BMO3urLM4mvcjn39jy6FG0/3mXOKBKVbW0gNTbkfjkU8ZDdbm3RM0B8vwIVDgxFPFt2Kxz4knyfw+nMJ1n2UHritVottrS0JJxRwtzc3LC5uTl5pc85PT1lq6urwnYR1ecRaFgk1XhAq/NoZAE1ZPpdiBOx6JFnLk8bWqli/mCIlkY4cH9/L60wrpXBUOB31UU4EDoYsEXM8vKyGFONQv07OzvCNz09LfoonOuqIWA8acaiLVsTFXVRU9TYxcUFK5fLwlbEfQ74lAaDxmWvU4QizxeVZHxwOekYBuazbBKpxdva2pIWL+I7HWn9jbpRA+WI702NXLY2AcypcULZlqyiSEsXWzA9ZXElFHlxgphjNoGwDyZRlKWbVjigiYdCWQFhaLNhzkMzE4gN3z0ma/oRDmji1et10eN0xEaWL70RfXm/mI6E13I4Zu9HOKCJVyqVRP/09CT6KMbGxqSVDY1KZeBF89G7xcT3Ey6EEoba11CCnJ+fC5vudcb0zBCv/qSdC4g2XgBnIZrCKp6NvMQDeWVglzdhaXAuVVDX7e7uyqt8yUJE2zFSlqR+PBsInUf2/SdjP9qX4vAUJzFoCuyVovE/eM0IBlcCMfYH+Q81yBoPAq4AAAAASUVORK5CYII=&labelColor=white&color=grey
[elasticsearch-container-ver]: https://img.shields.io/badge/dynamic/yaml?label=Elasticsearch&query=$.ELASTIC_VERSION&url=https%3A%2F%2Fraw.githubusercontent.com%2Fartshishkin%2Fart-gelenler-event-driven%2Fmain%2Fdocker-compose%2Fenv-versions.yaml&logoWidth=40&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAFYAAAAkCAYAAAATvM09AAAABGdBTUEAALGPC/xhBQAAAAlwSFlzAAASdAAAEnQB3mYfeAAABBNJREFUaEPlmU1IFGEYx9/xc2sLtzzkFuZXRHgIO9TBCN0gr56CFKK8dOoWRR07ZFEkEV2CyMJS+oAw6GKQ7tlDEiRSmCtWElG5kOWmNr3Pu8/I7HzsvM/MzuyEPxjnfR6dZf2/z/t/P0ZROcwDi2/OsOUvfRhl2dyaYqXROozWJ2RhVxdn2c/xVoycUZc/s9gRT33nmaHxNOuenMAoSz2/Zk60ZQMfKMG7FOmxHSRRAaV8u3hu4aWCmfWBlLCTd2NsaqAZI3doAq8XHIUFQUu4KMD8XJW4ewHELYrAKbwHRF6PtavSeG0aW+4J1HtX0izxkHssGCsCzf42/zzWVlinoV9scWFCMtK1vypvvmsfJjTKvI9AOyyFlfHTQggLlG09xqJ7r2MkCa9A5ZK5AmGWVy4mSXm/MHms7CRVCL+tav9EF1VDJ1IO1LwkiSTvGAKk5ZaR71+j2KIDov4vJO4n2Rif/BR+lyXHClYzC9jyh9LKGLY8ErAVGJ9VJZ4j77zCgpvJywjknYDnjLu2k1xkpxVF6IWFYWisNPinwPN0aQEl37GxRQhr9E7t8zXg92ADRpyqdk3YnTVxkaBSyYfl3M23GJnJrGb7TT3eKO5UqEO7UHmNBt6xVnuLweYWbGUxVv/a5FURibi61E3b8BOsqSxVxBUZnMEMEd0/nYPH/Fpol0fsPg7sQX9BB0EnaAhhp2eC2e+5FreI1Nspa4T/HaioiSuE3dXYIIIgIItr1+ce8yLkNmaZ19F/MHfIO9GLFiE8NqooLF4n2zVmPva+wpYcS93BdaRXrFYFdrTzaxT9OXTCwiycsqu6AKF+Bb2ogBBW4cI2hURYu1k4dMCX5JK18+sULt30CGHPnjvPnj1+hCk6FGFTnbWsJlqGkRnKtrFYyOy8xOR17eoVEfhNJvM3r6iA+3ETDDDkZfB0CENF7WnClj2hFpYPf72P5kNYAbZDQQ+fvO6F1GTBT0cdzgg0Aq1YGWAPH1ZkRQVCJ6zMiVMxMJ4NOJFjBT+WfrPqB7cxkuNQE22xP7/8h73rOIqRNWFbGVAsQCOnYrdENmDLP+LlFaxtdJi9mJvGjBlqdfgJTKZUUQHT5EWpWmq1WpFMdGIrlzBULYiqP0KkYPJYqFqVi+tEIUTNR7Gr1ouogOXkpZ6+ICWuV+yqFYBJDLwtcPhSD169eBEVyLuOVW5dZoqF7/ppAUaCPjuAkVKIlYnjBmFq4RtrfjqAkXdRZVYFRsTrZ2z7AlQp36s6vSCkIL3zKrlzw7OoslVqBeVclILMG1c3SAsLDM++Z30fJjGSx02V2lEIge2O+goJSVg9u0eeiDWpHSDm8wOH2Z5YNWYKzEqaDb1mbOTXhDgYBx/WezHMe+LiP7RtcnC7Osb+AQgSzKe9xJg9AAAAAElFTkSuQmCC&labelColor=white&color=grey


