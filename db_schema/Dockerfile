# Docker file for postgresql 9.4 on debain
#
# build with e.g. `docker build --force-rm=true -t koala/lada_db .',
# then run with e.g.
# `docker run --name lada_db -dp 2345:5432 koala/lada_db:latest'
#

FROM debian:jessie
MAINTAINER tom.gottfried@intevation.de

#
# Use utf-8
#
RUN echo \
    "locales locales/locales_to_be_generated multiselect en_US.UTF-8 UTF-8" | \
    debconf-set-selections && \
    echo "locales locales/default_environment_locale select en_US.UTF-8" | \
    debconf-set-selections

RUN apt-get update -y && apt-get install -y locales

ENV LC_ALL en_US.UTF-8

#
# Install packages
#
RUN apt-get update && \
    apt-get install -y postgresql-9.4-postgis-2.1 postgis

#
# Use user postgres to run the next commands
#
USER postgres

RUN /etc/init.d/postgresql start && \
    psql --command "CREATE USER admin WITH SUPERUSER PASSWORD 'secret';"

#
# Adjust PostgreSQL configuration so that remote connections to the
# database are possible.
#
RUN echo "host all  all    0.0.0.0/0  md5" >> \
    /etc/postgresql/9.4/main/pg_hba.conf

RUN echo "listen_addresses='*'" >> /etc/postgresql/9.4/main/postgresql.conf

#
# Expose the PostgreSQL port
#
EXPOSE 5432

#
# Create database
#
# Don't mind scary messages like
# 'FATAL: the database system is starting up'.
# It's because of the -w
#
ADD . /opt/lada_sql/

RUN /usr/lib/postgresql/9.4/bin/pg_ctl start -wD /etc/postgresql/9.4/main/ && \
    /opt/lada_sql/setup-db.sh

#
# Set the default command to run when starting the container
#
CMD ["/usr/lib/postgresql/9.4/bin/postgres", "-D", \
     "/var/lib/postgresql/9.4/main", "-c", \
     "config_file=/etc/postgresql/9.4/main/postgresql.conf"]
