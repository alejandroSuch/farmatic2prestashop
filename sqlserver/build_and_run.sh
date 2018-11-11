#!/bin/bash

docker build -t sqlserver .

if [[ -z "${IMPORT_DATA}" ]]; then
  echo "uno"
  docker run -it -p 1433:1433 --name sqlserver sqlserver
else
  docker run -it -p 1433:1433 -e "IMPORT_DATA=true" -v $(pwd)/../database/:/usr/src/backups/ --name sqlserver sqlserver
fi

