#!/bin/bash
# This file undoes all system changes done by init.sh

sudo systemctl disable messenger
sudo systemctl disable taskmanager

echo "Successfully removed"
