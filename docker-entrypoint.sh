#!/bin/bash

echo "Starting OMNITRACKER-git docker-entrypoint.sh..."

DAEMON=${DAEMON:-NO}
SLEEP_DURATION=${SLEEP_DURATION:-60}
# Compare strings lower case
if [[ "${DAEMON,,}" = "yes" ]]; then
  SLEEP_DURATION=60

  echo "\$DAEMON is set to 'yes' with \$SLEEP_DURATION set to $SLEEP_DURATION seconds..."
  echo

  while true; do
    java -jar /app/omnitracker-git.jar commit

    echo "Sleeping for $SLEEP_DURATION seconds before restart..."
    sleep ${SLEEP_DURATION}
  done
else
  echo "\$DAEMON not set to 'yes'; running once..."
  echo
  java -jar /app/omnitracker-git.jar
fi
