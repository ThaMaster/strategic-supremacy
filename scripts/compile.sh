# Compile the naming service jar
cd ../namingService || exit
mvn clean install

# Install the naming service to repository
mvn install:install-file \
-Dfile=./target/NamingService-1.0-shaded.jar \
-DgroupId=se.umu.cs.ads.ns \
-DartifactId=NamingService \
-Dversion=1.0 \
-Dpackaging=jar

# Compile the game
cd ../game || exit
mvn clean install