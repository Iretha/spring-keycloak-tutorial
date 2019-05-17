# How to secure Spring Boot Frontend with Keycloak and Spring Security

With this tutorial I will show you how to secure a simple Spring Boot application using Keycloak. 

We are going to implement simple login (with username and password) and logout. 
Later we will extend it with a social login (google/ facebook/ github etc).

In order to achieve this, we need to "Install & Setup our Keycloak Instance" and "Create and Setup a *demo-web-app* using Spring Boot".

The source code is available on [GitHub](https://github.com/Iretha/spring-keycloak-tutorial).

## Keycloak Installation & Setup

### Installation

The initial installation & setup is not a subject of this guide, although you can find some guidelines below. 
To install it, please follow the official [Getting Started](https://www.keycloak.org/docs/latest/getting_started/index.html) 
guide and then come back to do what's needed to secure our web application. 

## Setup

### Set Port Offset & Boot the Keycloak Instance
I will use the standalone mode with port offset = 100. 
Otherwise Keycloak will run on it's default port 8080, where actually my Tomcat instance runs.

To change the port, start Keycloak with an additional argument "Djboss.socket.binding.port-offset=<PORT_OFFSET>".
* Linux
```bash
$ cd bin
$ ./standalone.sh -Djboss.socket.binding.port-offset=100
```
* Windows
```bash
...\bin\standalone.bat -Djboss.socket.binding.port-offset=100
```

You can set the port offset permanently if you edit "standalone.xml". 
Find "jboss.socket.binding.port-offset" and change the offset:

```xml
jboss.socket.binding.port-offset:100
```

For more information, visit [the official guide](https://www.keycloak.org/docs/2.5/server_installation/topics/network/ports.html).

### Create the Admin Account

If your Keycloak instance is running, you can create an admin account.

By default the admin console is published on [http://localhost:8080/auth](http://localhost:8080/auth).
Don't forget to change the port, if you have a port offset. 
In my case, the admin console is available on [http://localhost:8180/auth](http://localhost:8180/auth)

Follow the [Official Guide](https://www.keycloak.org/docs/latest/getting_started/index.html#creating-the-admin-account)

### Logging in to the Admin Console

Go to [http://localhost:8180/auth/admin](http://localhost:8180/auth/admin) and verify our admin account.

Follow the [Official Guide](https://www.keycloak.org/docs/latest/getting_started/index.html#logging-in-to-the-admin-console)

### Create "dev" Realm 

For the purpose of this project, I'm going to create a new realm, called "dev".

Follow the [Official Guide](https://www.keycloak.org/docs/latest/getting_started/index.html#_create-realm)

### "Dev" Realm: Create "user" Role

For the purpose of this project, I'm going to create a new role, called "user".

Go to "Role", click "Add Role" and enter "user" as a role name.

### "Dev" Realm: Create User 

For the purpose of this project, I'm going to create a new user, called "devuser".

* Go to "Users", click on "Add User". Enter "devuser" as username and press "Save". 
* Go to "Credentials" and enter password, click "Reset Password" to save it.
* Go to "Role Mappings" and add "user" as a role.

Follow the [Official Guide](https://www.keycloak.org/docs/latest/getting_started/index.html#_create-new-user)

### "Dev" Realm: Register "web-app-client" Client

Create a new client called "web-app-client". This is the client, we are going to use for our web app.
* Go to "Clients" and press "Create". Enter "web-app-client" as client id, click "Save".
* Enter "Valid Redirect URIs" (in my case it is http://localhost:8080/*) and press "Save"

This is the location of your demo-web-app, which we are going to create as a second step.
My demo-web-app app will be published on "http://localhost:8080"

Follow the [official guide](https://www.keycloak.org/docs/latest/getting_started/index.html#creating-and-registering-the-client)

## Our Spring Web Application (demo-web-app)
We are going to use a simple Spring Boot application as our frontend (demo-web-app). 

### Project Settings
* Gradle (5.4.1) - you may use Maven
* Java 8
* Spring Boot 2.2.0 (SNAPSHOT)
* Packaging: War
* Dependencies: Web, Devtools
* Name: demo-web-app

### Create Project
You may use [Spring Initailizr](https://start.spring.io/) and let spring generate a project for you or do it by yourself. 
You may also get the source code from [github](https://github.com/Iretha/spring-keycloak-tutorial).

### Open "demo-web-app"
Use your favourite IDE.

### Setup the embedded Tomcat

* Add 'tomcat-embed-jasper' dependency

At this point I'm going to add "providedRuntime group: 'org.apache.tomcat.embed', name: 'tomcat-embed-jasper'".

build.gradle
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

* Update the Application Config

Open your SpringBootApplication and extend SpringBootServletInitializer. 
Then override tomcatFactory() in order to disable the Manifest Scanning of the jars.

If not disabled, it may cause various issues like "FileNotFound" or "Duplicate context initialization parameter" Exceptions.

For more information, please follow [this link](https://stackoverflow.com/questions/43264890/after-upgrade-from-spring-boot-1-2-to-1-5-2-filenotfoundexception-during-tomcat#43280452).

```java
@SpringBootApplication
public class Application extends SpringBootServletInitializer {
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }
    	
	/**
	 * https://stackoverflow.com/questions/43264890/after-upgrade-from-spring-boot-1-2-to-1-5-2-filenotfoundexception-during-tomcat#43280452
	 * @return
	 */
	@Bean
	public TomcatServletWebServerFactory tomcatFactory() {
		return new TomcatServletWebServerFactory() {
			@Override
			protected void postProcessContext(Context context) {
				((StandardJarScanner) context.getJarScanner()).setScanManifest(false);
			}
		};
	}
}
```


### Create the folder structure "webapp-> WEB-INF-> views"

We will place our JSP-s under "webapp->WEB-INF->views".

![](https://github.com/Iretha/spring-keycloak-tutorial/blob/master/images/project_structure.png==250x)

### Create MvcConfiguration.java

We need to configure our ResourceViewResolver, so that Spring knows where our pages are located and what suffix they will use.

MvcConfiguration.java
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

### Create a home.jsp 

This is a sample page we are going to protect with Keycloak. 

The only thing this page does is to display the name of the user, who is currently logged in.
If there is no user logged in, it will show "Guest" as a name. 
The ${name} will be loaded from the Spring MVC model.

Create it under "/webapp/WEB-INF/views/" with the following content:
home.jsp
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Hello, ${name}!</title>
<%--    <link href="/static/css/style.css" rel="stylesheet">--%>
</head>
<body>
<h2 style="color: darkcyan;">Hello, ${name}!</h2>

<form method="post" action="/logout-from-keycloak">
    <input type="submit" value="Logout"/>
</form>

<%--<script src="/static/js/user.js"></script>--%>
</body>
</html>
```

### Create /controller/HomeController.java

This controller will add the username as a name to the Spring MVC model (to be available for home.jsp).
At this point we will always return "Guest". Later, when we turn on the security, 
we will uncomment the code block and put the real username instead of "Guest".

```java
@Controller
public class HomeController {

    /**
     * Handles home requests and adds name of the user to the model, so that we can display it on the home page
     *
     * @param model
     * @return
     */
    @GetMapping({"/", "/home"})
    public String home(Model model) {
        String username = getUsername("Guest");

        model.addAttribute("name", username);
        return "/home";
    }

    /**
     * Retrieves the username of the user
     *
     * @param defaultUsername
     * @return
     */
    public String getUsername(String defaultUsername) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication.getPrincipal() == null) {
//            return defaultUsername;
//        }
//
//        if (authentication.getPrincipal() instanceof KeycloakPrincipal) {
//            KeycloakPrincipal userDetails = (KeycloakPrincipal) authentication.getPrincipal();
//            IDToken idToken = userDetails.getKeycloakSecurityContext().getIdToken();
//            return idToken != null ? idToken.getPreferredUsername() : idToken.getGivenName();
//        } else if (authentication.getPrincipal() instanceof String) {
//            return String.class.cast(authentication.getPrincipal());
//        }
        return defaultUsername;
    }
}
```

### Run your app
Let's run the project and check how it goes.

Go to [http://localhost:8080/home](http://localhost:8080/home)

What we expect to see is: Hello, Guest!

Now our sample project is up and running!
