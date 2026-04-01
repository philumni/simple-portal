FROM mariadb:11.7
WORKDIR /app

COPY cloud/db-init/init-db.sh ./
COPY cloud/sql ./sql

RUN chmod +x ./init-db.sh

CMD ["./init-db.sh"]
