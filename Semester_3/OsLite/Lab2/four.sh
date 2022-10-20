#!/bin/bash
touch out
> out
PROC=`ps -e | awk '{print $1}' | tail -n +2 | tr '\n' ' '`
for PID in $PROC; do
    if [ -d "/proc/$PID" ]; then
    PPD=`grep 'PPid:' "/proc/$PID/status" | grep -Eo '[0-9]+$'`
    ET=`grep 'se.sum_exec_runtime' "/proc/$PID/sched" | grep -Eo '[0-9.]+$'`
    STCH=`grep 'nr_switches' "/proc/$PID/sched" | grep -Eo '[0-9.]+$'`
    ADT=`echo "$ET $STCH" | awk '{print $1 / $2}'`
    echo "ProcessID=$PID : Parent_ProcessID=$PPD : Average_Running_Time=$ADT" >> out
    fi
done
sort -n -t "=" -k3 -o out out
