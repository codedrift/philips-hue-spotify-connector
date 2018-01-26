#!bin/bash

curl -X POST --data '{"devicetype":"my awesome hue app"}' "http://$1/api"