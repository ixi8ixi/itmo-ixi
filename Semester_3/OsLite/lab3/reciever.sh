#!/bin/bash
MODE="ADD"
HEAD=1
(tail -f pipe) |
while true; do
	read LINE
	case $LINE in
		+)
			MODE="ADD"
			echo "Mode has been changed to \"ADD\""
			;;
		\*)
			MODE="MUL"
			echo "Mode has been changed to \"MULTIPLY\""
			;;
		QUIT)
			echo "Calculations completed"
			echo "Result is: $HEAD"
			kill $(cat .pid)
			exit
			;;
		*)
			if [[ $LINE =~ ^[0-9]+$ ]]; then
				if [[ $MODE == "ADD" ]]; then
					let "HEAD = $HEAD + $LINE"
				else
					let "HEAD = $HEAD * $LINE"
				fi
			else
				echo "Invalid number format"
				echo "Current result is: $HEAD"
				kill $(cat .pid)
				exit
			fi
			;;
	esac
done
