# Compile the naming service

bash ../namingService/compile.sh

# Compile the game
bash ../gamecompile.sh

# Start the server in another terminal
gnome-terminal -- java -jar ../namingService/target/NamingService.jar

# Start the game
java -jar ../game/target/StrategicSupremacy.jar