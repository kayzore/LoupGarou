mvn install;
version=$(cat pom.xml | grep -oPm1 "(?<=<version>)[^<]+");
cp ~/.m2/repository/fr/leomelki/LoupGarou/$version/LoupGarou-$version-shaded.jar ~/workspace/THIRD_PARTY/LG-server/plugins/LoupGarou-v$version-shaded.jar
