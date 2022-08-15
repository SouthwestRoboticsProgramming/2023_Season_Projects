#!/bin/bash
# This file undoes all system changes done by init.sh

echo "Stopping services"
sudo systemctl stop taskmanager
sudo systemctl stop messenger

echo "Disabling services"
sudo systemctl disable messenger
sudo systemctl disable taskmanager

echo "Successfully removed"
