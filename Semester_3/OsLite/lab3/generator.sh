#!/bin/bash
echo $$ > .pid
while true; do
	read LINE
	echo "$LINE" > pipe
done
