from __future__ import print_function
import sys
import time
import json
import cgitb
cgitb.enable()

updated = str(int(time.mktime(time.gmtime())))
sensor = {
	"tags": ["heater", "room", "temperature"],
	"updated": updated,
	"description": "Heater in living lab",
	"registered": "1340290876682",
	"data": {
		"window": {
			"unit": "boolean",
			"updated": updated,
			"value": "1.0"
		},
		"temperature": {
			"unit": "celcious",
			"updated": updated
		}
	},
	"longitude": "8.42425525188446",
	"permission": "1",
	"latitude": "49.01351166210754"
}

try:
	with open('values', 'r') as f:
		sensor["data"]["temperature"]["value"] = f.readline().strip()
except IOError:
	pass

print("Content type: text/plain")
print()
json.dump(sensor, sys.stdout, indent=2)
