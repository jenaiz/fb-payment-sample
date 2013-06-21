mvn clean
mvn package
java -jar target/dependency/jetty-runner.jar target/*.war
