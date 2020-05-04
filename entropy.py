import math;

def termFunction(n, p):
    return n * (-1 * p) * math.log(p, 2);

def getEntropy(terms):
    result = 0;
    for x in terms:
        result += x;
    return result;

def getOccurrences(splitfile):
    dictionary = {};
    for item in splitfile:
        if item in dictionary.keys():
            dictionary[item] = dictionary[item] + 1;
        else:
            dictionary[item] = 1;
    return list(dictionary.values());

def getTerms(occurrences):
    total = 0;
    for x in occurrences:
        total += x;
    result = [];
    for x in occurrences:
        result.append(termFunction(x, x / total));
    return result;

def getSplitFile(filecontents, charactersplit):
    splitfile = [];
    for i in range(math.ceil(len(filecontents) / charactersplit)):
        beginindex = (i * charactersplit);
        endindex = (i * charactersplit) + charactersplit;
        splitfile.append(filecontents[beginindex:endindex]);
    return splitfile;

filename = 'WarAndPeace.txt'; # Shorter.txt
with open(filename, encoding='UTF-8') as reader:
    entropy = getEntropy(getTerms(getOccurrences(getSplitFile(reader.read(), 3))));
    print(entropy);
