#!/bin/bash
touch out
ps -U root | tail -n +2 | wc -l > out
ps -U root | tail -n +2 | awk '{print $1 ":" $4}' >> out
