#! /bin/sh
rm -f rest.json
touch rest.json
curl -s 'http://localhost:8080/wm/core/topology/switches/all/json' | python -m json.tool >> rest.json
curl -s 'http://localhost:8080/wm/core/topology/links/json' | python -m json.tool >> rest.json
curl -s 'http://localhost:8080/wm/registry/controllers/json' | python -m json.tool >> rest.json
curl -s 'http://localhost:8080/wm/registry/switches/json' | python -m json.tool >> rest.json
