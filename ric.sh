#!/bin/sh

printf "Run In Container (RIC)\n"

docker exec -it stringsdb-app "$@"
