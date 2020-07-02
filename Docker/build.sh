#/bin/bash

# build server in parent dir
cd ..
mvn install -Dmaven.test.skip=true
mv Server/dist/OpenAS2Server-2.10.0.zip Docker
if [ -f "../aws/credentials" ]; then
  cp ../aws/credentials Docker
fi
if [ -f "../config.xml" ]; then
  cp ../config.xml Docker
fi

# return to this directory
cd Docker

docker build -t openas2-server:1.0.0 .

rm *.zip
rm credentials
rm config.xml
