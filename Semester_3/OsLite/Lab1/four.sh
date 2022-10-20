#!/bin/bash
if [[ "$PWD" == "$HOME" ]]
    then
        echo "$HOME"
        exit 0
    else
        echo "You're not in your home directory, (((((((((((((((("
        exit 1
fi
