#!/usr/bin/env python
# coding=utf-8

import os

os.system('mkdir -p results')
os.system('javac homework.java')
for i in xrange(1,97):
    os.system('cp ./TestCase_New/Test{0}/input.txt ./input.txt'.format(i))
    os.system('java homework')
    os.system('time ./a.out')
    print("-->On test case #{0}<--".format(i))
    os.system('cp ./output.txt ./TestCase_New/Test{0}/output.txt'.format(i))
    os.system('diff ./Test{0}/output.txt ./TestCase_New/Test{0}/output.txt'.format(i))

os.system('rm input.txt output.txt')

#import os
#
#os.system('mkdir -p results')
#os.system('javac homework.java')
#for i in xrange(0,100):
#    os.system('cp ./{0}.in ./input.txt'.format(i))
#    os.system('java homework')
#    print("-->On test case #{0}<--".format(i))
#    os.system('cp ./{0}.out ./output{0}.txt'.format(i))
#    os.system('diff ./{0}.out ./output{0}.txt'.format(i))
#    os.system('rm input.txt output{0}.txt'.format(i))


