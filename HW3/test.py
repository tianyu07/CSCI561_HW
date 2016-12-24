#!/usr/bin/env python
# coding=utf-8

import os

os.system('rm -rf ./Result')
os.system('mkdir ./Result')

for i in xrange(1,31):
	print("--Test Case #{0}--".format(i))
	os.system('cp ./TestCase/input{0}.txt ./input.txt'.format(i))
	os.system('java homework')
	print("")
	os.system('diff ./output.txt ./TestCase/output{0}.txt'.format(i))
	os.system('cp ./output.txt ./Result/output{0}.txt'.format(i))

os.system('rm output.txt')
