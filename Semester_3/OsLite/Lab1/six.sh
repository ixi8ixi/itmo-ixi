#!/bin/bash
touch "full.log"
awk '/\(WW\)/ {print $0}' "/var/log/anaconda/X.log" | sed 's/(WW)/Warning:/' > "full.log"
awk '/\(II\)/ {print $0}' "/var/log/anaconda/X.log" | sed 's/(II)/Information:/' >> "full.log"
awk '{print $0}' "full.log"
