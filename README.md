OMNITRACKER stores scripts in the `scripts` database table.
Unfortunately, there is no version control of these scripts (except your
database backups). This tool extracts all scripts and commits them in a
git repository. This can be run as a one-shot or as a daemon (pulling
every X seconds).

# Usage
```
$ java -jar .\build\libs\omnitrackergit-0.0.1-SNAPSHOT-all.jar
Usage: omnitrackergit [OPTIONS] COMMAND [ARGS]...

Options:
  -h, --help  Show this message and exit

Commands:
  list    List all scripts
  export  Export scripts into local directory
  commit  Commit scripts to git repository
```

An `configuration.properties` file must exist in the working directory
(or the corresponding environment variables, e.g. `DATABASE_CONNECTION_STRING`, are set) to
determine the database and the git settings:
```
database.connection.string=jdbc:ucanaccess://OT-Templates.mdb
#database.connection.string=jdbc:sqlserver://myhost\\MYINSTANCE;databaseName=mydatabase;user=myuser;password=mypassword
git.repository.uri=https://git.myhost.com/omnitrackerscripts
git.user=myUser
git.password=myPassword
```
Microsoft Access (via `ucanaccess` JDBC driver) and MSSQL (official
Microsoft JDBC driver) are supported.

# Security
For MSSQL, you should create a special user which has read-only access the
`scripts` and `stringTranslations` tables.

# List all scripts
```
$ java -jar .\build\libs\omnitrackergit-0.0.1-SNAPSHOT-all.jar list
274     | \(Alle Ordner)\BasicStat_Snapshot_AddPercentage
316790  | \(Alle Ordner)\00. Basisdaten\00. 01 Web-Content\CancelAction Check Start and End Visible Date
306858  | \(Alle Ordner)\00. Basisdaten\00. 02 Web-Navigation\CancelAction Check Start and End Visible Date
540737  | \(Alle Ordner)\00. Basisdaten\00. 02 Web-Navigation\Declarations
540740  | \(Alle Ordner)\00. Basisdaten\00. 02 Web-Navigation\OnAfterFieldChanged
540744  | \(Alle Ordner)\00. Basisdaten\00. 02 Web-Navigation\OnClick
540741  | \(Alle Ordner)\00. Basisdaten\00. 02 Web-Navigation\OnClick
540742  | \(Alle Ordner)\00. Basisdaten\00. 02 Web-Navigation\OnClick
540743  | \(Alle Ordner)\00. Basisdaten\00. 02 Web-Navigation\OnClick
540738  | \(Alle Ordner)\00. Basisdaten\00. 02 Web-Navigation\OnOpen
540739  | \(Alle Ordner)\00. Basisdaten\00. 02 Web-Navigation\OnTimer
458804  | \(Alle Ordner)\00. Basisdaten\00. 03 Web-Funktionen\OnOpen
```

# Export all scripts
```
$ java -jar .\build\libs\omnitrackergit-0.0.1-SNAPSHOT-all.jar export myDirectory
```

# Commit all scripts
```
$ java -jar .\build\libs\omnitrackergit-0.0.1-SNAPSHOT-all.jar commit
```