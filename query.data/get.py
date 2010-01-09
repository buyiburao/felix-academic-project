#!/usr/bin/env python
import os
for i in range(1, 315):
    file = str(i)
    count = 0
    get_count = 0
    for line in open(file):
        count += 1
        if count % 5 == 0:
            if len(line.strip()) > 0:
                get_count += 1
    print i, 'th:', get_count

