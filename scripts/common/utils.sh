#! /bin/bash

# read-conf {filename} {parameter name} [default value]
function read-conf {
  local value=`grep ^${2} ${1} | cut -d "=" -f 2 | sed -e 's/^[ \t]*//'`
  if [ -z "${value}" ]; then
    echo $3
  else
    echo ${value}
  fi
}

# revert-file {filename}
# revert "filename" from "filename.bak" if "filename.tmp" exists.
function revert-file {
  local filename=$1
  local temp="${filename}.tmp"
  local backup="${filename}.bak"
  
  if [ -f "${temp}" ]; then
    echo -n "reverting ${filename} ... "
    mv ${backup} ${filename}
    rm ${temp}
    echo "DONE"
  fi
}

# create-conf-interactive {filename} {function to create conf} [param to function]
function create-conf-interactive {
  local filepath=$1
  local filename=`basename ${filepath}`
  local func=$2
  
  if [ -f ${filepath} ]; then
    # confirmation to overwrite existing config file
    echo -n "Overwriting ${filename} [Y/n]? "
    while [ 1 ]; do
      read key
      if [ -z "${key}" -o "${key}" == "Y" -o "${key}" == "y" ]; then
        ${func} $3
        break
      elif [ "${key}" == "N" -o "${key}" == "n" ]; then
        break
      fi
      echo "[Y/n]?"
    done
  else
    ${func} $3
  fi
}

# begin-conf-creation {config file name}
function begin-conf-creation {
  local conf=${1}
  local backup="${conf}.bak"
  local temp="${conf}.tmp"
  
  if [ -f ${conf} ]; then
    mv ${conf} ${backup}
    local filename=`basename ${backup}`
  fi
  
  if [ -f ${temp} ]; then
    rm ${temp}
  fi
  
  touch ${temp}

  echo ${temp}
}


# end-conf-creation {config file name}
function end-conf-creation {
  local conf=${1}
  local temp="${conf}.tmp"
  
  mv ${temp} ${conf}
}
