from __future__ import print_function
import sys
import cgi
import cgitb
cgitb.enable()

print("Content type: text/plain")
print()

form = cgi.FieldStorage()
if "energy" not in form:
	print("missing values")
else:
	with open('values', 'w') as f:
		print(form["energy"].value, file=f)
	print("OK")
