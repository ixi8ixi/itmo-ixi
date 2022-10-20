#!/bin/bash
res="$1"
if [[ "$res" -lt "$2" ]]; then res="$2"; fi
if [[ "$res" -lt "$3" ]]; then res="$3"; fi
echo "$res"
