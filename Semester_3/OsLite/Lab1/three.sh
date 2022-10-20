#!/bin/bash
echo -e -n "Menu: \n[1] Nano\n[2] Vi\n[3] Links\n[4] Exit\nPlease, enter item number: "
read item
case "$item" in
    1)
        nano
        ;;
    2)
        vi
        ;;
    3)
        links
        ;;
    4)
        echo ":("
        ;;
esac
