#!/bin/bash
# This file undoes all system changes done by init.sh

sudo unlink /etc/systemd/system/messenger.service

echo "Successfully removed"
