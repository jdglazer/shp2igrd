VERSION_INFO=src/main/resources/version.properties


VERSION=$( shell cat $VERSION_INFO |  | cut -d"\n" -f1 )
JAR=shp2igrd-${VERSION}.jar
RPM=igrd-builders-shp-${VERSION}.rpm

all:
clean:
${JAR}:
${RPM}:
