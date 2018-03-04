FROM mongo:3.6

MAINTAINER armando.ramirez.vila@gmail.com

COPY src/main/docker/tasks-api-data/tasks.json /tasks.json

CMD mongoimport --host tasks-api-mongo --db tasks --collection tasks --type json --file tasks.json --jsonArray --verbose