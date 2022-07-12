#!/bin/bash
# config-server-install-curl.sh

apt-get update -y

yes | apt-get install curl jq

/cnb/process/web