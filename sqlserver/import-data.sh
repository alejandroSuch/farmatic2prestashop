#wait for the SQL Server to come up
sleep 30s

#run the setup script to create the DB and the schema in the DB
/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P AdministradoR001 -d master -i setup.sql
