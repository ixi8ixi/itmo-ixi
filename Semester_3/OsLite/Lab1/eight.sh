#!/bin/bash
sort -n -t ':' -k 3 "/etc/passwd" | awk -F ":" '{print $1, $3}'
