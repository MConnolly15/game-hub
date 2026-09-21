#!/usr/bin/env bash

set -euo pipefail

if [ $# -lt 1 ]; then
  echo "Usage:"
  echo "  ./run-local.sh <branch-name>"
  echo ""
  echo "Examples:"
  echo "  ./run-local.sh main"
  echo "  ./run-local.sh terraform-aws-scaffold"
  echo "  ./run-local.sh feature/login-page"
  exit 1
fi

BRANCH_NAME="$1"
AWS_REGION="eu-west-2"
ENVIRONMENT="dev"

echo "Starting local Game Hub using branch:"
echo "  $BRANCH_NAME"
echo ""

BRANCH_SLUG="$(echo "$BRANCH_NAME" \
  | tr '[:upper:]' '[:lower:]' \
  | sed -E 's/[^a-z0-9]+/-/g' \
  | sed -E 's/^-+//; s/-+$//')"

if [ "$BRANCH_NAME" = "main" ]; then
  SSM_PREFIX="/game-hive/${ENVIRONMENT}/db"
else
  SSM_PREFIX="/game-hive/${ENVIRONMENT}/${BRANCH_SLUG}/db"
fi

echo "Using SSM path:"
echo "  $SSM_PREFIX"
echo ""

echo "Checking AWS credentials..."

aws sts get-caller-identity \
  --region "$AWS_REGION" \
  > /dev/null

echo "AWS credentials valid."
echo ""

echo "Reading database configuration from AWS SSM..."

DB_HOST="$(aws ssm get-parameter \
  --region "$AWS_REGION" \
  --name "${SSM_PREFIX}/host" \
  --query "Parameter.Value" \
  --output text)"

DB_PORT="$(aws ssm get-parameter \
  --region "$AWS_REGION" \
  --name "${SSM_PREFIX}/port" \
  --query "Parameter.Value" \
  --output text)"

DB_NAME="$(aws ssm get-parameter \
  --region "$AWS_REGION" \
  --name "${SSM_PREFIX}/name" \
  --query "Parameter.Value" \
  --output text)"

DB_USERNAME="$(aws ssm get-parameter \
  --region "$AWS_REGION" \
  --name "${SSM_PREFIX}/username" \
  --query "Parameter.Value" \
  --output text)"

DB_PASSWORD="$(aws ssm get-parameter \
  --region "$AWS_REGION" \
  --name "${SSM_PREFIX}/password" \
  --with-decryption \
  --query "Parameter.Value" \
  --output text)"

export DB_HOST
export DB_PORT
export DB_NAME
export DB_USERNAME
export DB_PASSWORD

echo "Database configuration loaded."
echo ""
echo "Database:"
echo "  Host:     $DB_HOST"
echo "  Port:     $DB_PORT"
echo "  Database: $DB_NAME"
echo "  Username: $DB_USERNAME"
echo "  Password: ********"
echo ""

echo "Starting Spring Boot..."
echo ""

./gradlew bootRun