import math;
import functools;

def getTerms(occurrences):
    total = functools.reduce(lambda a, b: a + b, occurrences);
    return list(map(lambda x: termFunction(x, x / total), occurrences));

# https://lerner.co.il/2014/05/11/creating-python-dictionaries-reduce/
getOccurrences = lambda splitfile: list(functools.reduce(lambda d, current: d.update({current: d[current] + 1 if current in d.keys() else 1}) or d, splitfile, {}).values());
getSplitFile = lambda file, charsplit: list(map(lambda x: file[(x[0] * charsplit):(x[0] * charsplit) + charsplit], enumerate([None] * math.ceil(len(file) / charsplit))));
getEntropy = lambda splitfile: functools.reduce(lambda a, b: a + b, getTerms(getOccurrences(splitfile)));
termFunction = lambda n, p: n * (-1 * p) * math.log(p, 2);

filename = 'WarAndPeace.txt'; # Shorter.txt
with open(filename, encoding='UTF-8') as reader:
    print(getEntropy(getSplitFile(reader.read(), 3)));
