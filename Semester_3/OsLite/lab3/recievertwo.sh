#!/bin/bash
echo $$ > .pid
HEAD=1
DELTA=1
MODE="ADD"
usr1()
{
	MODE="ADD"
}

usr2()
{
	MODE="MUL"
}

inc()
{
	let "DELTA = $DELTA + 1"
}

dec()
{
	let "DELTA = $DELTA - 1"
}

term()
{
	echo "Shutdown signal recieved"
	echo "Current result: $HEAD"
	exit
}

trap 'usr1' USR1
trap 'usr2' USR2
trap 'term' SIGTERM
trap 'inc' ALRM
trap 'dec' TTOU
while true; do
	case $MODE in
		"ADD")
			let "HEAD = $HEAD + $DELTA"
			;;
		"MUL")
			let "HEAD = $HEAD * $DELTA"
			;;
		*)
			:
			;;
	esac
	echo "Current value: $HEAD"
	sleep 1
done
