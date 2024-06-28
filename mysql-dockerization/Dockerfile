FROM mysql:5.7
COPY sql-scripts/ /docker-entrypoint-initdb.d/
CMD ["docker-entrypoint.sh","mysqld"]