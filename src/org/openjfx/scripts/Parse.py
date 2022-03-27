#!/usr/bin/env python
# coding: utf-8

import re
from math import fsum
import numpy as np


class Parse:

    def __init__(self, states, therapies):
        self.flag_dict = {}
        self.states = []
        self.therapies = []
        if type(self.readStates(states)) == tuple:
            self.states = states.replace("\"", "").split(", ")
        if type(self.readTherapies(therapies)) == tuple:
            self.therapies = therapies.replace("\"", "").split(", ")

    def readStates(self, input_str):
        if len(input_str) == 0:
            return -1
        if input_str.find(", ") == -1:
            return -2
        splits = input_str.split(", ")
        if not all(len(x.strip()) > 0 for x in splits):
            return -3
        if len(set(splits)) != len(splits):
            return -4
        if len(set(splits).intersection(set(self.therapies))) > 0:
            return -5
        return 0, input_str

    def readTherapies(self, input_str):
        if len(input_str) == 0:
            return -1
        splits = input_str.split(", ")
        if not all(len(x.strip()) > 0 for x in splits):
            return -2
        if len(set(splits)) != len(splits):
            return -3
        return 0, input_str

    def readAge(self, input_str):
        try:
            age = int(input_str)
            if age > 0:
                return 0, age
            return -1
        except ValueError:
            return -1

    def readSex(self, input_str):
        input_str = eval(input_str)
        if input_str == 0 or input_str == 1:
            return 0, input_str
        else:
            return -1

    def readCost(self, input_str):
        if len(input_str) == 0:
            return -1
        if input_str.find("\n") == -1:
            return -1
        splits = input_str.split("\n")
        if not all(len(x) > 0 for x in splits):
            return -1
        colon_count = -2
        for split in splits:
            colon = len(re.findall(":", split))
            if colon != colon_count and colon_count != -2:
                return -1
            colon_count = colon
        if colon_count == 0:
            return -1
        elif colon_count == 1:
            return self.readCostDict(splits)
        else:
            return self.readCostMap(splits)

    def tryParseToInt(self, x):
        try:
            int(x)
            return True
        except ValueError:
            return False

    def equalsCollections(self, x, y):
        diff1 = set(x) - set(y)
        diff2 = set(y) - set(x)
        return len(diff1.union(diff2)) == 0

    def checkCostDictValue(self, field):
        try:
            cost_values = eval(str(field))
        except SyntaxError:
            return -6
        keys = list(cost_values.keys())
        if not self.equalsCollections(keys, self.therapies + self.states):
            return -5
        if not all(self.tryParseToInt(x) and int(x) >= 0 for x in cost_values.values()):
            return -6
        values = list(cost_values.values())
        for i in range(len(keys)):
            try:
                cost_values[keys[i]] = eval(values[i])
            except SyntaxError:
                return -7, i
            except ValueError:
                return -7, i
            except NameError:
                return -7, i
        return 0, cost_values

    def readCostDict(self, pairs):
        field = {}
        for pair in pairs:
            if pair.find(":") == -1:
                return -1
            parts = pair.split(":")
            key = parts[0]
            value = parts[1]
            field[key] = value
        return self.checkCostDictValue(field)

    def readCostMapValue(self, field):
        try:
            cost_values = eval(str(field))
        except SyntaxError:
            return -6
        keys = list(cost_values.keys())
        if not all(self.tryParseToInt(x) and int(x) >= 0 for x in keys):
            return -2
        if 0 not in keys:
            return -3
        if len(keys) != len(set(keys)):
            return -4
        try:
            if not all(self.equalsCollections(list(eval(x).keys()), self.therapies + self.states)
                       for x in list(cost_values.values())):
                return -5
        except SyntaxError:
            return -1
        except ValueError:
            return -1
        except NameError:
            return -1
        cost_arrs = list(cost_values.values())
        for i in range(len(cost_arrs)):
            try:
                cost = eval(cost_arrs[i]).values()
            except SyntaxError:
                return -6, i
            except ValueError:
                return -6, i
            except NameError:
                return -6, i
            if not all(self.tryParseToInt(x) and int(x) >= 0 for x in cost):
                return -6, i
            cost_values[keys[i]] = eval(cost_arrs[i])
        return 0, cost_values

    def readCostMap(self, pairs):
        field = {}
        for pair in pairs:
            key_index = pair.find(":")
            if key_index == -1:
                return -1
            key = eval(pair[:key_index])
            value_str = pair[key_index + 2:]
            comma = len(re.findall(",", value_str))
            colon = len(re.findall(":", value_str))
            if colon - comma != 1:
                return -1
            value_pairs = value_str.split(", ")
            val_as_str = []
            for v_pair in value_pairs:
                if v_pair.find(":") == -1:
                    return -1
                parts = v_pair.split(":")
                v_key = parts[0]
                v_value = parts[1]
                pair_as_str = "'" + v_key + "': " + v_value
                val_as_str.append(pair_as_str)
            val_as_str = ", ".join(val_as_str)
            field[key] = "{" + val_as_str + "}"
        return self.readCostMapValue(field)

    def readEff(self, input_str):
        if len(input_str) == 0:
            return -1
        if input_str.find("\n") == -1 and len(self.therapies) != 1:
            return -1
        splits = input_str.split("\n")
        if not all(len(x) > 0 for x in splits):
            return -1
        colon = len(re.findall(":", splits[0]))
        if colon == 0:
            return -1
        elif colon == 1:
            return self.readEffDict(splits)
        else:
            return self.readEffMap(splits)

    def tryParseToFloat(self, x):
        try:
            float(x)
            return True
        except ValueError:
            return False

    def readEffDictValue(self, field):
        try:
            eff_value = eval(str(field))
        except SyntaxError:
            return -7
        keys = list(eff_value.keys())
        if not all(self.tryParseToInt(x) for x in keys):
            values = list(eff_value.values())
            if not self.equalsCollections(keys, list(self.therapies)):
                return -5
            for i in range(len(values)):
                try:
                    value = eval(values[i])
                except SyntaxError:
                    return -7, i
                except ValueError:
                    return -7, i
                except NameError:
                    return -7, i
                if len(value) != len(self.states):
                    return -6, i
                if not all(self.tryParseToFloat(x) and 0 <= float(x) <= 1 for x in value):
                    return -7, i
                eff_value[keys[i]] = value
            return 0, eff_value

    def readEffDict(self, pairs):
        field = {}
        for pair in pairs:
            if pair.find(":") == -1:
                return -1
            parts = pair.split(":")
            key = parts[0]
            value = "[" + parts[1] + "]"
            field[key] = value
        return self.readEffDictValue(field)

    def readEffMapValue(self, field):
        try:
            eff_value = eval(str(field))
        except SyntaxError:
            return -7
        keys = list(eff_value.keys())
        if not all(self.tryParseToInt(x) and int(x) >= 0 for x in keys):
            return -2
        if 0 not in keys:
            return -3
        if len(keys) != len(set(keys)):
            return -4
        eff_arrs = list(eff_value.values())
        for i in range(len(eff_arrs)):
            try:
                i_keys = list(eval(eff_arrs[i]).keys())
            except SyntaxError:
                return -7
            except ValueError:
                return -7, i
            except NameError:
                return -7, i
            if not self.equalsCollections(i_keys, list(self.therapies)):
                return -5, i
            i_values = list(eval(eff_arrs[i]).values())
            if not all(len(x) == len(self.states) for x in i_values):
                return -6, i
            extended_arr = sum(i_values, [])
            if not all(self.tryParseToFloat(x) and 0 <= float(x) <= 1 for x in extended_arr):
                return -7, i
            i_dict = eval(eff_arrs[i])
            eff_value[keys[i]] = i_dict
        return 0, eff_value

    def readEffMap(self, pairs):
        keys = []
        field_as_str = {}
        if len(pairs) < 2:
            return -1
        for pair in pairs:
            key_index = pair.find(":")
            if key_index == -1:
                return -1
            key = eval(pair[:key_index])
            keys.append(key)
            value_str = pair[key_index + 2:]
            value_pairs = value_str.split("; ")
            val_as_str = []
            for v_pair in value_pairs:
                if v_pair.find(":") == -1:
                    return -1
                parts = v_pair.split(":")
                v_key = parts[0]
                v_val = parts[1]
                pair_as_str = "'" + v_key + "': " + "[" + v_val + "]"
                val_as_str.append(pair_as_str)
            val_as_str = ", ".join(val_as_str)
            field_as_str[key] = "{" + val_as_str + "}"
        return self.readEffMapValue(field_as_str)

    def readMatr(self, input_str):
        if len(input_str) == 0:
            return -1
        if input_str.find("\n") == -1:
            return -1
        splits = input_str.split("\n")
        if not all(len(x) > 0 for x in splits):
            return -1
        colon = len(re.findall(":", splits[0]))
        if colon == 0:
            return self.readMatrArr(splits)
        elif colon == 1:
            return self.readMatrMap(splits)
        else:
            return -1

    def checkMatrShape(self, matr):
        if len(matr) != len(self.states):
            return -5
        for i in range(len(matr)):
            if len(matr[i]) != len(self.states):
                return -6, i
        return 0

    def checkProbs(self, matr):
        for i in range(len(matr)):
            if not all(0 <= x <= 1 for x in matr[i]):
                return -7, i
            if np.abs(1 - fsum(matr[i])) > 1e-5:
                return -8, i
        return 0

    def readMatrArrValue(self, matr_rows):
        try:
            matr = eval(str(matr_rows))
        except SyntaxError:
            return -7
        shape_flag = self.checkMatrShape(matr)
        if shape_flag == 0:
            prob_flag = self.checkProbs(matr)
            if prob_flag == 0:
                return 0, matr
            return prob_flag
        else:
            return shape_flag

    def readMatrArr(self, rows):
        matr_rows = []
        for row in rows:
            matr_rows.append("[" + row + "]")
        matr_rows = ",".join(matr_rows)
        matr_rows = "[" + matr_rows + "]"
        return self.readMatrArrValue(matr_rows)

    def readMatrMapValue(self, dict_field):
        try:
            matr = eval(str(dict_field))
        except SyntaxError:
            return -7
        keys = list(matr.keys())
        if not all(self.tryParseToInt(x) and int(x) >= 0 for x in keys):
            return -2
        if 0 not in keys:
            return -3
        if len(keys) != len(set(keys)):
            return -4
        values = list(matr.values())
        for i in range(len(values)):
            try:
                i_matr = eval(values[i])
            except SyntaxError:
                return -7, i
            except ValueError:
                return -7, i
            except NameError:
                return -7, i
            shape_flag = self.checkMatrShape(i_matr)
            if shape_flag == 0:
                prob_flag = self.checkProbs(i_matr)
                if prob_flag != 0:
                    return prob_flag, i
                else:
                    matr[keys[i]] = i_matr
            else:
                return shape_flag, i
        print(matr)
        print("matr")
        return 0, matr

    def readMatrMap(self, rows):
        dict_field = {}
        keys = []
        values = []
        cur_matr = []
        for i in range(len(rows)):
            row = rows[i]
            index = row.find(":")
            if index == len(row) - 1:
                key = eval(row[:index])
                keys.append(key)
                if len(cur_matr) != 0:
                    cur_matr = ", ".join(cur_matr)
                    values.append("[" + cur_matr + "]")
                    dict_field[keys[-2]] = "[" + cur_matr + "]"
                    cur_matr = []
            elif row.find(":") == -1:
                cur_matr.append("[" + row + "]")
            else:
                return -1
        if len(keys) < 2:
            return -1
        if len(cur_matr) != 0:
            cur_matr = ", ".join(cur_matr)
            values.append("[" + cur_matr + "]")
            dict_field[keys[-1]] = "[" + cur_matr + "]"
        return self.readMatrMapValue(dict_field)
