git pull
chmod +x ./gradle
./gradle bootJar
docker-compose build
docker-compose up -d