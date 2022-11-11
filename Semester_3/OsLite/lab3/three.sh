#!/bin/bash
# You can pass the pid of the processes as an arguments
# For starting process in the background you can use the following comand:
# `bash tloop.sh &`

CNI=`top -b -n 1 | awk -v pd="$1" 'pd == $1 {print $4}'`
while true; do
	echo "CURRENT NICE: $CNI"
	CP=`top -b -n 1 | awk -v pd="$1" 'pd == $1 {print $9}'`
	echo "CURRENT %CPU: $CP"
	if [[ `echo "$CP" | awk '{print ($1 > 10.0)}'` -eq 1 ]]
	then
		let "CNI = $CNI + 1"
		renice -n "$CNI" -p "$1"
	elif [[ `echo "$CP" | awk '{print ($1 < 5.0)}'` -eq 1 ]]
	then
		let "CNI = $CNI - 1"
		renice -n "$CNI" -p "$1"
	fi
	sleep 5
done
