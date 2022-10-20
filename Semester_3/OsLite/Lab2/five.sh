#!/bin/bash
if [ -s out ]; then
    SADT=0
    CT=0
    CPID=`head -1 out | awk -F '[= ]' '{ print $5 }'`
    while read line; do
        YT=`echo "$line" | awk -F '[= ]' '{ print $5 }'`
        if [[ "$CPID" == "$YT" ]]; then
            ADT=`echo "$line" | awk -F '[= ]' '{ print $8 }'`
            SADT=`echo "$ADT $SADT" | awk '{ print $1 + $2 }'`
            let "CT = $CT + 1"
        else
            SADT=`echo "$SADT $CT" | awk '{ print $1 / $2 }'`
            sed -i "/$line/i Average_Running_Children_of_ParentID=$CPID is $SADT" out
            CT=1
            SADT=0
            ADT=`echo "$line" | awk -F '[= ]' '{ print $8 }'`
            SADT=`echo "$ADT $SADT" | awk '{ print $1 + $2 }'`
            CPID="$YT"
        fi
    done < out
    SADT=`echo "$SADT $CT" | awk '{ print $1 / $2 }'`
    echo "Average_Running_Children_of_ParentID=$CPID is $SADT" >> out
fi
