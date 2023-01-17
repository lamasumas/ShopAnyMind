For simplicity of setup and assuming that postgresql, we will remove the postgres user password: So, in the file pg_hba.conf we will change it to:
"""
1-password.png
"""

After that, execut the database setup script:
	* If you use linux: run "sh setup_linux.sh"
	* If you use Windows: execute "setup_windows.bat"
Independly of the os, this will run the "setup.sql", which will create the database, the table that we will use and it will fill it with some examples.


