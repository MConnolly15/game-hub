resource "aws_db_subnet_group" "default" {
  name = local.is_main ? (
    "${var.project_name}-${var.environment}-db"
    ) : (
    "${local.resource_prefix}-db"
  )

  subnet_ids = data.aws_subnets.default.ids

  lifecycle {
    create_before_destroy = true
  }
}

resource "random_password" "branch_db" {
  count = local.is_main ? 0 : 1

  length           = 24
  special          = true
  override_special = "-_"
}

resource "aws_db_instance" "postgres" {
  identifier = local.is_main ? "game-hive-db" : "game-hive-db-${local.branch_slug}"

  engine              = "postgres"
  engine_version      = var.db_engine_version
  instance_class      = var.db_instance_class
  allocated_storage   = var.db_allocated_storage
  storage_type        = var.db_storage_type
  username            = var.db_master_username
  db_name             = var.db_name
  publicly_accessible = local.is_main ? var.db_main_publicly_accessible : true
  multi_az            = local.is_main ? var.db_main_multi_az : false

  db_subnet_group_name = aws_db_subnet_group.default.name

  vpc_security_group_ids = local.is_main ? [
    aws_security_group.rds_main[0].id
    ] : [
    aws_security_group.rds_branch[0].id
  ]

  deletion_protection = local.is_main
  skip_final_snapshot = !local.is_main
  apply_immediately   = !local.is_main

  password = local.is_main ? "unmanaged-set-manually-in-aws" : random_password.branch_db[0].result

  lifecycle {
    ignore_changes = [
      password,
      engine_version,
      backup_retention_period,
    ]
  }

  tags = {
    Branch = var.branch_name
  }
}