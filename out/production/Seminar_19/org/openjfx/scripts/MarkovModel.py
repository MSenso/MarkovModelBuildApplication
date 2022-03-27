#!/usr/bin/python
# -*- coding: utf-8 -*-

from math import fsum

import numpy as np

from Parse import Parse
from unidecode import unidecode


class Model:

    def __init__(self, states, costs, age, cycle_l, therapies, th_effect, trans_matr, sex, conditions=None):
        if conditions is None:
            conditions = []
        self.var_dict = {}
        self.parse = Parse(states, therapies)
        self.conditions = conditions
        self.var_dict["states"] = self.inputStates(states)
        self.var_dict["therapies"] = self.inputTherapies(therapies)
        self.var_dict["costs"] = eval(costs)
        self.var_dict["age"] = self.inputAge(age)
        self.var_dict["cycle_l"] = self.inputCycleLength(cycle_l)
        self.var_dict["th_effect"] = eval(th_effect)
        self.var_dict["trans_matr"] = eval(trans_matr)
        self.var_dict["sex"] = self.inputSex(sex)
        self.applyConditions()

    def writeToFile(self, var_name, new_value):
        if var_name == "costs":
            path = "src/org/openjfx/resources/modelfiles/parsed_costs.txt"
        else:
            path = "src/org/openjfx/resources/modelfiles/parsed_eff.txt"
        file = open(path, 'w')
        file.write(str(new_value))
        file.close()

    def applySetter(self, var_name, new_value):
        self.var_dict[var_name] = new_value

    def applyConditions(self):
        if len(self.conditions) != 0:
            for condition in self.conditions:
                reader = Reader(self)
                reader.read(condition)

    def tryParseToInt(self, x):
        try:
            int(x)
            return True
        except ValueError:
            return False

    def inputStates(self, states):
        states = states.replace("\"", "").split(", ")
        return states

    def inputTherapies(self, therapies):
        therapies = therapies.replace("\"", "").split(", ")
        return therapies

    def inputSex(self, sex):
        return sex

    def inputCosts(self, cost_values):
        self.var_dict["costs"] = eval(cost_values)
        return 0

    def inputAge(self, age_value):
        return age_value

    def inputCycleLength(self, cycle_l):
        return cycle_l

    def checkIfTerminalState(self, matr, index):
        if matr[index][index] == 1:
            return True
        return False

    def modifyTransMatrix(self, matr, coefs):
        for i in range(len(matr)):
            if not self.checkIfTerminalState(matr, i):
                old_probs = matr[i]
                if fsum(coefs) != len(self.var_dict["states"]):
                    new_probs = np.multiply(old_probs, np.subtract(1, coefs))
                    new_prob_sum = fsum(new_probs)
                    matr[i] = np.multiply(new_probs, 1 / new_prob_sum)
        return matr

    def getValue(self, field_name, key=0):
        field = self.var_dict[field_name]
        if type(field) == dict:
            keys = field.keys()
            if all(self.tryParseToInt(x) for x in keys):
                retrieved_val = field[key]
                if type(retrieved_val) == dict or type(retrieved_val) == list:
                    return retrieved_val.copy()
                else:
                    return retrieved_val
            else:
                if key != 0:
                    retrieved_val = field[key]
                    if type(retrieved_val) == dict or type(retrieved_val) == list:
                        return retrieved_val.copy()
                    else:
                        return retrieved_val
                else:
                    retrieved_val = field
                    if type(retrieved_val) == dict or type(retrieved_val) == list:
                        return retrieved_val.copy()
                    else:
                        return retrieved_val
        else:
            retrieved_val = field
            if type(retrieved_val) == list:
                return retrieved_val.copy()
            else:
                return retrieved_val

    def getCycleLengthByIter(self, iter_n, cycle_l):
        keys = list(cycle_l.keys())
        period = 0
        for i in range(len(keys)):
            if iter_n >= keys[i]:
                period = cycle_l[keys[i]]
        return period

    def forecast(self, iter_n, repeat_n, therapy, initial_state=""):
        mean_cost = 0
        k = 0
        initial_age = self.var_dict["age"]
        while k != repeat_n:
            self.var_dict["age"] = initial_age
            if therapy != "Control":
                therapy = unidecode(therapy)
                coefs = self.getValue("th_effect", therapy)
                matr = self.getValue("trans_matr")
                matr = self.modifyTransMatrix(matr, coefs)
            else:
                matr = self.getValue("trans_matr")
            if initial_state == "":
                initial_state = self.var_dict["states"][0]
            current_state = self.var_dict["states"][0]
            cost = self.getValue("costs", current_state)
            i = 0
            while i != iter_n:
                self.applyConditions()
                for j in range(len(self.var_dict["states"])):
                    if current_state == self.var_dict["states"][j]:
                        if not self.checkIfTerminalState(matr, j):
                            change = np.random.choice(range(len(self.var_dict["states"])), replace=True, p=matr[j])
                            current_state = self.var_dict["states"][change]
                            cost += self.getValue("costs", current_state)
                            if therapy != "Control":
                                cost += self.getValue("costs", therapy)
                        if current_state.lower() != "smert'" and current_state.lower() != "death":
                            self.var_dict["age"] += self.var_dict["cycle_l"] / 12
                        break
                i += 1
            mean_cost += cost
            k += 1
        print(str(round(mean_cost / repeat_n, 2)))


