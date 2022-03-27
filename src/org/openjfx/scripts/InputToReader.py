#!/usr/bin/python
# -*- coding: utf-8 -*-

import sys
from unidecode import unidecode

from MarkovModel import Model, Reader

states = open("src/org/openjfx/resources/modelfiles/parsed_states.txt").read().replace("[", "").replace("]", "")
if states[0] == "\'" and states[-1] == "\'":
    states = "\"" + states[1:-1] + "\""
therapies = open("src/org/openjfx/resources/modelfiles/parsed_therapies.txt").read().replace("[", "").replace("]", "")
if therapies[0] == "\'" and therapies[-1] == "\'":
    therapies = "\"" + therapies[1:-1] + "\""
costs = open("src/org/openjfx/resources/modelfiles/parsed_costs.txt").read()
eff = open("src/org/openjfx/resources/modelfiles/parsed_eff.txt").read()
condition = unidecode(sys.argv[4])
model = Model(states, costs, int(sys.argv[3]), 12, therapies, eff, sys.argv[1], int(sys.argv[2]))

print(Reader(model).read(condition))
