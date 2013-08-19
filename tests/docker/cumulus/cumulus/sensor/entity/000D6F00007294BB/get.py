from __future__ import print_function
import sys
import time
import json
import cgitb
cgitb.enable()

updated = str(int(time.mktime(time.gmtime())))
sensor = {
	"tags": ["yong", "desktop", "power", "yong", "desk", "d-bridge"],
	"updated": updated,
	"description": "d-bridge power consumption",
	"registered": "1315332111479",
	"data": {
		"energy": {
			"unit": "watt",
			"updated": updated
		}
	},
	"longitude": "8.424180150032043",
	"permission": "1",
	"latitude": "49.01351166210754"
}

try:
	with open('values', 'r') as f:
		sensor["data"]["energy"]["value"] = f.readline().strip()
except IOError:
	pass

print("Content type: text/plain")
print()
json.dump(sensor, sys.stdout, indent=2)
