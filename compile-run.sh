# Compile the naming service
cd ./namingService
bash ./compile.sh

# Compile the game
cd ../game
bash ./compile.sh

# Start the game
java -jar target/StrategicSupremacy.jar