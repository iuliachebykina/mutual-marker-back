#!/bin/bash
git pull
gradle wrapper
gradle bootJar
docker-compose build
docker-compose up -d