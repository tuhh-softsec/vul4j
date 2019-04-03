#
# Dockerfile for jboss wildfly application server cutomized for usage in the
# BfS-Lada project
#
# Build with e.g. `docker build --force-rm=true -t koala/lada_wildfly .'
# Run with e.g.
# `docker run --name lada_wildfly --link lada_db:lada_db
#            -dp 8181:8080 -p 1818:9990 koala/lada_wildfly'
# The linked container may be created from db_schema/Dockerfile.
#
# The LADA-server will be available under
# http://yourdockerhost:8181/lada-server
#

FROM debian:stretch
MAINTAINER raimund.renkert@intevation.de

#
# install packages
#
RUN apt-get update -y && \
    apt-get install -y --no-install-recommends \
            curl openjdk-8-jdk libpostgis-java libjts-java \
            git maven lighttpd

#
# Set up Wildfly
#
RUN mkdir /opt/jboss

RUN curl \
    https://download.jboss.org/wildfly/8.2.1.Final/wildfly-8.2.1.Final.tar.gz \
    | tar zx && mv wildfly-8.2.1.Final /opt/jboss/wildfly

ENV JBOSS_HOME /opt/jboss/wildfly

RUN $JBOSS_HOME/bin/add-user.sh admin secret --silent

EXPOSE 8080 9990 80

#
# Wildfly setup specific for LADA
#
RUN mkdir -p $JBOSS_HOME/modules/org/postgres/main

RUN curl https://jdbc.postgresql.org/download/postgresql-9.4-1200.jdbc4.jar >\
         $JBOSS_HOME/modules/org/postgres/main/postgresql.jar

RUN ln -s /usr/share/java/postgis-jdbc-2.2.1.jar \
       $JBOSS_HOME/modules/org/postgres/main/
RUN ln -s /usr/share/java/jts-1.14.jar \
       $JBOSS_HOME/modules/system/layers/base/org/hibernate/main/

RUN curl \
    http://www.hibernatespatial.org/repository/org/hibernate/hibernate-spatial/4.3/hibernate-spatial-4.3.jar > \
    $JBOSS_HOME/modules/system/layers/base/org/hibernate/main/hibernate-spatial-4.3.jar

#
# Add LADA-server repo
#
ADD . /usr/src/lada-server
WORKDIR /usr/src/lada-server

RUN ln -s $PWD/wildfly/postgres-module.xml \
       $JBOSS_HOME/modules/org/postgres/main/module.xml
RUN ln -fs $PWD/wildfly/hibernate-module.xml \
       $JBOSS_HOME/modules/system/layers/base/org/hibernate/main/module.xml
# The jdbcadapters need to know the postgres module to cope with PGeometry
RUN sed -i '/<\/dependencies>/i         <module name="org.postgres"/>' \
    $JBOSS_HOME/modules/system/layers/base/org/jboss/ironjacamar/jdbcadapters/main/module.xml
RUN ln -fs $PWD/wildfly/standalone.conf $JBOSS_HOME/bin/

RUN wildfly/execute.sh

RUN rm $JBOSS_HOME/standalone/configuration/standalone_xml_history/current/*

#
# Build and deploy LADA-server
#
ENV LADA_VERSION 3.3.8
RUN mvn clean compile package && \
    mv target/lada-server-$LADA_VERSION.war \
       $JBOSS_HOME/standalone/deployments/lada-server.war && \
    touch $JBOSS_HOME/standalone/deployments/lada-server.war.dodeploy

##configure lighttpd for apidoc
RUN mvn javadoc:javadoc
RUN sed -i 's|server.document-root        = "/var/www/html"|server.document-root        = "/usr/src/lada-server/target/site/apidocs"|' /etc/lighttpd/lighttpd.conf

## Start the webserver manually, when the container is started
# service lighttpd start

#
# This will boot WildFly in the standalone mode and bind to all interface
#
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", \
     "-bmanagement=0.0.0.0"]

