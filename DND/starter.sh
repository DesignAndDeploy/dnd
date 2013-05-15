#!/usr/bin/env bash

java -cp bin:testbin:lib/comm.jar:lib/gson-2.2.2.jar:lib/lights.jar:lib/log4j-api-2.0-beta3.jar:lib/log4j-core-2.0-beta3.jar -Dlog4j.configurationFile=log4j2.xml edu.teco.dnd.module.Module "$@"
