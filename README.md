# Spring Boot in the Cloud

The following is an overview of the repository:
- **samples**: In this folder, we included small samples for important spring concepts.
  - **dependency-injection**: a sample about how **@Configuration**, **@Bean**, **@Profile**, **@Value** is used and how dependency injection (DI) feels. Have a look and play around if needed. </br>
  HINT: This is also the source code to the first slides.
- **tasks**: Tasks to get hands dirty with spring boot and develop a full stack application with a web frontend, a REST interface and an integrated database.
  - **task0**: Building a simple web app with a thymeleaf frontend and some simple validation rules. Start from scratch with spring Initializr or see task1.
  - **task1**: Building a simple web app with a thymeleaf frontend and some simple validation rules. The first controller classes and templates for thymeleaf are already included under *tasks/task1/dvd*.
  - **taks2**: Spring JPA: Configure the repositories and solve common initialization exceptions. Use *solutions/task1* as a template if you not finished *task1*.
  - **task3**: Properties, Profiles and Spring Security.
  - **task4**: Spring HATEOAS, Building a REST API
  - **task5**: Testing
- **solutions**: Possible solution implementations for the tasks.
  - **task1**: A simple solution for the first website *movies*. An in memory list of two movies is presented in the browser under http://localhost:8080/movies. You can also POST a movie to the backend, which is then validated via bean validation. In case of validation errors, some error messages are displayed in the  browser.
  - **task2**: Repositories are added for *Movie*, *Actor* and *FilmStudio*. Also the entity graphs solution is used for resolving lazy associations. The web frontend is not altered, but the POSTed movies are now stored in an h2 database. See the instructions in the implementation guide. You also see the SQL statements in the console, you can alter this behavior by changing the logging levels in the *application.properties* file.
  Especially the *DemoData.java* is interesting in the persistence context here, since we store some sample data and work with associations. Also change the behavior of the *FetchType* or the entity graphs to see the problem of **LazyInitializationExceptions**.
  - **task3**: The solution for this task is implemented in a way that the first user which registers is the admin. All other users have the role USER, but the ADMIN can change user rights in a separate view. Also a form of pagination is implemented for the movies. To change the number of movies, you have to alter the *movies.pageSize* property in the *application.properties* file. To enable a page size property, the MovieRepository was changed (look in the classes and enjoy). The UI was basically enriched to deal with the user management.
  - **task4**: Two different approaches were implemented for structuring the application and building the REST API. These are also discussed on the slides. </br>
   - *FilmStudioAssembler* uses the domain class *FilmStudio* to generate a REST style representation of the data with hyperlinks. You can access this by hitting http://localhost:8080/v1/studios endpoint of *FilmStudioRestController*.
   - All other endpoints have separate representational model classes and assemblers which enables - as introduced in the theoretical part - an easy and controllable way of full and short representations of our model classes. A good example is the movies endpoint where the short representations of actors and film studios are included: http://localhost:8080/v1/movies </br>
  The endpoints are not secured in this example solution by an authorization token or some similar approaches.
  - **task5**: Implementing some @SpringBootTests. All tests are a starting point for further ones, but show our notion of unit and integration testing. A good example for spies is included in *RegistrationControllerIntegrationTest* to mock the number of users in the database, but execute the implemented service handling. We tried to reach a high line coverage (in our case 93%).
- **microservices**: Outlook, what might be useful to build a microservice architecture with spring boot and their corresponding services and servers. You can start each service, when entering the directory and execute *./gradlew bootRun*. Start *config* server first and then start *eureka*, *admin* and lastly *dvd*. A description and configuration of the services can be found under [microservices](microservices.md).
  - **config**: This directory includes the config server. In *src/main/resources/bootstrap.properties* you have to specify your GitHub username and password, otherwise you are not able to use the config server. Then exclude the file from being managed by Git via **git update-index --skip-worktree path/to/file**.
  - **config-files**: The directory with the config files, used by your *config* server. Everything is configured to work with the following microservices.
  - **eureka**: Netflix Eureka Server as a service registry. You do not have to configure anything here. Start and enjoy at http://localhost:8761.
  - **admin**: Codecentric's admin server. All microservices are "loaded" from the service registry **eureka**. You do not have to configure anything here. Start and enjoy at http://localhost:8000.
  - **dvd**: The first microservice in the overall architecture. Getting the config from the *config* server and registering with *eureka*.

But before looking at a possible microservice architecture, let's understand the basic concepts of spring and spring boot and implement your first spring boot applications in this [guide](implementation.md).
Along the way, we will reference some helpful **samples** and **tasks**.
