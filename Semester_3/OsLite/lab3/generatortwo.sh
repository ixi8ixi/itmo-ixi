#!/bin/bash
while true; do
	read LINE
	case $LINE in
		\+)
			kill -USR1 $(cat .pid)
			;;
		\*)
			kill -USR2 $(cat .pid)
			;;
		"INC")
			kill -ALRM $(cat .pid)
			;;
		"DEC")
			kill -TTOU $(cat .pid)
			;;
		"TERM")
			kill -SIGTERM $(cat .pid)
			exit
			;;
		*)
			:
			;;
	esac
done
