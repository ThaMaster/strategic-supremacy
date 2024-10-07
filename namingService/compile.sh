# Compile the naming service jar
mvn clean install

rm target/NamingService-1.0.jar
mv target/NamingService-1.0-shaded.jar target/NamingService.jar

# Install the naming service to repository
mvn install:install-file \
-Dfile=./target/NamingService.jar \
-DgroupId=se.umu.cs.ads.ns \
-DartifactId=NamingService \
-Dversion=1.0 \
-Dpackaging=jar
