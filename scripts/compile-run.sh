# Compile the game
cd ../game || exit
mvn clean install
rm target/StrategicSupremacy-1.0.jar
mv target/StrategicSupremacy-1.0-shaded.jar target/StrategicSupremacy.jar

# Start the game
java -jar target/StrategicSupremacy.jar