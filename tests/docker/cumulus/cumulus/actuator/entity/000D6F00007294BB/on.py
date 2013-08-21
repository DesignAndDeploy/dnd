from __future__ import print_function
import sys
import time
import json
import cgitb
cgitb.enable()

try:
	with open('state', 'w') as f:
		print("on", file=f)
except IOError:
	pass

print("Content type: text/plain")
print()
print("OK")
