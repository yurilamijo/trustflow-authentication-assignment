# trustflow-authentication-assignment

### Setup the database
1. Execute the ``trustflow-assignment.sql`` file to create the database tables and to add some default data.
2. Add your database url, username and password in the `application.yaml` to correctly configure the database.

### Run the backend API
1. Startup the backend up with `Intellij` through `Application.kt`
2. Confirm that the given localhost URL is the same that is defined in the front-end project. If not please change it accordingly.

### Optional - Import the Postman environment and collections
1. `trustflow.postman_collection.json` contains the API call
2. `trustflow.postman_environment.json` contains the configured environment variables, like access tokens and session tokens

### User Login accounts
#### Admin Role
````aiignore
username: "YuriLamijo"
password: "PasswordYuri"
````

#### User Role
````aiignore
username: "RobberJan"
password: "PasswordRobbert"
````