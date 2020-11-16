# Building your first microservice with SpringBoot - a simple DVD Store

## Task 1 - Implementing the frontend and controller of the backend

 Go to [Spring Initializr](https://start.spring.io/) and select the corresponding build tool (in our case Gradle), the Java language and input project metadata. Use Jar as packaging option and Java 11 since it is the most up to date LTS version of Java.

 We build our first full stack microservice with a web frontend, a service layer, a database and a simple REST interface.
 In the Dependency section choose **Spring Boot DevTools** (enables auto-reload), **Lombok**, **Spring Web** (this includes the embedded tomcat), **Thymeleaf**, **Spring Data JPA**, **H2 Database** and generate the source code and configuration.

 For the first task - implement a web frontend with Thymeleaf - we will implement a single class for every part in the MVC paradigm.
 When looking at the generated structure of our project, we will see, that the **spring web** starter generates two folders under *src/main/resources* for html templates and some static content for the sites like images etc.
 The *application.properties* is part of every spring boot application to configure the properties.
 For the first task, the defaults of this properties are sufficient, we come back to these properties in the next task when working with databases.
 Under *src/main/java* in your configured package, an entry point to start the application is generated in form of a **XYApplication** java class.

 Before starting the interesting part of controllers and views, we implement our model classes and use *Lombok* to generate some boilerplate code at compile time.
 In most IDEs Lombok needs to be installed, for IntelliJ this [link](https://projectlombok.org/setup/intellij) is helpful.
 Then go to settings (Ctrl+Alt+S) under *Build, Execution, Deployment* -> *Compiler* -> *Annotation Processors* and enable annotation processing.
 If you forget to enable this setting, you will get a lot of errors when compiling your solution first - keep this in mind.
 Now create a package *model* and implement the three domain model classes. Do not add the relationships in this task.

 Add another package *controller*, where we implement our first *MovieController* class.
 For the first part of the task, we only want to *GET* movies and present them in our browsers. Therefore implement a *GetMapping* and use the springframework's UI class *Model* to add a list of movies to the template rendering engine.
 Expose this endpoint at *movies*, therefore annotate the class with a suitable *RequestMapping*.
 Check out the presentation for further details, where to add the annotations :) Remember the return value of your *GetRequest* method since this return value is the name of your view.</br>
 A possible constructor, which instantiates two movies, can be look like the following:
 ```Java
 public MovieController() {
     this.movies = new ArrayList<>();
     // init movie list
     this.movies.add(new Movie("1", "Inception", false, 2010, "https://cdn.pixabay.com/photo/2017/05/15/17/43/calm-2315559_960_720.jpg"));
     this.movies.add(new Movie("2", "Cloud Atlas", false, 2012, "https://cdn.pixabay.com/photo/2020/03/02/16/19/vintage-4896141_960_720.jpg"));
 }
 ```

Now, you should have a *Controller* and *RequestMapping* annotated class with a *GetMapping* method using the *Model* to add attributes to the view. Ah, right - now implementing the view.
To add a view, we have to change the folder to *src/main/resources/templates*.
By default, thymeleaf will use this folder to look at views. Create a *movies.html* or name it anyhow, but check your return value in the movie controller you implemented. The name of the view and this return value MUST be the same.
Add thymeleaf as another namespace in your html, *xmlns:th="http://www.thymeleaf.org"*.
We use bootstrap for the layout, but you can also define your own styling as you like.
Then implement a table to show the information for movies. Use thymeleaf's loop implementation therefore. Assumed your attribute is named *movies*, the relevant part of your HTML looks like the following:
```HTML
<tr th:each="movie : ${movies}">
    <td><img width="120" th:src="${movie.coverImage}"/></td>
    <td th:text="${movie.title}"></td>
    <td th:text="${movie.wonOscar}"></td>
    <td th:text="${movie.year}"></td>
</tr>
```
If the attributes of your movie model class are different, adjust the names in curly brackets.

Now, go to your main class and run your first spring boot application.
Go to your browser and look at the movie details under http://localhost:8080/movies.

You will now see a table with two rows with *Inception* and *Cloud Atlas*, two of my favorite movies.
Ok, we can view data in the browser, but how can we send data to the backend filled in a form?

The following paragraphs will explain this.
First we need to adjust the *GetMapping* (yes, there is no mistake).
Go to the corresponding method and add a "placeholder" object movie as an additional attribute to the model object.
We will use this object to "store" the user input from the HTML form within the web component of your application.
Next step is to adjust the HTML and add a form element with input fields for the attributes.
We also need an endpoint, which is called when submitting the form: *th:action="@{/movies}"*. We will look at this action in a second, but before let's look at the thymeleaf syntax to set the attribute values.
```html
<form method="POST" th:action="@{/movies}" th:object="${movie}">
    <tr>
        <td>URL: <input name="coverImage" th:field="*{coverImage}" type="text"/></td>
        <td><input name="title" th:field="*{title}" type="text"></td>
        <td><input name="wonOscar" th:field="*{wonOscar}" type="checkbox"></td>
        <td><input name="year" th:field="*{year}" type="text"></td>
        <td><button>Create</button></td>
    </tr>
</form>
```
*movie* is the placeholder, added in the *GetMapping* method to the springframework UI's *model*.
Via *th:field* it is easy to set the attributes.
For a full documentation of thymeleaf and all the possibilities, look at the following [page](https://www.thymeleaf.org/documentation.html).

Good, we can now add additional movies, but where is the endpoint, which does the backend processing?
The thymeleaf action specified the POST endpoint, where the data is sent to.
So another time, we have to go to our controller and add a *PostMapping*, where one parameter in the method signature is the movie placeholder object.
This placeholder is injected from springframework's UI handling.
The idea is to add this movie to the *movies* list and redirect to */movies* to see the movies again in the browser with the newly created one.

Finished?

We missed one important aspect - *Validation*. </br>
We use the Java Bean Validation API by adding the following starter to the *build.gradle* in the dependency section:
```
implementation 'org.springframework.boot:spring-boot-starter-validation'
```
We can add this validation rules to our model classes and use an integrated mechanism to identify errors when posting new data to the backend, but the first step is to add rules :)
```java
@NotNull(message = "Title must be set")
@NotEmpty(message = "Title not there")
private String title;

@NotNull
@Pattern(regexp = "(https:\\/\\/).*\\.(?:jpg|gif|png)", message = "Must be a valid URL to a picture.")
private String coverImage;
```
Two examples for bean validation rules added to the *title* and *coverImage* attribute of our *Movie* class.
To enable the validation for the POST endpoint, we have to adjust the method signature of the method:
```java
public String processMovie(@Valid Movie movie, Errors errors, Model model) {...}
```
*@Valid* enables the validation and tests the rules. If errors occur, they are wrapped in the springframework validation bean *Errors*, which can be used via method injection (DI).
The springframework's ui *Model* class is already known. </br>
The *hasErrors()* called on the *errors* object enables a user to check if some validation errors are present and return to the */movies* page.
The errors can be also rendered for the user via some thymeleaf commands. An example for the coverImage is shown in the following:
```html
<td>
     URL: <input name="coverImage" th:field="*{coverImage}" type="text"/>
     <span class="text-danger" th:if="${#fields.hasErrors('coverImage')}" th:errors="*{coverImage}"></span>
</td>
```
Now we are finished with the first task. A possible solution is included in *solutions/task1*.


## Task 2 - Full stack application - adding a database to the current project

Since we already included *data jpa* and *h2* starter, we do not need to add another starter now. H2 is a development database, which allows you quite easily to access the tables when the web server (tomcat) is running. To work with databases, we have to update our **application.properties** file and configure our development database.

 ```bash
 # persisting the data - no need to enter the test data each time
spring.datasource.url=jdbc:h2:file:./data/sample
spring.datasource.driverClassName=org.h2.Driver
# user credentials for login in the web ui
spring.datasource.username=sa
spring.datasource.password=p
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
# update domain data, when model classes change
spring.jpa.hibernate.ddl-auto=update
```

Furthermore, we have to add JPA annotations to our model classes.
As introduced in the slides, *@Entity* and *@Id* are the only necessary annotations for model classes to "mark" them as database entities.

The next step to persist data is to create repository classes.
Spring Boot eases our lifes a lot since we only have to implement interfaces extending **JpaRepository<MODEL, ID>**. *MODEL* means the class, e.g. *Actor* or *Movie*, and *ID* the datatype of the id attribute. In the case of *Movie*, this results in the following signature:
```Java
public interface MovieRepository extends JpaRepository<Movie, Long> {}
```

That's it, believe it or not. Injecting the newly created repository in our controller class implemented in the first task and we are ready to store movies.

Try it out by adapting the *GetMapping* (*findAll()*) and the *PostMapping* (*save(movie)*) methods.

Three isolated classes without any relationship? Not a good use case at all! </br>
So we have a *1:N* relationship between *FilmStudio* and *Movie* and a *N:M* relationship between *Movie* and *Actor*.
The first step is to add attributes to the classes:
 - In *Actor* add a *list of movies* attribute with a *@ManyToMany* association.
 - In *Movie* add a *list of actors* attribute with a *@ManyToMany* association.
 - In *FilmStudio* add a *list of movies* attribute with a *OneToMany* association.


The result are three directed relationships, resulting in three join tables in the data schema.
To resolve the *N:M* mapping, we can introduce a join table at the owning side of this relation (we decide that *Movie* will be the owning side of the relation).
**Mnemonic**: Owning side normally means, where the foreign key of the dependent class is stored. </br>
```Java
public class Movie {
  ...
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name="movie_actor",
      joinColumns=@JoinColumn(name="movie_id"),
      inverseJoinColumns = @JoinColumn(name="actor_id"))
  private List<Actor> actors;
  ...
}

public class Actor {
  ...
  @ManyToMany(fetch = FetchType.LAZY, mappedBy = "actors", cascade={MERGE,PERSIST})
  private List<Movie> movies;
  ...
}
```
The inverse side of the relationship is marked by the *mappedBy* property of the corresponding relation.

To enable also a bidirectional relationship between *FilmStudio* and *Movie*, we add a *FilmStudio* attribute with a *@ManyToOne* annotation to the *Movie* class.
Since *ManyToOne* is per default the owning side, we have to enrich the *@OneToMany* in *FilmStudio* with a *mappedBy* property.
```java
public class Movie {
  ...
  // Due to merge, the film studio will be stored when it is not present in the database
  @ManyToOne(cascade = CascadeType.MERGE) // default fetch type: EAGER
  private FilmStudio filmStudio;
  ...
}

public class FilmStudio {
  ...
  @OneToMany(mappedBy = "filmStudio")
  private List<Movie> movies;
  ...
}
```

In our solution for the second task is a *DemoData* class. This is not a must here, but quite beneficial when you want to test the web browser with default data and also the associations.
It contains a method which is executed on start up and stores some sample data.
You can copy this class from the *solutions/task2* folder, but **you may have to adapt the names** etc.
If you want to add more data to this demo class feel free.

When starting the application, you will get a *LazyInitializationException* since all collection associations are normally stored in different tables and per default not fetched eagerly.
Do not change the *FetchType* from *LAZY* to *EAGER*.
This will introduce the discussed performance issues.
Use *named entity graphs* for *Movie* and *Actor* to define the methods which read the associated data.
This gives you the control that only these methods will have the associations initialized and therefore you are the master of the database performance.
The results are a lot of other *LazyInitializationExceptions* during development since you use a strict loading of associations, but during development this should be worth the effort for the better runtime performance.

The following code snippets shows the annotations needed to add *named entity graph* support for movies.
```java
public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Override
    @EntityGraph(value="Movie.movies") // entity graph solution
    Optional<Movie> findById(Long aLong);

    @Override
    @EntityGraph(value="Movie.movies") // entity graph solution
    Iterable<Movie> findAll();
}

@NamedEntityGraph(name = "Movie.movies", // entity graph solution
        attributeNodes = @NamedAttributeNode(value="actors")) // entity graph solution
public class Movie {
  ...
}
```
You can fix the problem for actor similarly.

When working with databases and to understand the implications of *named entity graphs* as shown in the training, it is often a good idea to enable tracing of SQL statements in your console. Enable this and also the pretty print facility via the following properties:

```bash
# SQL statements are only printed in the DEBUG mode - setting it to INFO will keep your output clean of SQL statements
logging.level.org.hibernate.SQL=DEBUG
# pretty format your output, otherwise you can't read anything by all the numbers used
spring.jpa.properties.hibernate.format_sql=true
# you will see the values, which are used to execute queries, especially within the WHERE clause
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
# same setting for transaction management (you will see the transaction context and involved connections to the db)
logging.level.org.hibernate.transaction=TRACE
logging.level.org.springframework.transaction=TRACE
```

Now we are finished with the second task. A possible solution is included in *solutions/task2*.

Back to the [main page](README.md).
