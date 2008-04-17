#!/bin/sh

# Loading functions
. ./functions.sh

# Reading variables file and asking questions
lines=`wc -l < ./variables.sh`
count=1
lines=`expr ${lines:-0} + 1`
while [ $count -lt $lines ]
do
    ask_param $count
    count=`expr ${count:-0} + 1`
done

#
# Starting installation
#

# Verifying the user is root
#if ( test `id -un` != "root" )
#then
#    echo "Only root can install this software."
#    echo "Apache DS installation has failed."
#    exit 1 ;
#fi

# Installing
echo "Installing..."

# Copying the server files
mkdir -p $APACHEDS_HOME_DIRECTORY
verifyExitCode
cp -r ../root/server/* $APACHEDS_HOME_DIRECTORY
verifyExitCode

# Creating instances home directory
mkdir -p $INSTANCES_HOME_DIRECTORY
verifyExitCode

# Creating the default instance home directory
DEFAULT_INSTANCE_HOME_DIRECTORY=$INSTANCES_HOME_DIRECTORY/$DEFAULT_INSTANCE_NAME
verifyExitCode
mkdir -p $DEFAULT_INSTANCE_HOME_DIRECTORY
verifyExitCode
mkdir -p $DEFAULT_INSTANCE_HOME_DIRECTORY/conf
verifyExitCode
mkdir -p $DEFAULT_INSTANCE_HOME_DIRECTORY/ldif
verifyExitCode
mkdir -p $DEFAULT_INSTANCE_HOME_DIRECTORY/log
verifyExitCode
mkdir -p $DEFAULT_INSTANCE_HOME_DIRECTORY/partitions
verifyExitCode
mkdir -p $DEFAULT_INSTANCE_HOME_DIRECTORY/run
verifyExitCode

# Creating the PID directory
mkdir -p /var/run/apacheds-$APACHEDS_VERSION
verifyExitCode

# Copying the default instance files
cp ../root/instance/apacheds.conf $DEFAULT_INSTANCE_HOME_DIRECTORY/conf/
verifyExitCode
cp ../root/instance/log4j.properties $DEFAULT_INSTANCE_HOME_DIRECTORY/conf/
verifyExitCode
cp ../root/instance/server.xml $DEFAULT_INSTANCE_HOME_DIRECTORY/conf/
verifyExitCode

# Filtering and copying the init.d script
sed -e "s;@APACHEDS.HOME@;${APACHEDS_HOME_DIRECTORY};" ../root/instance/apacheds-init > ../root/instance/apacheds-init.tmp
verifyExitCode
mv ../root/instance/apacheds-init.tmp ../root/instance/apacheds-init
verifyExitCode
sed -e "s;@INSTANCE.HOME@;${INSTANCES_HOME_DIRECTORY};" ../root/instance/apacheds-init > ../root/instance/apacheds-init.tmp
verifyExitCode
mv ../root/instance/apacheds-init.tmp ../root/instance/apacheds-init
verifyExitCode
sed -e "s;@INSTANCE@;${DEFAULT_INSTANCE_NAME};" ../root/instance/apacheds-init > ../root/instance/apacheds-init.tmp
verifyExitCode
mv ../root/instance/apacheds-init.tmp ../root/instance/apacheds-init
verifyExitCode
cp ../root/instance/apacheds-init /etc/init.d/apacheds-$APACHEDS_VERSION-$DEFAULT_INSTANCE_NAME
verifyExitCode

# Setting the correct permissions on executable files
chmod +x /etc/init.d/apacheds-$APACHEDS_VERSION-$DEFAULT_INSTANCE_NAME
verifyExitCode
chmod +x $APACHEDS_HOME_DIRECTORY/bin/apacheds
verifyExitCode

# Creating the apacheds user (only if needed)
USER=`eval "id -u -n apacheds"`
if [ ! "Xapacheds" = "X$USER" ]
then
	/usr/sbin/groupadd apacheds >/dev/null 2>&1 || :
	verifyExitCode
	/usr/sbin/useradd -g apacheds -d $APACHEDS_HOME_DIRECTORY apacheds >/dev/null 2>&1 || :
	verifyExitCode
fi

# Modifying owner
chown -R apacheds:apacheds $APACHEDS_HOME_DIRECTORY
chown -R apacheds:apacheds $INSTANCES_HOME_DIRECTORY
chown apacheds:apacheds /var/run/apacheds-$APACHEDS_VERSION
chown root:root /etc/init.d/apacheds-$APACHEDS_VERSION-$DEFAULT_INSTANCE_NAME
