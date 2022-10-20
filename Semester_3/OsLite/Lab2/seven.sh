#!/bin/bash
PROC=`ps -e | awk '{ print $1 }' | tail -n +2 | tr '\n' ' '`
TABLE=""
for PID in $PROC; do
    if [ -d "/proc/$PID" ]; then
        MEM=`grep 'read_bytes' "/proc/$PID/io" | grep -Eo '[0-9]+$'`
        TABLE="$TABLE $PID:$MEM"
    fi
done
sleep 60
PROC=`ps -e | awk '{ print $1 }' | tail -n +2 | tr '\n' ' '`
RESTBL=""
for PID in $PROC; do
    if [[ -d "/proc/$PID" && $(echo "$TABLE" | grep "$PID:") ]]; then
        PREV=`echo "$TABLE" | grep -Eo " $PID:[0-9]+" | awk -F ':' '{ print $2 }'`
        MEM=`grep 'read_bytes' "/proc/$PID/io" | grep -Eo '[0-9]+$'`
        CMDLN=`tr -d '\0'<"/proc/$PID/cmdline"`
        let "RESULT = $MEM - $PREV"
        RESTBL="$PID:$CMDLN:$RESULT\n$RESTBL"
    fi
done
echo -e "$RESTBL" | head -n -1 | sort -n -t ":" -k3 | head -3
