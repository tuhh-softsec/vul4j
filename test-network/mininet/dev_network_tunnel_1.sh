#!/bin/bash
onosdev1=192.168.56.11
onosdev2=192.168.56.12
onosdev3=192.168.56.13
onosdev4=192.168.56.14

dir=`dirname $0`
TUNNEL=("2 2 onosdev2" "3 3 onosdev3" "4 4 onosdev4" "5 5 onosdev2")
NW_ID=01

start () {
  ## Modify ##
  ulimit -c
  for (( i = 0; i< ${#TUNNEL[@]}; i ++)); do
    t=`echo ${TUNNEL[$i]}`
    ifnr=`echo $t | awk '{print $1}'`
    tun_tag=`echo $t | awk '{print $2}'`
    tun_end_=`echo $t | awk '{print $3}'`
    tun_end=`eval echo '$'$tun_end_`
    echo "ifconfig tap${NW_ID}_${ifnr}"
    ifconfig tap${NW_ID}_${ifnr}
    if [ $? -ne 0 ]; then
      echo "creating tap${NW_ID}_${ifnr}"
      sudo tunctl -t tap${NW_ID}_${ifnr} 
    fi
    echo "$dir/capsulator -v -d -t eth1 -f ${tun_end} -vb tap${NW_ID}_${ifnr}#${tun_tag}"
    sudo ifconfig tap${NW_ID}_${ifnr} 0.0.0.0 up > /dev/null 2>&1
    sudo $dir/capsulator -v -d -t eth1 -f ${tun_end} -vb tap${NW_ID}_${ifnr}#${tun_tag}  > /dev/null 2>&1 &
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
