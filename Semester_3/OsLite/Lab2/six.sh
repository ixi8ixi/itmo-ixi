#!/bin/bash
PROC=`ps -e | awk '{print $1}' | tail -n +2 | tr '\n' ' '`
MNP="0 0"
for PID in $PROC; do
    if [ -d "/proc/$PID" ]; then
        STATM=`grep 'VmRSS' "/proc/$PID/status" | grep -Eo '[0-9]+'`
        MNP="$MNP $PID $STATM"
        MNP=`echo "$MNP" | awk '{ if ( $2 < $4 ) { print $3,$4; } else { print $1,$2 } }'`
    fi
done
echo "$MNP" | awk '{ print $1 }'
