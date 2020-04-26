mvn install;
version=$(cat pom.xml | grep -oPm1 "(?<=<version>)[^<]+");
cp ~/.m2/repository/fr/leomelki/LoupGarou/$version/LoupGarou-$version-jar-with-dependencies.jar ~/workspace/THIRD_PARTY/LG-server/plugins/LoupGarou-complete-v$version.jar
