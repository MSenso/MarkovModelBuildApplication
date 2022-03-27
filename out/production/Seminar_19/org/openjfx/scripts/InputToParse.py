#!/usr/bin/python
# -*- coding: utf-8 -*-
from Parse import Parse
from unidecode import unidecode

import sys
sys.argv[1:] = [unidecode(x) for x in sys.argv[1:]]

parse = Parse(sys.argv[3], sys.argv[4])
if sys.argv[1] == "states":
    print(parse.readStates(sys.argv[2]))
elif sys.argv[1] == "therapies":
    print(parse.readTherapies(sys.argv[2]))
if sys.argv[1] == "age":
    print(parse.readAge(sys.argv[2]))
elif sys.argv[1] == "cost":
    print(parse.readCost(sys.argv[2]))
elif sys.argv[1] == "eff":
    print(parse.readEff(sys.argv[2]))
elif sys.argv[1] == "matr":
    print(parse.readMatr(sys.argv[2]))
