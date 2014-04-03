#!/bin/bash

#TUNNEL=("0 1 VIP_ONOS10" "1 2 VIP_ONOS10") 
__TUNNEL__
NW_ID=0__NWID__

start () {
  ## Modify ##
  ulimit -c
  for (( i = 0; i< ${#TUNNEL[@]}; i ++)); do
    t=`echo ${TUNNEL[$i]}`
    ifnr=`echo $t | awk '{print $1}'`
    tun_tag=`echo $t | awk '{print $2}'`
    tun_end_=`echo $t | awk '{print $3}'`
    tun_end=`eval echo '$'$tun_end_`
    ifconfig tap${NW_ID}_${ifnr}
    echo "ifconfig tap${NW_ID}_${ifnr}"
    if [ $? -ne 0 ]; then
      echo "creating tap${NW_ID}_${ifnr}"
      sudo tunctl -t tap${NW_ID}_${ifnr} 
    fi
    echo "./capsulator -v -d -t eth0 -f ${tun_end} -vb tap${NW_ID}_${ifnr}#${tun_tag}"
    sudo ifconfig tap${NW_ID}_${ifnr} 0.0.0.0 up > /dev/null 2>&1
    sudo ./capsulator -v -d -t eth0 -f ${tun_end} -vb tap${NW_ID}_${ifnr}#${tun_tag}  > /dev/null 2>&1 &
  done
}

stop () {
  sudo pkill capsulator
}

case "$1" in
  start | restart)
    stop
    start
    ;;
  stop)
    stop
    ;;
  status)
    nr=`pgrep capsulator | wc -l`
    if [ $nr -gt 0 ]; then
      echo "$nr tunnel(s) is running"
    else
      echo "tunnel is not running"
    fi
    ;;
  *)
    echo "Usage: $0 {start|stop|restart|status}" >&2
    exit 1
    ;;
esac
