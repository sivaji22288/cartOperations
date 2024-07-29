# Getting Started

# Retail Store Cart Operations

This microservice is designed to manage logged-in User Cart operations.


## Prerequisites

Before you begin, ensure you have met the following requirements:

* You have installed the Java version 21.
* You have a Windows machine. This guide is tailored for Windows users.

## Setting Up

To set up the project, follow these steps:

1. Clone the repository from GIT `git clone -b master https://github.com/sivaji22288/cartOperations.git` to your local machine.
2. Navigate to the project directory.
3. Run `gradlew clean build` to build the project.

## Running the Application

To run the application, follow these steps:

1. Navigate to the project directory.
2. Run `gradlew bootRun` to start the application.

## Testing the Application

To test the application, follow these steps:

1. Navigate to the project directory.
2. Run `gradlew test` to execute the tests.

## Swagger Documentation

To View the swagger documentation, fallow these steps:
1. Launch the application.
2. Open any browser and navigate to `http://localhost:9388/swagger-ui/index.html` 

## SonarQube Report

To generate the SonarQube report, follow these steps:

1. Navigate to the project directory.
2. if you are running the SonarQube locally, then run the below command to generate the SonarQube report.
   `gradlew test jacocoTestReport sonarqube -Dsonar.host.url=http://localhost:9000 -Dsonar.login=admin -Dsonar.password=admin`
3. Open the browser and navigate to `http://localhost:9000/` to view the SonarQube report.
4. Login with the credentials `admin/admin`.
5. Navigate to the project and view the report.
6. You can also view the report by running the below command.
   `gradlew sonarqube -Dsonar.host.url=http://localhost:9000 -Dsonar.login=admin -Dsonar.password=admin`
7. If SonarQube is not running locally, then you can view the report by running the below command.
   `gradlew test jacocoTestReport sonarqube`
8. Navigate to `http://localhost:63342/cartOperations/build/jacocoHtml/index.html?_ijt=uhf68641g5bsg0ci4io0gthg1q&_ij_reload=RELOAD_ON_SAVE` to view the Jacoco report.

## Contributing to the Project

To contribute to this project, follow these steps:

1. Fork the repository.
2. Create a new branch: `git checkout -b <branch_name>`.
3. Make your changes and commit them: `git commit -m '<commit_message>'`.
4. Push to the original branch: `git push origin cartOperations/<location>`.
5. Create the pull request.

## Contact

If you want to contact me, you can reach me at `<sivaji.konapala@gmail.com>`.

## License


### Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.3.2/gradle-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.3.2/gradle-plugin/reference/html/#build-image)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/3.3.2/reference/htmlsingle/index.html#data.sql.jpa-and-spring-data)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.3.2/reference/htmlsingle/index.html#web)
* [Validation](https://docs.spring.io/spring-boot/docs/3.3.2/reference/htmlsingle/index.html#io.validation)

### Guides
The following guides illustrate how to use some features concretely:

* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Validation](https://spring.io/guides/gs/validating-form-input/)

### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

