# spring-keycloak-tutorial
Spring Frontend &amp; Keycloak as Authentication Server

### Spring Frontend & Keycloak Authentication Server

## Frontend Application (Spring Boot)
We are going to use a simple Spring Boot aplication as our frontend. You may go to [Spring Initailizr](https://start.spring.io/) and let spring generate a project for you or do it by yourself. You may also get the source code from [github](https://github.com/Iretha/spring-keycloak-tutorial).

1. Project Settings
-Gradle (5.4.1) - you may use Maven
-Java 8
-Spring Boot 2.2.0 (SNAPSHOT)
-Packaging: War
-Dependencies: Web, Devtools
1. Open the project with your favourite IDE
1. Add "tomcat-embed-jasper" as dependency
```Gradle
plugins {
	id 'org.springframework.boot' version '2.2.0.BUILD-SNAPSHOT'
	id 'java'
}
apply plugin: 'io.spring.dependency-management'
apply plugin: 'war'
war {
	enabled = true
}
group = 'com.smdev.demo'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '1.8'
repositories {
	mavenCentral()
	maven { url 'https://repo.spring.io/snapshot' }
	maven { url 'https://repo.spring.io/milestone' }
}
dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-web'
	runtimeOnly 'org.springframework.boot:spring-boot-devtools'
	providedRuntime group: 'org.apache.tomcat.embed', name: 'tomcat-embed-jasper'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
	testImplementation 'org.springframework.security:spring-security-test'
}
```
1. Create the following project structure:
![](https://github.com/Iretha/spring-keycloak-tutorial/blob/master/images/project_structure.png)
1. Create a home.jsp under "/webapp/WEB-INF/views/"
This is the sample page we are going to protect with Keycloak.
The home.jsp should have the following content:
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Hello, ${name}!</title>
</head>
<body>
<h2>Hello, ${name}!</h2>
</body>
</html>
```
1. Create HomeController.java:
```java
@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public String home(Model model, @RequestParam(value = "name", required = false, defaultValue = "Guest") String name) {
        model.addAttribute("name", name);
        return "home";
    }
}
```
1. Create MvcConfiguration.java
```java
@Configuration
@EnableWebMvc
@ComponentScan
public class MvcConfiguration implements WebMvcConfigurer {
    
    /**
     * Configure Views & View Resolver
     * @param registry
     */
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        InternalResourceViewResolver pageResolver = new InternalResourceViewResolver();
        pageResolver.setPrefix("/WEB-INF/views/");
        pageResolver.setSuffix(".jsp");
        registry.viewResolver(pageResolver);
    }
}
```
1. Let's run the project and check how it goes.
Go to [http://localhost:8080/home](http://localhost:8080/home)
What we expect to see is: Hello, Guest!
Another option to add your name [http://localhost:8080/home?name=John](http://localhost:8080/home?name=John)
What we expect to see is: Hello, John!

Now our sample project is up and running!
