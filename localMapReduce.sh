#!/bin/bash
slaveFolder="slave"
masterFolder="master"
slaveFile="src.Manager"
masterFile="src.Manager"
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
ant
java -cp target "$masterFile" "$numberOfSlaves" "$@" &
cd ..

#LAUCHING THE SLAVES
for i in $(seq 1 $((numberOfSlaves))); do
  cp -r "$slaveFolder" "$slaveFolder$i"
  cd "$slaveFolder$i"
  ant
  echo "java -cp target $slaveFile $numberOfSlaves $i $2"
  if [ -n "$2" ]; then
    java -cp target "$slaveFile" "$numberOfSlaves" "$i" "$2" &
  else
    java -cp target "$slaveFile" "$numberOfSlaves" "$i" &
  fi
  cd ..
done