#### Create Database
For this project create following database
```
fa_player_audit
```
#### Create Following Pubsub Topics
- com.thefa.audit.player.fnd
```
curl -X PUT localhost:8433/v1/projects/the-fa-api-dev/topics/com.thefa.audit.player.fnd
```
#### Create Following Pubsub Subscriptions
- com.thefa.audit.player.fnd.default
```
curl -X PUT localhost:8433/v1/projects/the-fa-api-dev/subscriptions/com.thefa.audit.player.fnd.default \ 
-H "Content-Type: application/json" \ 
-d '{"topic": "projects/the-fa-api-dev/topics/com.thefa.audit.player.fnd", "ackDeadlineSeconds": 600}'
```

``` For Windows ```

```curl -X PUT localhost:8433/v1/projects/the-fa-api-dev/subscriptions/com.thefa.audit.player.fnd.default -H "Content-Type: application/json" -d "{\"topic\": \"projects/the-fa-api-dev/topics/com.thefa.audit.player.fnd\", \"ackDeadlineSeconds\": 600}"```
- List Topics
```
curl -X GET localhost:8433/v1/projects/the-fa-api-dev/topics
```
- List Subscription
```
curl -X GET localhost:8433/v1/projects/the-fa-api-dev/subscriptions
```

#### Important Files
- **build.gradle**  - _Gradle Build File_
- **gradle.properties** - _Database Properties_
- **application.properties** - _Spring Boot Application Properties_
- **app.yaml** - _AppEngine Application (Service) Configuration_

#### Gradle Commands
- `./gradlew clean` Cleans the project
- `./gradlew build` Builds project
- `./gradlew build -x test` Build project without running tests
- `./gradlew test` Runs tests only
- `./gradlew clean build` Cleans and builds project
- `./gradlew bootRun` Run the application

#### FlywayDB Commands
- `-i` This flag can be used with the FlywayDB commands below to output info
- `./gradlew flywayClean` Deletes the whole schema (for Development Purpose)
- `./gradlew flywayMigrate` Migrates database to latest schema
- `./gradlew flywayInfo` Prints the details and status information about all the migrations
- `./gradlew flywayRepair` Repairs the Flyway schema history table
- `./gradlew flywayValidate` Validate applied migrations against resolved ones (on the filesystem or classpath) to detect accidental changes that may prevent the schema(s) from being recreated exactly. Validation fails if differences in migration names, types or checksums are found, versions have been applied that aren"t resolved locally anymore or versions have been resolved that haven"t been applied yet

#### Localhost Swagger URLs
- [http://localhost:8080/audit/swagger-ui.html](http://localhost:8080/audit/swagger-ui.html)
- [http://localhost:8080/audit/v2/api-docs](http://localhost:8080/audit/v2/api-docs)

#### Local Authentication Header
````
X-Endpoint-API-UserInfo = eyJjbGFpbXMiOiJ7XCJpc3NcIjpcImh0dHBzOi8vYWNjb3VudHMuZ29vZ2xlLmNvbVwiLFwiYXpwXCI6XCI0MDc0MDg3MTgxOTIuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb21cIixcImF1ZFwiOlwiNDA3NDA4NzE4MTkyLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tXCIsXCJzdWJcIjpcIjExMjIxMjk0Mzc5MjY3MTkzMTcyNlwiLFwiaGRcIjpcInRoZWZhLmNvbVwiLFwiZW1haWxcIjpcIm9tZXIuYXJzaGFkQHRoZWZhLmNvbVwiLFwiZW1haWxfdmVyaWZpZWRcIjp0cnVlLFwiYXRfaGFzaFwiOlwiM0VrX1VQRF9JMDJuc1hVM25pYVpvZ1wiLFwiaWF0XCI6MTU0MTU3MTE2OCxcImV4cFwiOjE1NDE1NzQ3Njh9IiwiaXNzdWVyIjoiaHR0cHM6Ly9hY2NvdW50cy5nb29nbGUuY29tIiwiaWQiOiIxMTIyMTI5NDM3OTI2NzE5MzE3MjYiLCJlbWFpbCI6Im9tZXIuYXJzaGFkQHRoZWZhLmNvbSJ9
````