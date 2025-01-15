#!/bin/bash

LOG_LINE=$1

if cat /var/log/*.log | grep -q "$LOG_LINE"; then
    exit 0  # Healthy
else
    exit 1  # Unhealthy
fi