#!/bin/bash
grep -r '.*' --include=\*.log /var/log | wc -l
