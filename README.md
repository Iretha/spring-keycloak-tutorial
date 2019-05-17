# How to secure Spring Boot Frontend with Keycloak and Spring Security

We need to setup a Keycloak instance first and then we are going to create a sample spring boot web app, 
which will use the keycloak as identity and access management solution.

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

```
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

### !!! SSH Tunnel may be needed !!!
If your Keycloak instance is installed on a different machine (not the one where Spring app will run), 
you should create a ssh tunnel between both.

Go to the machine, where Spring app will run and make a tunnel the the machine, where keycloak runs.

* Linux
```bash
$ ssh <user>@<KEYCLOAK_HOST_IP> -L 8180:127.0.0.1:8180
$ Confirm with your <user_password>
```
i.e.
$ ssh dev@192.168.1.122 -L 8180:127.0.0.1:81800

* Windows
You may use PuTTY or similar. 
There are many tutorials you can follow i.e. [this one](https://www.skyverge.com/blog/how-to-set-up-an-ssh-tunnel-with-putty/)

## Create Spring Boot Demo Web App and secure it with Keycloak and Spring Security (Spring Boot Adapter)

The tutorial is available under the [demo-web-app](https://github.com/Iretha/spring-keycloak-tutorial/tree/master/demo-web-app/README.md) folder.
