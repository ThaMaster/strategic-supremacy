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

cd ../scripts || exit
# Start the server in another terminal
gnome-terminal -- java -jar ../namingService/target/NamingService-1.0-shaded.jar

# Start the game
java -jar ../game/target/StrategicSupremacy-1.0-shaded.jar