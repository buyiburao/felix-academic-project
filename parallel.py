#parse kv file, returns [ (K, V), ...]
def parseKVList(f):
	lines = [x.strip() for x in open(f) if x.strip() != '']
	return [x.split('\t') for x in lines]
import threading
class CommandThread(threading.Thread):
	def __init__(self, commandline, outfile):
		threading.Thread.__init__(self)
		self.commandline = commandline
		self.outfile = outfile
	def run(self):
		import subprocess 
		out = open(self.outfile + '.out', 'w')
		err = open(self.outfile + '.err', 'w')
		pipe = subprocess.Popen(
				self.commandline,
				stdout = out,
				stderr = err,
				shell = True
				)
		pipe.wait()
		out.close()
		err.close()

if __name__ == '__main__':
	import sys
	import getopt
	optlist, args = getopt.getopt(sys.argv[1:], 'l:d:c:o:')
	machinelist = ''
	dependency = ''
	commandline = 'echo hello world!'
	outdir = 'outdir'
	for k, v in optlist:
		if k == '-l':
			machinelist = v
		elif k == '-d':
			dependency = v
		elif k == '-c':
			commandline = v
		elif k == '-o':
			outdir = v
	import os
	os.system('mkdir ' + outdir)
	if machinelist == '':
		print 'usage: prog -l machinelist [-d dependency] [-c commandline] [-o outdir]'
		exit()
	#parse machinelist
	print 'parsing machine list'
	machines = parseKVList(machinelist)
	print machines
	if len(machines) > 0 and len(machines[0]) == 1:
		machines = [(x[0], '') for x in machines]
	print 'machine\textra_params'
	for m, p in machines:
		print m + '\t' + p
	print
	#processing dependency
	if dependency != '':
		dependencies = parseKVList(dependency)
		for f, t in dependencies:
			print f + ' => ' + t
			threads = []
			for m, p in machines:
				threads.append(
						CommandThread(
							'rsync -rv --size-only %s %s:%s' % (f, m , t),
							outdir + '/' + m + '.rsync'
						)
					)
			for th in threads:
				th.start()
			while True:
				print
				flag = False
				for th in threads:
					if th.isAlive():
						flag = True
						print th.commandline, ' Alive'
				if not flag:
					break
				import time
				time.sleep(5)
	#executing command
	threads = []
	for m, p in machines:
		threads.append(CommandThread('''ssh %s "%s %s"''' % (m, commandline, p), outdir + '/' + m))
	for th in threads:
		th.start()
	while True:
		print
		flag = False
		for th in threads:
			if th.isAlive():
				flag = True
				print th.commandline, ' Alive'
		if not flag:
			break
		import time
		time.sleep(5)
