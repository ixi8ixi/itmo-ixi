#!/bin/bash
RESULT=""
read INPUT
while [ "$INPUT" != "q" ];
do
    RESULT="$RESULT${INPUT} "
    read INPUT
done
echo "$RESULT"
