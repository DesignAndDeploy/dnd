from __future__ import print_function
import os
import sys
import json
import cgitb
cgitb.enable()

print("Content type: text/plain")
print()

if 'REQUEST_METHOD' not in os.environ or os.environ['REQUEST_METHOD'] != 'PUT':
	print("not a PUT request")
	exit(1)

obj = json.load(sys.stdin)
if not obj or "temp" not in obj:
	print("invalid data")
elif not isinstance(obj["temp"], basestring):
	print("invalid type")
else:
	with open('value', 'w') as f:
		print(obj["temp"], file=f)
	print("OK")
