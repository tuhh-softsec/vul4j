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
# http://yourdockerhost:8181/lada-server-2.2.0
#

FROM java:7-jdk
MAINTAINER raimund.renkert@intevation.de

#
# install packages
#
RUN apt-get update -y && \
    apt-get install -y libpostgresql-jdbc-java libpostgis-java libjts-java \
            maven

#
# Set up Wildfly
#
RUN mkdir /opt/jboss

RUN curl \
    https://download.jboss.org/wildfly/8.2.1.Final/wildfly-8.2.1.Final.tar.gz \
    | tar zx && mv wildfly-8.2.1.Final /opt/jboss/wildfly

ENV JBOSS_HOME /opt/jboss/wildfly

RUN $JBOSS_HOME/bin/add-user.sh admin secret --silent

EXPOSE 8080 9990

#
# Add LADA-server repo
#
ADD . /usr/src/lada-server
WORKDIR /usr/src/lada-server

#
# Wildfly setup specific for LADA
#
RUN mkdir -p $JBOSS_HOME/modules/org/postgres/main
RUN ln -s /usr/share/java/postgresql-jdbc4-9.2.jar \
       $JBOSS_HOME/modules/org/postgres/main/
RUN ln -s /usr/share/java/postgis-jdbc-2.1.4.jar \
       $JBOSS_HOME/modules/org/postgres/main/
RUN ln -s /usr/share/java/jts-1.11.jar \
       $JBOSS_HOME/modules/system/layers/base/org/hibernate/main/
RUN curl \
    http://www.hibernatespatial.org/repository/org/hibernate/hibernate-spatial/4.3/hibernate-spatial-4.3.jar > \
    $JBOSS_HOME/modules/system/layers/base/org/hibernate/main/hibernate-spatial-4.3.jar

RUN cp wildfly/postgres-module.xml \
       $JBOSS_HOME/modules/org/postgres/main/module.xml
RUN cp wildfly/hibernate-module.xml \
       $JBOSS_HOME/modules/system/layers/base/org/hibernate/main/module.xml

RUN wildfly/execute.sh

RUN rm $JBOSS_HOME/standalone/configuration/standalone_xml_history/current/*

#
# Build and deploy LADA-server
#
RUN mvn clean compile package
RUN mv target/lada-server-2.2.0.war $JBOSS_HOME/standalone/deployments
RUN touch $JBOSS_HOME/standalone/deployments/lada-server-2.2.0.war.dodeploy

#
# This will boot WildFly in the standalone mode and bind to all interface
#
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", \
     "-bmanagement=0.0.0.0"]
