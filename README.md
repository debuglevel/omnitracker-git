<!--- some badges to display on the GitHub page -->

![Travis (.org)](https://img.shields.io/travis/debuglevel/omnitracker-git?label=Travis%20build)
![Gitlab pipeline status](https://img.shields.io/gitlab/pipeline/debuglevel/omnitracker-git?label=GitLab%20build)
![GitHub release (latest SemVer)](https://img.shields.io/github/v/release/debuglevel/omnitracker-git?sort=semver)
![GitHub](https://img.shields.io/github/license/debuglevel/omnitracker-git)

# OMNITRACKER git

OMNITRACKER stores scripts in the `scripts` database table. Unfortunately, there is no version control of these scripts (except your database backups).
`omnitracker-git` extracts all scripts from the database and commits them into a git repository. It's a REST microservice which commits all scripts on a POST request. This can also be scheduled to be done e.g. every 60 seconds.


# HTTP API

## Start commit

To commit scripts to the git repository, POST a request:

```
$ curl -X POST 'http://localhost:8080/repository/' --header 'Authorization: Basic U0VDUkVUX1VTRVJOQU1FOlNFQ1JFVF9QQVNTV09SRA=='
```

## List all scripts
You can also list all existing scripts (although this is rather a command to check for a working database connection than anything useful):
```
curl -X GET 'http://localhost:8080/scripts/' --header 'Authorization: Basic U0VDUkVUX1VTRVJOQU1FOlNFQ1JFVF9QQVNTV09SRA=='
```

# Configuration

There is a `application.yml` included in the jar file. Its content can be modified and saved as a separate `application.yml` on the level of the jar file. Configuration can also be applied via the other supported ways of Micronaut (see <https://docs.micronaut.io/latest/guide/index.html#config>). For Docker, the configuration via environment variables is the most interesting one (see `docker-compose.yml`).

# Docker
See `docker-compose.yml` for an example how to run this tool with Docker. 

# Security
For MSSQL, you should create a special user which has read-only access the `scripts`, `ProblemArea`, `stringTranslations` and `stringTransShort` tables.

# Database types
MSSQL (official Microsoft JDBC driver) and Microsoft Access (via `ucanaccess` JDBC driver; but rather slow) are supported.

