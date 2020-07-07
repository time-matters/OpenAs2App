#/bin/bash

# build server in parent dir
cd ..

mvn install -Dmaven.test.skip=true

docker build -f Docker/Dockerfile -t openas2-server:1.0.0 .
