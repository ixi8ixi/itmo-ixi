#!/bin/bash
touch out
ps -e | awk '! system("test -f /sbin/\""$4"\"") {print $1}' > out
