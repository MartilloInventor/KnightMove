#!/usr/bin/env bash
trap "exit" INT # so that shell script can be exited via Control-C
while [ true ]; do java -jar ./target/KnightMove-1.0-SNAPSHOT.jar ; done
