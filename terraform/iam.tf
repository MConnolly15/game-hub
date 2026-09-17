resource "aws_security_group" "ec2" {
  name        = "${local.resource_prefix}-ec2"
  description = "Game Hub app instance: SSH and app port open"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    description = "SSH"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "App"
    from_port   = var.app_port
    to_port     = var.app_port
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name   = "${local.resource_prefix}-ec2"
    Branch = var.branch_name
  }
}

resource "aws_security_group" "rds_main" {
  count = local.is_main ? 1 : 0

  name        = "${local.resource_prefix}-rds-main"
  description = "Game Hub database: Postgres from the app instance only"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    description     = "Postgres from app instance"
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_security_group.ec2.id]
  }

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_security_group" "rds_branch" {
  count = local.is_main ? 0 : 1

  name        = "${local.resource_prefix}-rds-branch"
  description = "Game Hub disposable branch database"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    description = "Postgres from CI and branch app"
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  lifecycle {
    create_before_destroy = true
  }
}

data "aws_iam_policy_document" "ec2_assume_role" {
  statement {
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["ec2.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "ec2_app" {
  name               = "${local.resource_prefix}-ec2-app"
  assume_role_policy = data.aws_iam_policy_document.ec2_assume_role.json
}

data "aws_iam_policy_document" "ec2_app_ssm_read" {
  statement {
    actions = [
      "ssm:GetParameter",
      "ssm:GetParameters",
    ]

    resources = [
      "arn:aws:ssm:${var.aws_region}:${data.aws_caller_identity.current.account_id}:parameter/game-hive/${var.environment}/*",
    ]
  }
}

resource "aws_iam_role_policy" "ec2_app_ssm_read" {
  name   = "ssm-read"
  role   = aws_iam_role.ec2_app.id
  policy = data.aws_iam_policy_document.ec2_app_ssm_read.json
}

resource "aws_iam_instance_profile" "app" {
  name = "${local.resource_prefix}-ec2-app"
  role = aws_iam_role.ec2_app.name
}