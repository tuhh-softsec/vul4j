########
Cluster Management Tools
#######

ssh : contains necessary files in .ssh (don't change file permission)
bash_profile: Can be used as $HOME/.bash_profile file. The following four lines needs to be changed as necessary

export RCP_USER=ubuntu
export RCMD_CMD=ssh
export RCMD_CMD_ARGS="-i $HOME/.ssh/onlabkey.pem"
export RCMD_USER=ubuntu
export RCP_CMD="scp -i $HOME/.ssh/onlabkey.pem -o StrictHostKeyChecking=no"
export FANOUT=64
export CLUSTER="$HOME/bin/cluster.txt"

### Set the proper value ##
export ONOS_CLUSTER_BASENAME="onosdevx"
export ONOS_CLUSTER_NR_NODES=8

bin/start.sh : shutdown all service and restart
bin/stop.sh : shutdown all service
bin/status.sh : show status of the services 
