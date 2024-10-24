# Compile the game
cd ../game || exit
mvn clean install

# Start the game
java -jar target/StrategicSupremacy-1.0-shaded.jar