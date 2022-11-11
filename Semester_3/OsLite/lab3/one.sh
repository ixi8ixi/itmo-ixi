#!/bin/bash
mkdir /home/test && echo "Catalog test was created succesfully" >> ~/report && touch "/home/test/$(date '+%D-%T')"
ping -c 4 www.net_nikogo.ru || echo "$(date '+%D_%T') Unable to reach www.net_nikogo.ru" >> ~/report
