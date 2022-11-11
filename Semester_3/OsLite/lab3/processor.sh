#!/bin/bash
echo $$ > .pid
A=1
MODE="rabota"
usr1()
{
	MODE="ostanov"
}
trap 'usr1' USR1
while true; do
	case $MODE in
		"rabota")
			let A=$A+1
			echo $A
			;;
		"ostanov")
			echo "Stopped by SIGUSR1"
			exit
			;;
	esac
	sleep 1
done
