# Strings database

The "Strings database" is a small application for serving a database of strings.
Users can save new strings to the database or search for previously stored strings.

This is a small Spring Boot Java application for demonstration purposes. It uses a
MySQL database to store the strings and contains a small React application as a
frontend. The React application uses [babel-standalone](https://babeljs.io/docs/en/)
to compile the application in real-time in the user's browser. In production environments,
however, we should use a build system running on Node.js. The choice for
[babel-standalone](https://babeljs.io/docs/en/) in this case is merely for demonstration
purposes.

## How to run the application

You will need [Docker](https://www.docker.com/) and [docker-compose](https://docs.docker.com/compose/).
If you do not have them already installed, please follow
[these](https://docs.docker.com/engine/install/) and [these](https://docs.docker.com/compose/install/)
setup instructions.

### 1. Clone this repository in your local machine

```sh
git clone REPO_URL
```

### 2. Setup environment configuration

You may set up your environment variables by creating a file `.env` in your project root.
You may copy the file `.env.example` and define your own values for the informed variables.

Please remember to update the Spring Boot configuration `.yml` files at `src/main/resources/`
accordingly.

### 3. Build and start the development containers

The following command will download the required images, prepare, and start the development
containers. The command will run in the background.

```sh
docker-compose up -d
```

From now on, we will execute commands in the application container:

```sh
docker exec -it smart-hardware-shop-app echo "running in container"
```

If you are running a Unix compatible environment, you can use the `ric.sh` script:

```sh
sh ric.sh echo "running in container"
```

or:

```sh
chmod +x ric.sh
./ric.sh echo "running in container"
```

We will use the `ric.sh` script in the following examples.

### 4. Test the application (optional)

```sh
sh ric.sh mvn clean test
```

### 5. Install the application

```sh
sh ric.sh mvn clean install
```

### 6. Start the application

```sh
sh ric.sh mvn spring-boot:run
```

## Accessing the application

The application is available at [http://localhost:8081/](http://localhost:8081/).
