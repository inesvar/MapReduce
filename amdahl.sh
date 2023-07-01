#!/bin/bash
login="ivarhol-21"
commoncrawl="/cal/commoncrawl/CC-MAIN-20230320083513-20230320113513-"
fileToSplit=""$commoncrawl"00000.warc.wet"

ssh "$login"@tp-3a107-02 rm -rf splits
ssh "$login"@tp-3a107-02 split -dn 2 $fileToSplit split_half_
ssh "$login"@tp-3a107-02 split -dn 3 $fileToSplit split_third_
ssh "$login"@tp-3a107-02 split -dn 4 $fileToSplit split_fourth_
ssh "$login"@tp-3a107-02 split -dn 5 $fileToSplit split_fifth_
ssh "$login"@tp-3a107-02 split -dn 6 $fileToSplit split_sixth_

sleep 20
echo ""
echo ""
echo "running with 1 slave"
echo ""
echo ""
./mapReduce.sh 1 1

sleep 20
echo ""
echo ""
echo "running with 2 slaves"
echo ""
echo ""
./mapReduce.sh 2 1 "~/split_half_0" 2

sleep 20
echo ""
echo ""
echo "running with 3 slaves"
./mapReduce.sh 3 1 "~/split_third_0" 3
echo ""
echo ""

sleep 20
echo ""
echo ""
echo "running with 4 slaves"
./mapReduce.sh 4 1 "~/split_fourth_0" 4
echo ""
echo ""

sleep 20
echo ""
echo ""
echo "running with 5 slaves"
./mapReduce.sh 5 1 "~/split_fifth_0" 5
echo ""
echo ""

sleep 20
echo ""
echo ""
echo "running with 6 slaves"
./mapReduce.sh 6 1 "~/split_sixth_0" 6
echo ""
echo ""

ssh "$login"@tp-3a107-02 rm -rf splits









