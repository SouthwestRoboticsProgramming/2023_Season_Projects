#!/bin/bash
# To use, copy all files in this directory into the home folder
# of the Raspberry Pi, then run this script as the pi user to
# set everything up

jreFileName="OpenJDK11U-jre_aarch64_linux_hotspot_11.0.16_8.tar.gz"
jreDirName="jdk-11.0.16+8-jre"

echo "Extracting Java Runtime Environment"
tar -xf $jreFile
mv $jreDirName jre

echo "Setting script execute permissions"
chmod +x Messenger/run.sh
chmod +x TaskManager/run.sh

echo "Creating TaskManager tasks directory"
mkdir TaskManager/tasks

echo "Installing systemd services"
sudo ln -s Messenger/messenger.service /etc/systemd/system

sudo systemctl enable messenger
sudo systemctl start messenger

echo "Setup complete!"
