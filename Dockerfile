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
# Set ENV for pacakge versions
ENV WILDFLY_VERSION 16.0.0.Final
# see wildfly pom.xml for hibernate_spatial_version
ENV HIBERNATE_SPATIAL_VERSION 5.3.9.Final
ENV GEOLATTE_GEOM_VERSION 1.4.0
ENV LADA_VERSION 3.3.9-SNAPSHOT

RUN echo "Building Image using WILDFLY_VERSION=${WILDFLY_VERSION}, HIBERNATE_SPATIAL_VERSION=${HIBERNATE_SPATIAL_VERSION}, GEOLATTE_GEOM_VERSION=${GEOLATTE_GEOM_VERSION}, LADA_VERSION=${LADA_VERSION}."

#
# Set up Wildfly
#
RUN mkdir /opt/jboss

RUN curl \
    https://download.jboss.org/wildfly/${WILDFLY_VERSION}/wildfly-${WILDFLY_VERSION}.tar.gz\
    | tar zx && mv wildfly-${WILDFLY_VERSION} /opt/jboss/wildfly

ENV JBOSS_HOME /opt/jboss/wildfly

RUN $JBOSS_HOME/bin/add-user.sh admin secret --silent

EXPOSE 8080 9990 80

#
# Wildfly setup specific for LADA
#
RUN mkdir -p $JBOSS_HOME/modules/org/postgres/main

RUN curl http://central.maven.org/maven2/org/hibernate/hibernate-spatial/${HIBERNATE_SPATIAL_VERSION}/hibernate-spatial-${HIBERNATE_SPATIAL_VERSION}.jar >\
        $JBOSS_HOME/modules/system/layers/base/org/hibernate/main/hibernate-spatial.jar

RUN curl http://central.maven.org/maven2/org/geolatte/geolatte-geom/${GEOLATTE_GEOM_VERSION}/geolatte-geom-${GEOLATTE_GEOM_VERSION}.jar >\
        $JBOSS_HOME/modules/system/layers/base/org/hibernate/main/geolatte-geom.jar

RUN ln -s /usr/share/java/postgresql.jar \
       $JBOSS_HOME/modules/org/postgres/main/
RUN ln -s /usr/share/java/postgis-jdbc.jar \
       $JBOSS_HOME/modules/org/postgres/main/
RUN ln -s /usr/share/java/jts.jar \
       $JBOSS_HOME/modules/system/layers/base/org/hibernate/main/
RUN ln -s $JBOSS_HOME/modules/system/layers/base/org/hibernate/main/hibernate-core-*.jar $JBOSS_HOME/modules/system/layers/base/org/hibernate/main/hibernate-core.jar
RUN ln -s $JBOSS_HOME/modules/system/layers/base/org/hibernate/main/hibernate-envers-*.jar $JBOSS_HOME/modules/system/layers/base/org/hibernate/main/hibernate-envers.jar

RUN ls -hal $JBOSS_HOME/modules/system/layers/base/org/hibernate/main/


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

