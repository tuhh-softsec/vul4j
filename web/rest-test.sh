#! /bin/sh
rm -f rest.json
touch rest.json

urls="http://localhost:8080/wm/onos/topology/switches/all/json http://localhost:8080/wm/onos/linkdiscovery/links/json http://localhost:8080/wm/onos/registry/controllers/json http://localhost:8080/wm/onos/registry/switches/json"

for url in $urls; do
  echo "---REST CALL---" >> rest.json
  echo "curl -s $url" >> rest.json
  echo "---Result----" >> rest.json
  curl -s $url | python -m json.tool >> rest.json
done
