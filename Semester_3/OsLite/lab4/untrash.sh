#!/bin/bash
if [[ ! -f ~/.trash.log ]]; then
	echo "trash.log doesn't exist :("
	exit
fi


FILES=$(cat ~/.trash.log | tail -n +2 | grep "/$1$")

for LINE in "$FILES"; do
	LINK=$(echo "$LINE" | awk '{print $1}')
	FILE=$(echo "$LINE" | awk '{print $2}')
	echo "Restore file $FILE? [Y/n]"
	read ANS
	case "$ANS" in
		[Yy])
			LAST=$(echo "$FILE" | grep -o '/[A-Za-z0-9]*$')
			PTH=$FILE
			PTH=${PTH%"$LAST"}

			if [[ ! -d "$PTH" ]]; then
				echo "Directory $PTH doesn't exist, file will be created in $HOME"
				PTH=$HOME
			fi

			if [[ ! -f "$FILE" ]]; then
				ln ~/.trash/$LINK $FILE
				rm ~/.trash/$LINK
				sed -i "/^$LINK\t/d" ~/.trash.log
			else
				NEW=$LAST

				while [[ -f "$PTH$NEW" ]]; do
					echo "File $NEW already exist in $PTH, please enter new name"
					echo "(Name should be without leading slash)"
					read NEW
					if [[ "$NEW" != "" ]]; then
						NEW="/$NEW"
					else
						echo "Name should be not empty"
						NEW=$LAST
					fi
				done

				ln ~/.trash/$LINK $PTH$NEW
				rm ~/.trash/$LINK
				sed -i "/^$LINK\t/d" ~/.trash.log
			fi
			;;
		*)
			:
			;;
	esac
done
