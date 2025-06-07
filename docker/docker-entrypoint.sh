#!/usr/bin/env bash
if [ "$1" = 'afk-bot' ]; then
  mkdir -p /var/log/afkbot/
  if [ ! -f /usr/lib/afkbot/data/config.json ]; then
    echo "No config file was found! Creating a new config file".
    rm 2>/dev/null 1>&2 -rf /tmp/afkbot-init/logs
    mkdir -p /tmp/afkbot-init/logs
    /usr/bin/fishing-bot -logsdir /tmp/afkbot-init/logs -config /tmp/afkbot-init/config.json -onlyCreateConfig
    jq ".server.ip = \"${MC_SERVER}\"" /tmp/afkbot-init/config.json |
      jq ".server.port = ${MC_PORT}" |
      jq ".server.\"default-protocol\" = \"${MC_PROTOCOL}\"" |
      jq ".server.\"online-mode\" = ${MC_ONLINE_MODE}" >/usr/lib/afkbot/data/config.json
  fi
fi

exec "$@"
