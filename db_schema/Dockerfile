# Docker file for the LADA database on Debian
#
# build with e.g. `docker build --force-rm=true -t koala/lada_db .',
# then run with e.g.
# `docker run --name lada_db -dp 2345:5432 koala/lada_db:latest'
#
# For easier testing of schema or example data changes, it can be useful to add
# `-v $PWD:/opt/lada_sql/' and run setup-db.sh within the container.
#

FROM debian:stretch
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
    apt-get install -y curl unzip gnupg
RUN echo "deb http://apt.postgresql.org/pub/repos/apt/ stretch-pgdg main" \
    >> /etc/apt/sources.list
RUN curl https://www.postgresql.org/media/keys/ACCC4CF8.asc | apt-key add -
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
            postgresql-11-postgis-2.5 postgresql-11-postgis-2.5-scripts postgis

#
# Set environment variables
#
ENV PGCONF /etc/postgresql/11/main/postgresql.conf
ENV PGDATA /var/lib/postgresql/11/main

#
# Use user postgres to run the next commands
#
USER postgres

#
# Adjust PostgreSQL configuration so that remote connections to the
# database are possible.
#
RUN echo "host all  all    0.0.0.0/0  md5" >> \
    /etc/postgresql/11/main/pg_hba.conf
RUN echo "listen_addresses='*'" >> $PGCONF

#
# Keep log on stderr to be able to use docker logs
#
RUN sed -i '/logging_collector/s/on/off/' $PGCONF

#
# Expose the PostgreSQL port
#
EXPOSE 5432

#
# Add context as working directory
#
ADD . /opt/lada_sql/
WORKDIR /opt/lada_sql/

#
# Create database
#
# Don't mind scary messages like
# 'FATAL: the database system is starting up'.
# It's because of the -w
#
RUN /usr/lib/postgresql/11/bin/pg_ctl start -wo "--config_file=$PGCONF" && \
    /opt/lada_sql/setup-db.sh && \
    /usr/lib/postgresql/11/bin/pg_ctl stop

#
# Set the default command to run when starting the container
#
CMD ["/usr/lib/postgresql/11/bin/postgres", \
     "--config_file=/etc/postgresql/11/main/postgresql.conf"]
