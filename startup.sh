#!/bin/bash
cd mutual-marker-front
git pull
cd ..
cp .env ./mutual-marker-back/.env
cp init.sql ./mutual-marker-back/init.sql
cd mutual-marker-back
git pull
gradle clean  bootJar
docker-compose build
docker-compose up -d
