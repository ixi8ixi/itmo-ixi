#!/bin/bash
touch "emails.lst"
grep -rhIEo "[[:alnum:].+-]+@[[:alnum:]]+\.[[:alnum:]]+" "/etc" | tr '\n' ',' | sed 's/.$//' | sed 's/,/, /g' > "emails.lst"
