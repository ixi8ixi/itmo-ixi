#!/bin/bash

#check if file exists before start
if [[ ! -f "$1" ]]; then
	echo "File '$1' not found in current directory :("
	exit
fi

if [[ ! -f .tn ]]; then
	touch .tn
	echo "1" > .tn
fi

if [[ ! -d ~/.trash ]]; then
	mkdir ~/.trash
fi

tmp=$(cat .tn)
ln "$1" ~/.trash/$tmp
rm "$1"

if [[ ! -f ~/.trash.log ]]; then
	touch ~/.trash.log
	echo -e "link\t\tpath" > ~/.trash.log
fi

echo -e "$tmp\t\t$(pwd)/$1" >> ~/.trash.log

let "tmp = $tmp + 1"
echo "$tmp" > .tn

