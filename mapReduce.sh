#!/bin/bash
# A simple variable example

pkill java

login="ivarhol-21"
remoteFolder="/tmp/$login/"
slaveFileLocation="slave/"
masterFileLocation="master/"
build="build.xml"
IPfile="IPs.txt"
slaveFileName="Listener"
masterFileName="MasterServer"
fileExtension=".java"



slaves=("tp-3a101-07")
master="tp-3a101-08"

#PRINTING THE MACHINES
echo "Running with ${#slaves[@]} slaves: ${slaves[@]}"
echo "Master is ${master[@]}"

#CREATING A FILE CONTAINING ALL THE IPS
rm "$slaveFileLocation$IPfile"
rm "$masterFileLocation$IPfile"
for c in ${slaves[@]}; do
  IP=$(ssh "$login@$c" hostname -I  | awk '{print $1}')
  echo "$IP" >> "$slaveFileLocation$IPfile"
  echo "$IP" >> "$masterFileLocation$IPfile"
done
IP=$(ssh "$login@$master" hostname -I  | awk '{print $1}')
echo "$IP" >> "$slaveFileLocation$IPfile"
echo "$IP" >> "$masterFileLocation$IPfile"

#LAUCHING THE MASTER
ssh "$login@$master" rm -rf "$remoteFolder"
ssh "$login@$master" mkdir "$remoteFolder"
scp -r "$masterFileLocation" "$login@$master:$remoteFolder"
ssh "$login@$master" cd "$remotefolder$masterFileLocation";
ssh "$login@$master" ant
ssh "$login@$master" java -cp target src.MasterServer

sleep 5

i=0
for c in ${slaves[@]}; do
  #SENDING THE PROGRAM AND DATA TO EACH COMPUTER
  command1=("ssh" "$login@$c" "rm -rf $remoteFolder;mkdir $remoteFolder")
  command2=("scp" "-r" "$slaveFileLocation" "$login@$c:$remoteFolder")
  #COMPILING AND RUNNING THE CODE
  command3=("ssh" "$login@$c" "cd $remoteFolder$slaveFileLocation;ant;java -cp target src.$fileName ${#slaves[@]} $i $@")

  echo ${command1[*]}
  "${command1[@]}"
  echo ${command2[*]}
  "${command2[@]}"
  echo ${command3[*]}
  "${command3[@]}" &
  i=$((i+1))
done