#!/usr/bin/env bash

set -e

sudo docker build -t dnd/universe universe
sudo docker build -t dnd/lighttpd lighttpd
sudo docker build -t dnd/cumulus cumulus
