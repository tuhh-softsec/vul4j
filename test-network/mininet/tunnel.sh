#!/bin/bash
VIP_ONOS1=10.0.1.217
VIP_ONOS2=10.0.1.176
VIP_ONOS3=10.0.1.173
VIP_ONOS4=10.0.1.222
VIP_ONOS5=10.0.1.191
VIP_ONOS6=10.0.1.158
VIP_ONOS9=10.0.1.29
VIP_ONOS10=10.0.1.28
VIP_ONOS11=10.0.1.62

NW_ID="a"
# [[tap_if_nr, tag, other_end],..]
TUNNEL=("0 1 VIP_ONOS5") 

start () {
  ## Modify ##
  ulimit -c
  for (( i = 0; i< ${#TUNNEL[@]}; i ++)); do
    t=`echo ${TUNNEL[$i]}`
    ifnr=`echo $t | awk '{print $1}'`
    tun_tag=`echo $t | awk '{print $2}'`
    tun_end_=`echo $t | awk '{print $3}'`
    tun_end=`eval echo '$'$tun_end_`
    ifconfig tap${NW_ID}${ifnr}
    echo "ifconfig tap${NW_ID}${ifnr}"
    if [ $? -ne 0 ]; then
      echo "creating tap${NW_ID}${ifnr}"
      sudo tunctl
    fi
    echo "./capsulator -v -d -t eth0 -f ${tun_end} -vb tap${NW_ID}${ifnr}#${tun_tag}"
    sudo ifconfig tap${NW_ID}${ifnr} 0.0.0.0 up > /dev/null 2>&1
    sudo ./capsulator -v -d -t eth0 -f ${tun_end} -vb tap${NW_ID}${ifnr}#${tun_tag}  > /dev/null 2>&1 &
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
