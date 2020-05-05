# https://stackoverflow.com/questions/10117073/how-to-use-initializer-to-set-up-my-multiprocess-pool
# https://stackoverflow.com/questions/10117073/how-to-use-initializer-to-set-up-my-multiprocess-pool
from multiprocessing import Pool;
import statistics;
import functools;
import math;
import time;

def init_worker(funcs, file, charsplit):
    for func in funcs:
        func.file = file;
        func.charsplit = charsplit;

def mapSplitFile(i):
    return mapSplitFile.file[(i * mapSplitFile.charsplit):(i * mapSplitFile.charsplit) + mapSplitFile.charsplit];

def mapTerms(arg):
    termFunction = lambda n, p: n * (-1 * p) * math.log(p, 2);
    x = arg[0];
    total = arg[1];
    return termFunction(x, x / total);

def main(file, charsplit, threads):
    with Pool(processes=threads,initializer=init_worker, initargs=([mapSplitFile,mapTerms],file,charsplit,)) as pool:
        start = time.time();
        splitfile = pool.map(mapSplitFile, range(math.ceil(len(file) / charsplit)));
        occurrences = list(functools.reduce(lambda d, current: d.update({current: d[current] + 1 if current in d.keys() else 1}) or d, splitfile, {}).values());
        total = functools.reduce(lambda a, b: a + b, occurrences);
        occurrences = [[x, total] for x in occurrences];
        terms = pool.map(mapTerms, occurrences);
        entropy = functools.reduce(lambda a, b: a + b, terms);
        end = time.time();
        return end - start, entropy;

if __name__ == '__main__':
    file = '';
    with open('WarAndPeace.txt', encoding='UTF-8') as reader: # Shorter
        file = reader.read();
    output = '';
    for charsplit in [1, 2, 3]:
        for threads in [1, 2, 4, 8, 16, 32, 64]:
            times = [];
            for i in range(3):
                result = main(file, charsplit, threads);
                times.append(result[0]);
                output += f'Total time with {threads} threads: {result[0]} seconds . . . entropy: {result[1]}';
            output += f'\nAverage time with {threads} threads and {charsplit} charsplit: {statistics.mean(times)}\n';
            print(f'\nAverage time with {threads} threads and {charsplit} charsplit: {statistics.mean(times)}\n');
    with open('output.txt', 'w+') as writer:
        writer.write(output);
