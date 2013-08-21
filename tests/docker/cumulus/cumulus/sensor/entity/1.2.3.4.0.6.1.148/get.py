from __future__ import print_function
import sys
import time
import json
import cgitb
cgitb.enable()

updated = str(int(time.mktime(time.gmtime())))
sensor = {
	"tags": ["room", "inside", "entrance"],
	"updated": updated,
	"description": "upart attached on the entrance",
	"registered": "1300233479548",
	"data": {
		"light": {
			"unit": "level",
			"updated": updated
		},
		"move": {
			"unit": "count",
			"updated": updated,
			"value": "0.0"
		},
		"temperature": {
			"unit":"celsius",
			"updated": updated
		}
	},
	"longitude": "8.424188531935215",
	"permission": "1",
	"latitude":"49.01369919357993"
}

try:
	with open('values', 'r') as f:
		sensor["data"]["light"]["value"] = f.readline().strip()
		sensor["data"]["temperature"]["value"] = f.readline().strip()
except IOError:
	pass

print("Content type: text/plain")
print()
json.dump(sensor, sys.stdout, indent=2)
