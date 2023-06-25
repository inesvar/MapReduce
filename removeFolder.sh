#!/bin/bash
# Run "./tp_up" command to get the list of IP addresses
array=($(ssh ivarhol-21@ssh.enst.fr tp_up |  grep -v 1a201))
# SSH into each machine and remove the folder if it exists
for ip_address in "${array[@]}"
do
    echo "Removing folder on $ip_address"
    # connect to ivarhol-21@ip_address
    ssh ivarhol-21@$ip_address rm -rf /tmp/ivarhol-21
done