mvn clean install
rm target/NamingService-1.0.jar
mv target/NamingService-1.0-shaded.jar target/NamingService.jar
java -jar target/NamingService.jar