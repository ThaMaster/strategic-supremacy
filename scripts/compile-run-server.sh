cd ../namingService || exit
mvn clean install

# Install the naming service to repository
mvn install:install-file \
-Dfile=./target/NamingService-1.0-shaded.jar \
-DgroupId=se.umu.cs.ads.ns \
-DartifactId=NamingService \
-Dversion=1.0 \
-Dpackaging=jar

cd ../scripts || exit
# Start the server in another terminal
java -jar ../namingService/target/NamingService-1.0-shaded.jar