#!bin/bash
HUE_BRIDGE_IP=$1

curl -X POST --data '{"devicetype":"my awesome hue app"}' "http://$HUE_BRIDGE_IP/api"