import re


class Reader:
    def __init__(self, model):
        self.cond_var = ""
        self.operator = ""
        self.cond_val = ""
        self.edit_var = ""
        self.edit_val = ""
        self.else_val = ""
        self.model = model
        self.operator_map = {}

    def read(self, s):
        splits = self.findNumberSign(s)
        self.cond_var = s[:splits[0]]
        self.operator = s[splits[0] + 1: splits[1]]
        self.cond_val = s[splits[1] + 1: splits[2]]
        self.edit_var = s[splits[2] + 1: splits[3]]
        if len(splits) == 5:
            self.edit_val = s[splits[3] + 1: splits[4]]
            self.else_val = s[splits[4] + 1:]
        else:
            self.edit_val = s[splits[3] + 1:]
        err_codes = self.evalValues()
        if not all(code == 0 for code in err_codes):
            return "; ".join(map(str, err_codes))
        self.operator_map = {
            "<": self.checkLess(self.model.var_dict[self.cond_var], self.cond_val),
            "<=": self.checkLessOrEquals(self.model.var_dict[self.cond_var], self.cond_val),
            ">": self.checkMore(self.model.var_dict[self.cond_var], self.cond_val),
            ">=": self.checkMoreOrEquals(self.model.var_dict[self.cond_var], self.cond_val),
            "==": self.checkEquals(self.model.var_dict[self.cond_var], self.cond_val),
            "!=": self.checkNotEquals(self.model.var_dict[self.cond_var], self.cond_val)
        }
        self.applyCondition()
        return "0; 0; 0"

    def applyCondition(self):
        if self.operator_map[self.operator]:
            self.model.applySetter(self.edit_var, self.edit_val)
        elif self.else_val != "":
            self.model.applySetter(self.edit_var, self.else_val)

    def checkLess(self, var_value, value):
        try:
            return var_value < value
        except TypeError:
            return False

    def checkLessOrEquals(self, var_value, value):
        try:
            return var_value <= value
        except TypeError:
            return False

    def checkMore(self, var_value, value):
        try:
            return var_value > value
        except TypeError:
            return False

    def checkMoreOrEquals(self, var_value, value):
        try:
            return var_value >= value
        except TypeError:
            return False

    def checkEquals(self, var_value, value):
        return var_value == value

    def checkNotEquals(self, var_value, value):
        return var_value != value

    def findNumberSign(self, condition):
        indices_object = re.finditer(pattern='#', string=condition)
        return [index.start() for index in indices_object]

    def condFlag(self):
        cond_flag = self.tryParseValue(self.cond_var, self.cond_val)
        if type(cond_flag) == list or type(cond_flag) == tuple:
            if cond_flag[0] == 0:
                self.cond_val = cond_flag[1]
                return 0
            else:
                return cond_flag
        else:
            return cond_flag

    def editFlag(self):
        edit_flag = self.tryParseValue(self.edit_var, self.edit_val)
        if type(edit_flag) == list or type(edit_flag) == tuple:
            if edit_flag[0] == 0:
                self.edit_val = edit_flag[1]
                return 0
            else:
                return edit_flag
        else:
            return edit_flag

    def elseFlag(self):
        if self.else_val == "":
            return 0
        else_flag = self.tryParseValue(self.edit_var, self.else_val)
        if type(else_flag) == list or type(else_flag) == tuple:
            if else_flag[0] == 0:
                self.else_val = else_flag[1]
                return 0
            else:
                return else_flag
        else:
            return else_flag

    def evalValues(self):
        flags = [self.condFlag(), self.editFlag(), self.elseFlag()]
        return flags

    def tryParseValue(self, var_name, val):
        if var_name == "states":
            return self.model.parse.readStates(val)
        elif var_name == "costs":
            return self.model.parse.readCost(val)
        elif var_name == "therapies":
            return self.model.parse.readTherapies(val)
        elif var_name == "th_effect":
            return self.model.parse.readEff(val)
        elif var_name == "trans_matr":
            return self.model.parse.readMatr(val)
        elif var_name == "age":
            return self.model.parse.readAge(val)
        else:
            return self.model.parse.readSex(val)

