#start SQL Server, start the script to create the DB and import the data, start the app
/opt/mssql/bin/sqlservr &

if [[ -z "${IMPORT_DATA}" ]]; then
  tail -f /bin/znew
else
  /usr/src/app/import-data.sh
  tail -f /bin/znew
fi
