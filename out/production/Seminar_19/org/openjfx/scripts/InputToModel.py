from MarkovModel import Model
import sys
from unidecode import unidecode

states = open("src/org/openjfx/resources/modelfiles/parsed_states.txt").read().replace("[", "").replace("]", "")
if states[0] == "\'" and states[-1] == "\'":
    states = "\"" + states[1:-1] + "\""
therapies = open("src/org/openjfx/resources/modelfiles/parsed_therapies.txt").read().replace("[", "").replace("]", "")
if therapies[0] == "\'" and therapies[-1] == "\'":
    therapies = "\"" + therapies[1:-1] + "\""
costs = open("src/org/openjfx/resources/modelfiles/parsed_costs.txt").read()
eff = open("src/org/openjfx/resources/modelfiles/parsed_eff.txt").read()
if len(sys.argv[7]) != 0:
    conditions = unidecode(sys.argv[7])
    model = Model(states, costs, int(sys.argv[1]), 12, therapies, eff, sys.argv[2], int(sys.argv[3]), [conditions])
else:
    model = Model(states, costs, int(sys.argv[1]), 12, therapies, eff, sys.argv[2], int(sys.argv[3]))
model.forecast(int(sys.argv[4]), int(sys.argv[5]), sys.argv[6])
