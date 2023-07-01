#!/bin/bash
login="ivarhol-21"
remoteFolder="/tmp/$login/"
slaveFolder="slave"
masterFolder="master"
slaveFile="src.SlaveManager"
masterFile="src.MasterManager"
IPFile="IPs.txt"
localIP=$(hostname -I  | awk '{print $1}')

# CREATING AN ARRAY OF FILES NAMES
if [ -z "$3" ]; then
  prefix="/cal/commoncrawl/CC-MAIN-20230320083513-20230320113513-"
  data=(""$prefix"00000.warc.wet"
  ""$prefix"00001.warc.wet"
  ""$prefix"00002.warc.wet"
  ""$prefix"00003.warc.wet"
  ""$prefix"00004.warc.wet"
  ""$prefix"00005.warc.wet"
  ""$prefix"00006.warc.wet"
  ""$prefix"00007.warc.wet")
else 
  prefix="$3"
  length="$4"
  data=()
  for ((i=0; i<$length; i++)) 
  do
    data+=(""$prefix""$i"")
  done
fi
echo "data : ${data[@]}"

# SETTING THE NUMBER OF SLAVES TO $1
allSlaves=("tp-3a107-01" "tp-3a107-03" "tp-3a107-04" "tp-3a107-10" "tp-3a107-13" "tp-3a107-16")
numberOfSlaves=${#allSlaves[@]}
if [ -z "$1" ]; then
  slaves=${allSlaves[@]}
  slavesExceptLast=${allSlaves[@]::$((numberOfSlaves - 1))}
  lastSlave=${allSlaves[@]:$((numberOfSlaves - 1)):1}
else 
  numberOfSlaves="$1"
  slaves=${allSlaves[@]::$((numberOfSlaves))}
  slavesExceptLast=${allSlaves[@]::$((numberOfSlaves - 1))}
  lastSlave=${allSlaves[@]:$((numberOfSlaves - 1)):1}
fi
master="tp-3a107-02"

# SETTING THE NUMBER OF FILES / SLAVE TO $2
numberOfFilesPerSlave=0
if [ -z "$2" ]; then
  numberOfFilesPerSlave=$((numberOfFilesPerSlave+1))
else 
  numberOfFilesPerSlave="$2"
fi
echo "each slave is reading $numberOfFilesPerSlave files"

if ($length < $((numberOfSlaves * numberOfFilesPerSlave))); then
  echo "not enough files"
  exit 1
fi

#PRINTING THE MACHINES
echo "Running with $numberOfSlaves slaves: ${slaves[@]}"
echo "all slaves except the last : ${slavesExceptLast[@]}"
echo "last slave : $lastSlave"
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

echo "ssh $login@$master cd $remoteFolder$masterFolder; ant -S; java -cp target $masterFile $numberOfSlaves"
ssh "$login@$master" cd "$remoteFolder$masterFolder; ant -S; java -cp target $masterFile $numberOfSlaves" &

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
for c in ${slavesExceptLast[@]}; do
  command4=("ssh" "$login@$c" "cd $remoteFolder$slaveFolder$i;java -cp target $slaveFile $numberOfSlaves $i ${data[@]: $(((i - 1) * numberOfFilesPerSlave)) : $((numberOfFilesPerSlave))}")
  echo ${command4[*]}
  "${command4[@]}" &
  i=$((i+1))
done

command4=("ssh" "$login@$lastSlave" "cd $remoteFolder$slaveFolder$i;java -cp target $slaveFile $numberOfSlaves $i ${data[@]: $(((i - 1) * numberOfFilesPerSlave)) : $((numberOfFilesPerSlave))}")
echo ${command4[*]}
"${command4[@]}"