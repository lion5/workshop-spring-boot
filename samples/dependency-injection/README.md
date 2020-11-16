# Dependency Injection and Annotation Sample

This is a dependency and annotation sample for configuration options with properties and profiles.
It also shows a minimum example of dependency injection, where we create different **Construction** implementations, dependent on the set profile.

For ease of use, we expose a single REST endpoint in **de.lion5.spring.sample.di.rest.VehicleController**.
You can use this endpoint via http://localhost:7777/vehicle. We configured the port in the **application.properties**.
In case you face any problems with port conflicts, change the port there.

As discussed, we use a different **Construction** implementation dependent on the profile. 
In **de.lion5.spring.sample.di.configuration.Vehicles** you recognize, that the class is annotated with **@Configuration**.
So during the classpath scanning process, this class is recognized as a candidate and the **@Bean** annotated factory methods are executed.
Since these methods are additionally annotated with **@Profile**, they are only instantiated at runtime, when the corresponding profile is set.
If two profiles are set, e.g. **dev** and **prod**, you will get a runtime error.

What we forgot to mention so far is the attribute **wheels** in **Bicycle**, **Tricycle** and **Car** class.
This property is specified in the corresponding property files and read via the **@Value("${vehicle.wheels}")** annotation.

Play a bit around, you can specify the active profile as a VM argument in your IDE or at command line via **-Dspring.profiles.active=PROFILE**.
If you do not specify a profile, the **default** profile is used (**application.properties**).