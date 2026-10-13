#!/bin/sh
set -eu
owner=kawakaze430
repo=agentic-finance-platform
if ! command -v gh >/dev/null 2>&1; then
  echo 'Install the GitHub CLI and authenticate securely with gh auth login.' >&2
  exit 1
fi
login=$(gh api user --jq .login)
if [ "$login" != "$owner" ]; then
  echo "Refusing to publish using a different GitHub account." >&2
  exit 1
fi
if [ -n "$(git status --porcelain)" ]; then
  echo 'Commit or remove untracked changes before publishing.' >&2
  exit 1
fi
./mvnw -B -ntp verify
if gh repo view "$owner/$repo" >/dev/null 2>&1; then
  echo 'Repository already exists. Inspect it before adding a remote and pushing.' >&2
  exit 1
fi
gh repo create "$owner/$repo" --public --description 'Java/Spring Boot invoice operations with balanced bookkeeping, read-only agent tools, and auditable human approvals' --source . --remote origin --push
