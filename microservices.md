# Overall Microservice Architecture

We will package the following services in **containers** and orchestrate them via **docker-compose**. </br>
**HINT**: You do not have to be a docker pro to work with this tutorial :) The only thing you have to do is installing docker for your machine and running the command **docker-compose up** within your **microservices directory**. When you want to clean your docker system again, execute **docker-compose down**. </br>

Dockerizing the services will introduce minor "mistakes" where links in the provided web interfaces are incorrect from an host perspective but valid when being in the correct network (the docker's docker-compose network).
But this does not hinder us to see the benefits such an approach will have.
There are also some links included in the presentation you saw, where you can configure the web interfaces.
 - [Config Server](microservices.md#config-server) exposes a service, where other services can get their configuration by start-up from git repositories. This enables a developer to concentrate all configuration files version controlled in a single place.
 - [Eureka Server](microservices.md#eureka-server) is a service registry. All services which include an eureka client dependency automatically connect to a eureka server. Easier resolution of services (also some kind of DNS resolution works then). (Implemented by Netflix)
 - [Admin Server](microservices.md#admin-server) delivers a dashboard for all running services with management and monitoring information. (Implemented by Codecentric)
 - [DVD Service](microservices.md#dvd-service) is our first, not feature complete service, but shows all the aspects we need to implement a full stack app with spring boot.


 **HINT**: The properties shown in the following paragraphs are for a local deployment on localhost. So the idea is to run every single server via **./gradlew bootRun** or from within the IDE of choice. In the configured docker case, the settings may be different to some extent.

<a id="config-server"></a>
## Spring Boot Config Server

Go to [Spring Initializr](https://start.spring.io/) and select the corresponding build tool (in our case Gradle), the Java language and input project metadata. Use Jar as packaging option and Java 11 since it is the most up to date LTS version of Java.

In the Dependency section choose **Config Server** and generate the source code and configuration.

Add **@EnableConfigServer** to your **ConfigServerApplication.java**.
```Java
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigServerApplication.class, args);
	}
}
```

Add an **application.properties** file under **src/main/resources**.

In the **application.properties**, you have to specify the current repository. Also the port is predefined here. We will use the *localhost:8888* config information within the other services' configuration.
We also specify a subpath, where the config server searches for configuration files.
```bash
spring.application.name=configserver
server.port=8888
spring.cloud.config.server.git.uri=https://github.com/lion5/workshop-spring-boot
spring.cloud.config.server.git.search-paths=config-files
```

<a id="eureka-server"></a>
## Spring Cloud Netflix Eureka server

Go to [Spring Initializr](https://start.spring.io/) and select the corresponding build tool (in our case Gradle), the Java language and input project metadata. Use Jar as packaging option and Java 11 since it is the most up to date LTS version of Java.

In the Dependency section choose **Eureka Server**, **Config Client** and generate the source code and configuration.

Our **Eureka Server** will get it's configuration from the **ConfigServer**, therefore, we add the Config Client dependency.

To start this stand alone spring application, you have to enable the eureka server. Therefore add the **@EnableEurekaServer** annotation to your EurekaServerApplication.java file.

```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaServerApplication.class, args);
	}

}
```

In our **bootstrap.properties**, we include the following lines:
```bash
spring.application.name=service-registry
spring.cloud.config.uri=http://localhost:8888
```

Since we have no configuration files hosted by our config server, we will add them before starting the eureka server.
The configuration files following a naming schema.
*SERVICE-NAME*-*PROFILE*.properties.
By default the file **application.properties** is used and then the custom **SERVICE.properties** can override these defaults. Maybe an example makes it clearer.
In our case we specify properties in the *config-files* folder of our *config server*. One property in  **application.properties** is:
```bash
# since we have a config server, there is no need for fixed server ports any more
# this also avoids port conflicts especially when testing services
server.port=0
```

This setting specifies, that every service getting its config from the config server will choose a random port (when the default profile is set). This is no problem since we will configure every service as an eureka client, but our eureka server should have a stable port mapping, otherwise the service registry and the service naming resolution will not work. Therefore, we will override the defaults for our service registry and create a property file named **service-registry.properties**. Remember: the name of the file is equivalent with the **spring.application.name** of our eureka server.
```bash
# default port for eureka server
# default port for eureka server
server.port=8761
eureka.instance.hostname=localhost
eureka.client.fetch-registry=false
eureka.client.register-with-eureka=false
eureka.client.service-url.defaultZone=http://${eureka.instance.hostname}:${server.port}/eureka
# Good for production cases - preventing deregistration of active services
eureka.server.enable-self-preservation=false
```
The self-preservation property is only set to false in development environments. A red warning message is displayed, for further infos check the docs of eureka server.
The *server.port* setting here will override the default specified in *application.properties* and the service registry will have a stable port.

Now it makes sense to add another property in the *application.properties* for registering the eureka server's uri to every eureka client (microservice).
```bash
# service registry url
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
```

<a id="actuator"></a>
## Monitoring and Administration

Spring Boot Actuator is another starter, which includes management information out of the box. Via different properties, you can include, exclude endpoints and specify the base path for management information. (Your eureka and config server already transitively include the necessary build dependencies).

Add the following line to your project's (dvd store) *build.gradle* within the depenency block:
```bash
implementation 'org.springframework.boot:spring-boot-starter-actuator'
```

Then configure the actuator endpoints at *management* by including all endpoints except for *heapdump*. Also show a detailed view of the health endpoint with all corresponding subsystems and external dependencies. Therefore alter the **application.properties**.
```bash
management.endpoints.web.base-path=/management
management.endpoints.web.exposure.include=*
management.endpoints.web.exposure.exclude=heapdump
management.endpoint.health.show-details=always
```

If you want to specify these settings for all your microservices, which are participating in your overall system architecture, you can also change the **application.properties** version controlled and provided by the configuration server.

Try to curl your service at **localhost:RANDOMPORT/management**.

The basic accessed endpoint is **/info**.
So how to customize the **/info** endpoint.

As you might expect, you can do this via configuration properties.
Prefix the stuff you want to show with **info** in your **application.properties** file. An example might be:
```bash
info.contact.name=Johannes Manner
info.contact.email=johannes.manner(at)uni-bamberg.de
info.company.name=Lion[5]
info.company.website=https://lion5.io/
```
Another source of information which might be quite helpful for consumers of your service is the build version and the artifact name etc.
You can easily include these metadata by overriding the **springBoot()** task in your **build.gradle** like this:
```bash
springBoot {
  buildInfo()
}
```

So when looking at our eureka dashboard and clicking on the link in the dashboard, per default the **/actuator/info** endpoint is called.
Since we changed our base path to **/management**, this will not work out of the box.
So you have to adjust the eureka's info and health endpoints. **!! "These links show up in the metadata that is consumed by clients and are used in some scenarios to decide whether to send requests to your application, so it is helpful if they are accurate." [Eureka Client Docu](https://cloud.spring.io/spring-cloud-netflix/multi/multi__service_discovery_eureka_clients.html) !!**

So alter your microservices application.properties accordingly:
```bash
eureka.instance.statusPageUrlPath=${management.endpoints.web.base-path}/info
eureka.instance.healthCheckUrlPath=${management.endpoints.web.base-path}/health
```
We are using the bash-path property defined earlier to be as generic as possible.
By now, we have microservices exposing some management information about their properties, beans, health etc., but we only can use them via curling the endpoint.
Thanks to codecentric AG (https://www.codecentric.de/) for their implementation of a user friendly web frontend, where you can investigate your actuator endpoints exposed by your microservices.

<a id="admin-server"></a>
## Monitoring - leveled up - Spring Boot Admin Server

Go to [Spring Initializr](https://start.spring.io/) and select the corresponding build tool (in our case Gradle), the Java language and input project metadata. Use Jar as packaging option and Java 11 since it is the most up to date LTS version of Java.

We also want to use our service registry to expose all listed services there to our admin server. So choose **Codecentric's Spring Boot Admin (Server)**, **Config Client**, **Eureka Discovery Client** as dependencies.

Change your **AdminServerApplication.java** and add the annotation **@EnableAdminServer** to your class.
```Java
@SpringBootApplication
@EnableAdminServer
public class AdminServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdminServerApplication.class, args);
	}

}
```

Also edit your **bootstrap.properties** to configure the application name and the location of your configuration server.
```bash
spring.application.name=admin-server
# spring config server url - necessary for all applications
# getting their configs from standalone server
spring.cloud.config.uri=http://localhost:8888
```

In our GitHub respository, we specified 8000 as the admin port, so access your admin server at http://localhost:8000.

<a id="dvd-service"></a>
## First microservice - dvd store

As already described in the [implementation guide](implementation.md) we deploy a custom dvd store, where a user management is existent and also some kind of REST API.
The service is by far not feature complete but gives a first overview of all concepts which are needed to build a full stack app with spring boot and how to integrate it in an architecture with management services.

To use our implementation solution of *solutions/taks5* you have to adapt the build.gradle: add *application* plugin, add *eureka client*, *actuator*, *config client* dependencies to your build. **HINT:** It is easier to create a suitable *build.gradle* through the Initializr.

Back to the [main page](README.md).
