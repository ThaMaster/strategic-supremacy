# Compile the naming service jar
cd ../namingService || exit
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

# Compile the game
cd ../game || exit
mvn clean install
rm target/StrategicSupremacy-1.0.jar
mv target/StrategicSupremacy-1.0-shaded.jar target/StrategicSupremacy.jar

# Start the game
java -jar target/StrategicSupremacy.jar