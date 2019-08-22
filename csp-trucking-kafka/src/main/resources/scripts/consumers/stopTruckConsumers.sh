#!/bin/bash

kill $(ps aux | grep 'smm-producers-consumers-generator*' | awk '{print $2}')