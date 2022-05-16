#!/bin/bash
git pull
./gradle bootJar
docker-compose build
docker-compose up -d