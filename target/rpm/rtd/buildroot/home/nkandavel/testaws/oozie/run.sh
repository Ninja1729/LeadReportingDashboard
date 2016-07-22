#!/usr/bin/env bash

# Triggers Oozie TC-Pricing-Processing coordinator using manual properties
# To overwrite the coordinator's start and end date pass the following arguments:
# param1:  startDate=YYYY-MM-DDTMM:SSZ
# param2:  endDate=YYYY-MM-DDTMM:SSZ

currentPath=$(dirname $0)

echo "Submitting oozie coordinator with custom start and end dates"
oozie job -oozie  http://oozie:11000/oozie/ -config $currentPath/coordinator.properties  -D startDate=${1} -D endDate=${2} -run