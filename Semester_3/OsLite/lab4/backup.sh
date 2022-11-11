#!/bin/bash
MODE="false"
LATEST=""
for i in {0..6}; do
	BNAME=$(date --date="$i days ago" "+%Y-%m-%d")
	BNAME="Backup-$BNAME"
	if [[ -d /home/user/$BNAME ]]; then
		MODE="true"
		LATEST=/home/user/$BNAME
	fi
done

FILES=$(find /home/user/source | tail -n +2)

CHL=""
NL=""

if [[ "$MODE" == "true" ]]; then
	for df in $FILES; do
		DIRNAME=$(echo "$df" | sed -r 's~^/home/user/source~~')
		if [[ $(file "$df" | awk '{print $2}') == "directory" ]]; then
			if [[ ! -d $LATEST$DIRNAME ]]; then
				mkdir $LATEST$DIRNAME
			fi
		else
			if [[ ! -f $LATEST$DIRNAME ]]; then
				NL="$NL$LATEST$DIRNAME\n"
				cp $df $LATEST$DIRNAME
			elif [[ $(wc -c $df | awk '{print $1}') != $(wc -c $LATEST$DIRNAME | awk '{print $1}') ]]; then
				NEWNAME=$LATEST$DIRNAME.$(date "+%Y-%m-%d")
				mv $LATEST$DIRNAME $NEWNAME
				cp $df $LATEST$DIRNAME
				CHL="$CHL$LATEST$DIRNAME $NEWNAME\n"
			fi
		fi
	done
	echo "==========================================================================" >> /home/user/backup-report
	echo "Backup has been updated!" >> /home/user/backup-report
	echo "Name: $LATEST" >> /home/user/backup-report
	echo "Added files:" >> /home/user/backup-report
	echo -e "$NL" >> /home/user/backup-report
	echo "Changed files:" >> /home/user/backup-report
	echo -e "$CHL" >> /home/user/backup-report
else
	echo "==========================================================================" >> /home/user/backup-report
	echo "New backup has been created!" >> /home/user/backup-report
	DRNAME=Backup-$(date "+%Y-%m-%d")
	echo "Name: /home/user/$DRNAME" >> /home/user/backup-report
	echo "Date: $(date "+%Y-%m-%d")" >> /home/user/backup-report
	echo "Files list:" >> /home/user/backup-report
	mkdir /home/user/$DRNAME
	for df in $FILES; do
		if [[ $(file "$df" | awk '{print $2}') == "directory" ]]; then
			mkdir /home/user/$DRNAME$(echo "$df" | sed -r 's~^/home/user/source~~')
		else
			cp $df /home/user/$DRNAME$(echo "$df" | sed -r 's~^/home/user/source~~')
			echo "$df" >> /home/user/backup-report
		fi
	done
fi
