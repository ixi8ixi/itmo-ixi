#!/bin/bash
ps -e --sort start | tail -1 | awk '{ print $1 }'
