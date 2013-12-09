#! /bin/bash
. ${HOME}/bin/func.sh

dsh -g ${basename} 'for i in `seq 1 25`; do ssh-keyscan 1.1.$i.1 >> ${HOME}/.ssh/known_hosts; done'
