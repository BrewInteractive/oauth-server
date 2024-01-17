<p  align="center">
<a  href="http://brewww.com/"  target="_blank"><img  src="https://github.com/BrewInteractive/oauth-server/blob/main/Brew-Logo-Small.png?raw=true"  width="300"  alt="Brew Logo"  /></a>
</p>

<h1  align="center">OAuth Server</h1>

<p align="center">
<a href="https://sonarcloud.io/summary/overall?id=BrewInteractive_oauth-server" target="_blank"><img src="https://sonarcloud.io/api/project_badges/measure?project=BrewInteractive_oauth-server&metric=alert_status"/></a>
<a href="https://sonarcloud.io/summary/overall?id=BrewInteractive_oauth-server" target="_blank"><img src="https://sonarcloud.io/api/project_badges/measure?project=BrewInteractive_oauth-server&metric=coverage"/></a>
<a href="https://hub.docker.com/repository/docker/brewery/oauth-server/general" target="_blank"><img src="https://img.shields.io/docker/pulls/brewery/oauth-server" alt="Docker Pulls" /></a> 
<a href="https://github.com/BrewInteractive/oauth-server/blob/main/LICENSE?raw=true" target="_blank"><img src="https://img.shields.io/github/license/BrewInteractive/oauth-server" alt="Package License" /></a>
</p>
<p align="center">
<a href="https://www.instagram.com/brew_interactive/" target="_blank"><img src="https://img.shields.io/badge/Instagram-E4405F?style=for-the-badge&logo=instagram&logoColor=white" alt="Instagram" /></a>
<a href="https://www.linkedin.com/company/brew-interactive/" target="_blank"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white" alt="Linkedin" /></a>
<a href="https://twitter.com/BrewInteractive" target="_blank"><img src="https://img.shields.io/badge/Twitter-1DA1F2?style=for-the-badge&logo=twitter&logoColor=white" alt="Twitter" /></a>
</p>

## Purpose

This project contains implementation of [OAuth 2.0](https://www.rfc-editor.org/rfc/rfc6749) which is the
industry-standard protocol for authorization. It's developed by
using [Spring Boot](https://spring.io/guides/gs/spring-boot/) framework in Java.

## Usage Instructions

These instructions provide information on how to use the oauth-server project.

### Starting Locally

You can run the server in your local environment with below commands.

#### Dependency Installation

```bash
mvn clean install
```

#### Database Connection

You will need a postgres database with OAuth Server entities to run the server. You can run
the [OAuth Server Hasura](https://github.com/BrewInteractive/oauth-server-hasura) in your local environment to get a
proper database.

#### Environment Variables

You will need to add below environment variables to run the server. These values must be provided in **env.properties**
file in the root directory.:

| Variable Name                 | Description                                                          | Required | Default Value |
| ----------------------------- | -------------------------------------------------------------------- | :------: | :-----------: |
| DB_HOST                       | Represents the hostname of the database that needs to be connected.  |   YES    |       -       |
| DB_NAME                       | Represents the name of the database that needs to be connected.      |   YES    |       -       |
| DB_USER                       | Represents the user of the database that needs to be connected.      |   YES    |       -       |
| DB_PASSWORD                   | Represents the password of the database that needs to be connected.  |   YES    |       -       |
| AUTHORIZATION_CODE_EXPIRES_MS | Represents the expiry time of authorization code in milliseconds.    |    No    |    300000     |
| LOGIN_SIGNUP_ENDPOINT         | Represents the login/signup endpoint of the implementer application. |   YES    |       -       |
| COOKIE_ENCRYPTION_ALGORITHM   | Represents the algorithm while decrypting the user cookie.           |   YES    |       -       |
| COOKIE_ENCRYPTION_SECRET      | Represents the secret while decrypting the user cookie.              |   YES    |       -       |
| ENABLE_SWAGGER                | Represents the enabling or disabling the swagger.                    |    NO    |       -       |

#### Starting the Server

```bash
mvn spring-boot:start
```

#### Stopping the Server

```bash
mvn spring-boot:stop
```

### Running with Docker

You can run the server in Docker environment with below commands:

First, you need to build the image:

```bash
docker build . -t oauth-server
```

Then, you can run the image with following command:

```bash
docker run -d -p 8080:8080 -e DB_HOST=<database_host> -e DB_NAME=<database_name> -e DB_USER=<database_username> -e DB_PASSWORD=<database_password> oauth-server -e LOGIN_SIGNUP_ENDPOINT=<login_signup_endpoint>
```

### Running Tests

You can run the tests with below command:

```bash
mvn test
```

## License

OAuth Server is [MIT licensed](LICENSE).
