#!/bin/bash
# A simple variable example
login="ivarhol-21"
remoteFolder="/tmp/$login/"
slaveFolder="slave"
masterFolder="master"
execFile="src.Manager"
IPFile="IPs.txt"
localIP=$(hostname -I  | awk '{print $1}')
commoncrawl="/cal/commoncrawl/CC-MAIN-20230320083513-20230320113513-"
data=(""$commoncrawl"00000.warc.wet"
""$commoncrawl"00001.warc.wet"
""$commoncrawl"00002.warc.wet")


# RUNNING THE MAP REDUCE REMOTELY
slaves=("tp-1a252-04")
numberOfSlaves=${#slaves[@]}
echo "Running with $numberOfSlaves slaves"
master="tp-1a252-02"

#PRINTING THE MACHINES
echo "Running with $numberOfSlaves slaves: ${slaves[@]}"
echo "Master is ${master[@]}"

#LAUCHING THE MASTER
echo "ssh $login@$master cd $remoteFolder$masterFolder; ant -S; java -cp target $execFile $numberOfSlaves $@"
ssh "$login@$master" cd "$remoteFolder$masterFolder; ant -S; java -cp target $execFile $numberOfSlaves $@" &

sleep 2

#LAUNCHING THE SLAVES
i=1
for c in ${slaves[@]}; do
  command4=("ssh" "$login@$c" "cd $remoteFolder$slaveFolder$i;java -cp target $execFile $numberOfSlaves $i ${data[$((i-1))]} $@")
  echo ${command4[*]}
  "${command4[@]}" &
  i=$((i+1))
done