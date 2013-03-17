#! /bin/sh
rm -f rest.json
touch rest.json

urls="'http://localhost:8080/wm/core/topology/switches/all/json' 'http://localhost:8080/wm/core/topology/links/json' 'http://localhost:8080/wm/registry/controllers/json' 'http://localhost:8080/wm/registry/switches/json'"

for url in $urls; do
  echo "---REST CALL---" >> rest.json
  echo "curl -s $url" >> rest.json
  echo "---Result----" >> rest.json
  curl -s $url | python -m json.tool >> rest.json
done
