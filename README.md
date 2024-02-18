# BookStoreApp-Distributed-Application
<hr>

## About this project
This is an Ecommerce project still `development in progress`, where users can adds books to the cart and buy those books.

Application is being developed using Java, Spring and React.

Using Spring Cloud Microservices and Spring Boot Framework extensively to make this application distributed. 

<hr>

## Frontend Checkout Flow
![CheckOutFlow](https://user-images.githubusercontent.com/14878408/103235826-06d5ca00-4969-11eb-87c8-ce618034b4f3.gif)

## Architecture
All the Microservices are developed using spring boot. 
This spring boot applications will be registered with eureka discovery server.

FrontEnd React App makes request's to NGINX server which acts as a reverse proxy.
NGINX server redirects the requests to Zuul API Gateway. 

Zuul will route the requests to microservice
based on the url route. Zuul also registers with eureka and gets the ip/domain from eureka for microservice while routing the request. 

<hr>

## Run front-end app

Navigate to `bookstore-frontend-react-app` folder
Run below commnads to start Frontend React Application

```bash
yarn install
yarn start
```

## Run this project using Docker Swarm

Follow the [tutorial](https://docs.docker.com/engine/swarm/swarm-tutorial/create-swarm/) for setting up Docker Swarm on your cluster.

Run `docker service create --name registry --publish published=5000,target=5000 registry:2` to create a disposable registry for the docker images.

Create network: `docker network create --driver overlay bookstore-app-network`

Run `mvn clean install` at root of project to build all the microservices jars.

Run `docker compose build` to build all the images.

Run `docker compuse push` to push all the generated images to the local repository.

Run `docker stack deploy --with-registry-auth -c docker-compose.yml <name_of_stack>` to deploy the stack.

**Note:** This setup deploys 3 replicas of each business logic service.

## Clear stack from swarm.

Bring stack down: `docker stack rm <name_of_stack>`

Remove local repository: `docker service rm registry`

### Service Discovery
This project uses Eureka or Consul as Discovery service.

While running services in local, then using eureka as service discovery.

While running using docker, consul is the service discovery. 

Reason to use Consul is it has better features and support compared to Eureka. Running services individually in local uses Eureka as service discovery because dont want to run consul agent and set it up as it becomes extra overhead to manage. Since docker-compose manages all consul stuff hence using Consul while running services in docker.

## Monitoring
There are 2 setups for monitoring

1. Prometheus and Graphana.
2. TICK stack monitoring.

Both the setups are very powerful, where prometheus works on pull model. we have to provide target hosts where the prometheus can pull the metrics from. If we specify target hosts using individual hostname/ip its not feasible at end because it will be like hard coded hostnames/ip. So we use Consul discovery to provide target hosts dynamically. By this way when more instances added for same service no need to worry about adding to prometheus target hosts because consul will dynamically add this target in prometheus.

TICK(Telegraf, InfluxDB, Chronograf, Kapacitor) This setup is getting more attention due to its push and pull model. InfluxDB is a time series database, bookstore services push the metrics to influxDB(push model), In Telegraf we specify the targets to pull metrics(pull model). Chronograf/Graphana can be used to view the graph/charts. Kapacitor is used to configure rules for alarms.

`docker-compose` will take care of bringing all this monitoring containers up.

Dashboards are available at below ports

```
Graphana   : 3030
Zipkin     : 9411
Prometheus : 9090
Telegraf   : 8125
InfluxDb   : 8086
Chronograf : 8888
Kapacitor  : 9092 

```

```
First time login to Graphana use below credentials

Username : admin  
Password : admin

```

> Account Service

To Get `access_token` for the user, you need `clientId` and `clientSecret`

```
clientId : '93ed453e-b7ac-4192-a6d4-c45fae0d99ac'
clientSecret : 'client.devd123'
```

There are 2 users in the system currently. 
ADMIN, NORMAL USER

```
Admin 
userName: 'admin.admin'
password: 'admin.devd123'
```

```
Normal User 
userName: 'devd.cores'
password: 'cores.devd123'
```

*To get the accessToken (Admin User)* 

```curl 93ed453e-b7ac-4192-a6d4-c45fae0d99ac:client.devd123@localhost:4001/oauth/token -d grant_type=password -d username=admin.admin -d password=admin.devd123```
