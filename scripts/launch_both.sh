#!/bin/bash

mvn exec:java -Dexec.mainClass=za.co.wethinkcode.robots.server.Server 

mvn exec:java -Dexec.mainClass=za.co.wethinkcode.robots.client.Client
