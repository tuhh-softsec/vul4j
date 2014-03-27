#!/bin/sh

SILENT=""

while getopts “hy” OPTION
do
  case OPTION in
    y)
      SILENT="-y"
      ;;
    *)
      echo
      echo "This script will install Oracle JDK 7 and set JAVA_HOME"
      echo "Usage: $ $0"
      echo
      exit 1
      ;;
  esac
done

if [ "$SILENT" = "-y" ]; then
 sudo apt-get install -y software-properties-common python-software-properties
fi

# print what's going on
set -x

sudo add-apt-repository ${SILENT} ppa:webupd8team/java
if [ $? -ne 0 ]; then
  { set +x; } 2>/dev/null
  echo
  echo "Registering Oracle Java repository failed."
  echo "If the error was about add-apt-repository command not found,"
  echo "try one of the following and retry running this script"
  echo
  echo "$ sudo apt-get install software-properties-common"
  echo "$ sudo apt-get install software-properties-common python-software-properties"
  echo
  exit 1
fi

# fail on error
set -e

sudo apt-get update
sudo apt-get install ${SILENT} oracle-java7-set-default

{ set +x; } 2>/dev/null
echo
echo "Done. You may need to relogin for the environment variable change to take effect."


