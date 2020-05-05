# Resources:
# https://stackoverflow.com/questions/10117073/how-to-use-initializer-to-set-up-my-multiprocess-pool
# https://lerner.co.il/2014/05/11/creating-python-dictionaries-reduce/
from multiprocessing import Pool;
import statistics;
import functools;
import math;
import time;

# attach vars to the function passed so they can be used in parallel processing. See SO article for context.
def init_worker(func1, file, charsplit):
    func1.file = file;
    func1.charsplit = charsplit;

# function used to get the character clump starting from the specified index
def mapSplitFile(i): return mapSplitFile.file[(i * mapSplitFile.charsplit):(i * mapSplitFile.charsplit) + mapSplitFile.charsplit];

# function used to calculate each term based on the formula (n_c)(−p_c)lg(p_c)
def mapTerms(arg):
    termFunction = lambda n, p: n * (-1 * p) * math.log(p, 2);
    x = arg[0];
    total = arg[1];
    return termFunction(x, x / total);

# function used to count occurrences in a list of character clumps
def reduceOccurrencesDictionary(dictionary, current):
    dictionary.update({current: dictionary[current] + 1 if current in dictionary.keys() else 1});
    return dictionary;

# function used to reduce a list of numbers to it's summation
def reduceAdd(a, b): return a + b;

# returns entropy in the file based on the specified charsplit and uses the specified number of threads
def getEntropy(file, charsplit, threads):
    # create pool with the specified number of threads
    with Pool(processes=threads, initializer=init_worker, initargs=(mapSplitFile, file, charsplit,)) as pool:
        start = time.time();
        # get a list of occurrences of each character clump from the mapSplitFile result
        occurrences = list(functools.reduce(reduceOccurrencesDictionary,
            pool.map(mapSplitFile, range(math.ceil(len(file) / charsplit))),
            {}).values());
        # add all occurrences together to get the total used in the entropy calculation
        total = functools.reduce(reduceAdd, occurrences);
        # calculate the entropy based on the summation of all of the terms calculated with the mapTerms function
        entropy = functools.reduce(reduceAdd,
            pool.map(mapTerms, map(lambda x: [x, total], occurrences)));
        # return the amount of time spent on the calculation as well as the entropy
        return {
            'processingtime': time.time() - start,
            'entropy': entropy
        };
    # if pool is closed before returning result, raise an exception
    raise Exception('Error encountered when calculating entropy');

# read file from disk
def readFile(filename):
    with open(filename, encoding='UTF-8') as reader:
        return reader.read();
    # if reader is closed before returning result, raise an exception
    raise Exception(f'Error encountered when reading {filename}');

# write file to disk
def writeFile(filename, contents):
    with open(filename, 'w+') as writer:
        writer.write(contents + '\n');
        return True;
    # if writer is closed before returning result, raise an exception
    raise Exception(f'Error encountered when writing {filename}');

# main
if __name__ == '__main__':
    file = readFile('WarAndPeace.txt');
    print(getEntropy(file, 3, 4));
    # the below code was used to calulate average times for the report, so it isn't all functional
    # I didn't stress too much about that since I could've just ran the above code in main
    # multiple times to finish the assignment.
    '''
    file = readFile('WarAndPeace.txt');
    output = '';
    outputJSON = {};
    # for each charsplit
    for charsplit in [1, 2, 3]:
        # for each thread count
        for threads in [1, 2, 4, 8, 16, 32, 64]:
            # for x trials
            results = map(lambda _: getEntropy(file, charsplit, threads), range(3));
            # calculate average time
            average = statistics.mean(map(lambda x: x["processingtime"], results));
            # output to console
            outputJSON.update({f'{threads}Threads_{charsplit}Charsplit': {'processingtime': average}});
            out = f'\nAverage time with {threads} threads and {charsplit} charsplit: {average}\n';
            output += out;
            print(out);
    # output to output.txt/output.json
    writeFile('output.txt', output);
    writeFile('output.json', str(outputJSON));
    '''
