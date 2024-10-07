# Compile the naming service
cd ./namingService
bash ./compile.sh

# Compile the game
cd ../game
bash ./compile.sh

# Start the server in another terminal
gnome-terminal -- java -jar ../namingService/target/NamingService.jar

# Start the game
java -jar target/StrategicSupremacy.jar