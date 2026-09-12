#!/usr/bin/env bash
set -euo pipefail

usage() {
  echo "Usage: TV=192.168.1.50 [PORT=8090] [TOKEN=...] $0 [command]"
  echo "Commands: health info simple full replace persistent clear queue invalid all (default: all)"
}

TV="${TV:-192.168.1.50}"
PORT="${PORT:-8090}"
BASE="http://${TV}:${PORT}"
TOKEN="${TOKEN:-}"

auth=()
if [[ -n "$TOKEN" ]]; then
  auth=(-H "Authorization: Bearer ${TOKEN}")
fi

post() {
  curl -sS -X POST "${BASE}/notify" -H 'Content-Type: application/json' "${auth[@]}" -d "$1"
  echo
}

health() {
  echo "# GET /health (never needs a token)"
  curl -sS "${BASE}/health"
  echo
}

info() {
  echo "# GET /info"
  curl -sS "${auth[@]}" "${BASE}/info"
  echo
}

simple() {
  echo "# POST /notify with only the required field"
  post '{"message":"Dinner is ready"}'
}

full() {
  echo "# POST /notify with every field"
  post '{
    "id": "order-1042",
    "title": "Order #1042",
    "message": "New order received. 2x Margherita, 1x Tiramisu.",
    "image": "https://picsum.photos/seed/tibeepost/800/400",
    "icon": "https://picsum.photos/seed/icon/96/96",
    "duration": 20,
    "persistent": false,
    "position": "center",
    "widthPercent": 70,
    "background": "#B91C1C",
    "textColor": "#FFFFFF",
    "accent": "#FDE047",
    "dim": 0.9,
    "sound": "default",
    "speak": true
  }'
}

replace() {
  echo "# Reusing an id updates the card in place"
  post '{"id":"order-1042","title":"Order #1042","message":"Now being prepared","background":"#065F46","textColor":"#FFFFFF"}'
}

persistent() {
  echo "# A persistent card stays until it is deleted"
  post '{"id":"kitchen","message":"Kitchen closes in 10 minutes","persistent":true,"position":"top-right","widthPercent":40,"sound":"none"}'
}

clear_card() {
  echo "# DELETE /notify/kitchen"
  curl -sS -X DELETE "${auth[@]}" "${BASE}/notify/kitchen"
  echo
}

queue() {
  echo "# Three quick posts: the first shows, the others queue"
  for n in 1 2 3; do
    post "{\"id\":\"queue-${n}\",\"message\":\"Queued card ${n}\",\"duration\":3,\"sound\":\"none\"}"
  done
}

invalid() {
  echo "# Validation errors name the field (expect HTTP 400)"
  curl -sS -o /dev/stdout -w ' -> HTTP %{http_code}\n' -X POST "${BASE}/notify" \
    -H 'Content-Type: application/json' "${auth[@]}" \
    -d '{"message":"x","duration":"soon"}'
  curl -sS -o /dev/stdout -w ' -> HTTP %{http_code}\n' -X POST "${BASE}/notify" \
    -H 'Content-Type: application/json' "${auth[@]}" \
    -d '{"title":"no message"}'
  curl -sS -o /dev/stdout -w ' -> HTTP %{http_code}\n' -X POST "${BASE}/notify" \
    -H 'Content-Type: application/json' "${auth[@]}" \
    -d '{not json'
}

all() {
  health
  info
  simple
  full
  replace
  persistent
  clear_card
  queue
  invalid
}

case "${1:-all}" in
  health) health ;;
  info) info ;;
  simple) simple ;;
  full) full ;;
  replace) replace ;;
  persistent) persistent ;;
  clear) clear_card ;;
  queue) queue ;;
  invalid) invalid ;;
  all) all ;;
  -h|--help) usage ;;
  *) echo "unknown command: $1" >&2; usage >&2; exit 1 ;;
esac
