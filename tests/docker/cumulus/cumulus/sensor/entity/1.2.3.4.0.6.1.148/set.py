from __future__ import print_function
import sys
import cgi
import cgitb
cgitb.enable()

print("Content type: text/plain")
print()

form = cgi.FieldStorage()
if "temperature" not in form or "light" not in form:
	print("missing values")
else:
	with open('values', 'w') as f:
		print(form["light"].value, file=f)
		print(form["temperature"].value, file=f)
	print("OK")
