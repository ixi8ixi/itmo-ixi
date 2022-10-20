 #!/bin/bash
man bash | tr -cs '[:alnum:]' '\n'| tr 'A-Z' 'a-z' | sort | uniq -c | sort -nr | head -3
