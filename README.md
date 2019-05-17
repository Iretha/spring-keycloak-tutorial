# How to secure Spring Boot Frontend with Keycloak and Spring Security (Login (Username/Password + Social) & Logout)

This tutorial will show you how to secure a simple Spring Boot project using Keycloak. 
We will implement login and logout actions, so that you can perform the full login-logout cycle. 
In order to achieve this, we need a spring boot web application (our demo-web-app) and a keycloak installation.

The source code is available on [GitHub](https://github.com/Iretha/spring-keycloak-tutorial).

## Keycloak Installation & Setup

Please, follow the steps from the official [Getting Started](https://www.keycloak.org/docs/latest/getting_started/index.html) guide, in order to install it..

##### Set Port Offset & Start the Server

For the purpose of this project, I use the standalone option with port offset = 100. 
Otherwise keycloak server will run on it's default port 8080, where my Tomcat is located.

To change the port, start the server with an argument "Djboss.socket.binding.port-offset=<PORT_OFFSET>".
* Linux
```bash
$ cd bin
$ ./standalone.sh -Djboss.socket.binding.port-offset=100
```
* Windows
```bash
...\bin\standalone.bat -Djboss.socket.binding.port-offset=100
```

##### Create the Admin Account

By default the admin console is published on [http://localhost:8080/auth](http://localhost:8080/auth).
Don't forget to change the port, if you have a port offset. 
In my case, the admin console is available on [http://localhost:8180/auth](http://localhost:8180/auth)

Follow the [Official Guide](https://www.keycloak.org/docs/latest/getting_started/index.html#creating-the-admin-account)

##### Logging in to the Admin Console

Go to [http://localhost:8180/auth/admin](http://localhost:8180/auth/admin) and verify our admin account.

Follow the [Official Guide](https://www.keycloak.org/docs/latest/getting_started/index.html#logging-in-to-the-admin-console)

##### Create "dev" Realm 

For the purpose of this project, I'm going to create a new realm, called "dev".

Follow the [Official Guide](https://www.keycloak.org/docs/latest/getting_started/index.html#_create-realm)

##### "Dev" Realm: Create "user" Role

For the purpose of this project, I'm going to create a new role, called "user".

Go to "Role", click "Add Role" and enter "user" as a role name.

##### "Dev" Realm: Create User 

For the purpose of this project, I'm going to create a new user, called "devuser".

* Go to "Users", click on "Add User". Enter "devuser" as username and press "Save". 
* Go to "Credentials" and enter password, click "Reset Password" to save it.
* Go to "Role Mappings" and add "user" as a role.

Follow the [Official Guide](https://www.keycloak.org/docs/latest/getting_started/index.html#_create-new-user)

##### "Dev" Realm: Register "web-app-client" Client

Create a new client called "web-app-client". This is the client, we are going to use for our web app.
* Go to "Clients" and press "Create". Enter "web-app-client" as client id, click "Save".
* Enter "Valid Redirect URIs" (in my case it is http://localhost:8080/*) and press "Save"

This is the location of your demo-web-app, which we are going to create as a second step.
My demo-web-app app will be published on "http://localhost:8080"

Follow the [official guide](https://www.keycloak.org/docs/latest/getting_started/index.html#creating-and-registering-the-client)


Now we are ready to proceed with the spring app.

## Our Frontend Application (demo-web-app)
We are going to use a simple Spring Boot application as our frontend (demo-web-app). 
You may use [Spring Initailizr](https://start.spring.io/) and let spring generate a project for you or do it by yourself. 
You may also get the source code from [github](https://github.com/Iretha/spring-keycloak-tutorial).

1. Project Settings
- Gradle (5.4.1) - you may use Maven
- Java 8
- Spring Boot 2.2.0 (SNAPSHOT)
- Packaging: War
- Dependencies: Web, Devtools
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
