FROM mongo:3.6

MAINTAINER armando.ramirez.vila@gmail.com

COPY src/main/docker/tasks-api-data/tasks.json /tasks.json
COPY src/main/docker/tasks-api-data/users.json /users.json

CMD mongoimport --host tasks-api-mongo --db tasks --collection users --type json --file users.json --jsonArray && \
	mongoimport --host tasks-api-mongo --db tasks --collection tasks --type json --file tasks.json --jsonArray