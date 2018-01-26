#!/usr/bin/env bash

docker rm -f spotify-playlist-manager && docker rmi -f spotify-playlist-manager
docker build . -t spotify-playlist-manager
docker run -d --name=spotify-playlist-manager -p 8089:80 spotify-playlist-manager
