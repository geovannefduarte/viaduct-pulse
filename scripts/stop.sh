#!/usr/bin/env bash
# Stops every Pulse app started from this checkout, then any embedded Postgres still serving one of its data directories.
set -euo pipefail

repo_root=$(cd "$(dirname "$0")/.." && pwd -P)

cwd_of() {
  if [[ -d /proc/$1 ]]; then
    readlink "/proc/$1/cwd" 2>/dev/null || true
  else
    lsof -a -p "$1" -d cwd -Fn 2>/dev/null | sed -n 's/^n//p'
  fi
}

belongs_here() {
  local args cwd
  args=$(ps -o args= -p "$1" 2>/dev/null) || return 1
  cwd=$(cwd_of "$1")
  [[ $args == *"$repo_root"* || $cwd == "$repo_root" || $cwd == "$repo_root"/* ]]
}

app_pids() {
  local pid
  for pid in $(pgrep -f 'dev\.geovanne\.pulse\.PulseApplication|app/build/libs/app\.jar' || true); do
    if belongs_here "$pid"; then echo "$pid"; fi
  done
}

postgres_pids() {
  local pid
  for pid in $(pgrep -x postgres || true); do
    if [[ $(ps -o args= -p "$pid" 2>/dev/null) == *" -D "* ]] && belongs_here "$pid"; then echo "$pid"; fi
  done
}

stop_all() {
  local signal=$1 wait_seconds=$2 list=$3 name=$4 pids pid
  read -r -a pids <<< "$($list | tr '\n' ' ')"
  if [[ ${#pids[@]} -eq 0 ]]; then return 0; fi
  echo "Stopping $name (SIG$signal): ${pids[*]}"
  kill "-$signal" "${pids[@]}" 2>/dev/null || true
  for _ in $(seq "$wait_seconds"); do
    if [[ -z $($list) ]]; then return 0; fi
    sleep 1
  done
  read -r -a pids <<< "$($list | tr '\n' ' ')"
  if [[ ${#pids[@]} -eq 0 ]]; then return 0; fi
  echo "Killing $name that did not stop: ${pids[*]}"
  for pid in "${pids[@]}"; do pkill -KILL -P "$pid" 2>/dev/null || true; done
  kill -KILL "${pids[@]}" 2>/dev/null || true
}

stop_all TERM 20 app_pids "the app"
stop_all INT 10 postgres_pids "embedded Postgres"

port=${SERVER_PORT:-8080}
holder=$(lsof -nP -tiTCP:"$port" -sTCP:LISTEN 2>/dev/null || true)
if [[ -n $holder ]]; then
  echo "Port $port is still in use by a process that isn't Pulse from this checkout:"
  ps -o pid=,args= -p "$holder" | cut -c1-160
  exit 1
fi
echo "Pulse is stopped."
