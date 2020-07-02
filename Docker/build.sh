#/bin/bash

# build server in parent dir
cd ..
mvn install -Dmaven.test.skip=true
mv Server/dist/OpenAS2Server-2.10.0.zip Docker
cp ../aws/credentials Docker
cp ../config.xml Docker

# return to this directory
cd Docker

docker build -t openas2-server:1.0.0 .

rm credentials
rm config.xml
