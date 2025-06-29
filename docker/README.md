### REQUIREMENT BEFORE RUNNING DOCKER COMPOSE FILES
- Must have docker installed
- Make two environment files: .env.docker and .env.mysql. And put this in this folder

### RUN DOCKER-COMPOSE.BASE.YML
docker compose -f docker/docker-compose.base.yml --env-file docker/.env.mysql up --build

### RUN DOCKER-COMPOSE.DEV.YML
docker compose -f docker/docker-compose.dev.yml --env-file docker/.env.mysql -p smoking_support up -d --build

### RUN DOCKER-COMPOSE.NGINX.YML
docker compose -f docker/docker-compose.nginx.yml --env-file docker/.env.mysql up --build