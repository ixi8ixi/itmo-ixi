#!/bin/bash
if [[ ! -d /home/user/restore ]]; then
	mkdir /home/user/restore
fi

BUPS=$(grep '^Name:' /home/user/backup-report | uniq | awk '{print $2}' | tac)
for df in $BUPS; do
	if [[ -d $df ]]; then
		FL=$(find $df | tail -n +2)
		for file in $FL; do
			DIRNAME=$(echo "$file" | sed 's~/home/user/[^/]*~~')
			if [[ $(file "$file" | awk '{print $2}') == "directory" ]]; then
				mkdir -p /home/user/restore$DIRNAME
			else
				if [[ ! "$file" =~ /.*\.[0-9]{4}-[0-9]{2}-[0-9]{2} ]]; then
					cp $file /home/user/restore$DIRNAME
				fi
			fi
		done
		exit
	fi
done
echo "Nothing to restore :("
