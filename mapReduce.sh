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

#CREATING A FILE CONTAINING ALL THE IPS
echo "rm $slaveFolder/$IPFile"
rm $slaveFolder/$IPFile
echo "rm $masterFolder/$IPFile"
rm $masterFolder/$IPFile
echo "IP=$(ssh "$login@$master" hostname -I  | awk '{print $1}')"
IP=$(ssh "$login@$master" hostname -I  | awk '{print $1}')
echo "$IP" >> $slaveFolder/$IPFile
echo "$IP" >> $masterFolder/$IPFile
for c in ${slaves[@]}; do
  echo "IP=$(ssh "$login@$c" hostname -I  | awk '{print $1}')"
  IP=$(ssh "$login@$c" hostname -I  | awk '{print $1}')
  echo "$IP" >> $slaveFolder/$IPFile
  echo "$IP" >> $masterFolder/$IPFile
done


#LAUCHING THE MASTER
echo "ssh $login@$master rm -rf $remoteFolder; mkdir $remoteFolder"
ssh "$login@$master" rm -rf "$remoteFolder; mkdir $remoteFolder"
echo "scp -r $masterFolder $login@$master:$remoteFolder"
scp -r "$masterFolder" "$login@$master:$remoteFolder"

echo "ssh $login@$master cd $remoteFolder$masterFolder; ant -S; java -cp target $execFile $numberOfSlaves $@"
ssh "$login@$master" cd "$remoteFolder$masterFolder; ant -S; java -cp target $execFile $numberOfSlaves $@" &

sleep 10

#SENDING THE PROGRAM AND DATA TO EACH COMPUTER AND COMPILING
i=1
for c in ${slaves[@]}; do
  command1=("ssh" "$login@$c" "rm -rf $remoteFolder;mkdir $remoteFolder")
  command2=("scp" "-r" "$slaveFolder" "$login@$c:$remoteFolder")
  command2b=("ssh" "$login@$c" "cp -r $remoteFolder$slaveFolder $remoteFolder$slaveFolder$i")
  command3=("ssh" "$login@$c" "cd $remoteFolder$slaveFolder$i;ant -S")
  echo ${command1[*]}
  "${command1[@]}"
  echo ${command2[*]}
  "${command2[@]}"
  echo ${command2b[*]}
  "${command2b[@]}"
  echo ${command3[*]}
  "${command3[@]}"
  i=$((i+1))
done

sleep 10

#RUNNING THE CODE
i=1
for c in ${slaves[@]}; do
  command4=("ssh" "$login@$c" "cd $remoteFolder$slaveFolder$i;java -cp target $execFile $numberOfSlaves $i ${data[$((i-1))]} $@")
  echo ${command4[*]}
  "${command4[@]}" &
  i=$((i+1))
done