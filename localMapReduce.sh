#!/bin/bash
slaveFolder="slave"
masterFolder="master"
slaveFile="src.SlaveManager"
masterFile="src.MasterManager"
IPFile="IPs.txt"


# RUNNING THE MAPREDUCE LOCALLY
numberOfSlaves=1
echo "Running with $numberOfSlaves slaves"

#CLEANING UP AFTER THE LAST EXECUTION
pkill java
rm -rf "$slaveFolder"?*

#CREATING A FILE CONTAINING THE LOCAL IP
rm "$slaveFolder/$IPFile"
rm "$masterFolder/$IPFile"
IP=$(hostname -I  | awk '{print $1}')
for i in $(seq 1 $((numberOfSlaves + 1))); do
  echo "$IP" >> "$slaveFolder/$IPFile"
  echo "$IP" >> "$masterFolder/$IPFile"
done

#LAUCHING THE MASTER
cd "$masterFolder"
ant -S
java -cp target "$masterFile" "$numberOfSlaves" "$@" &
cd ..

#LAUCHING THE SLAVES
for i in $(seq 1 $((numberOfSlaves))); do
  cp -r "$slaveFolder" "$slaveFolder$i"
  cd "$slaveFolder$i"
  ant -S
  echo "java -cp target $slaveFile $numberOfSlaves $i "S0.txt" "S1.txt""
  java -cp target "$slaveFile" "$numberOfSlaves" "$i" "S0.txt" "S1.txt" &
  cd ..
done