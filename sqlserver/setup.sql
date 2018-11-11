RESTORE FILELISTONLY
FROM DISK = '/usr/src/backups/BACKUP.DAT'
GO

RESTORE DATABASE FARMACIA
FROM DISK = '/usr/src/backups/BACKUP.DAT'
WITH MOVE 'FARMACIA' TO '/tmp/FARMACIA.MDF',
	 MOVE 'FARMACIALog' TO '/tmp/FARMACIALog.LDF';
