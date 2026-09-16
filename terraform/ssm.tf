locals {
  ssm_prefix = local.is_main ? "/game-hive/${var.environment}/db" : "/game-hive/${var.environment}/${local.branch_slug}/db"
}

resource "aws_ssm_parameter" "db_host" {
    name = "${local.ssm_prefix}/host"
    type = "String"
    value = aws_db_instance.postgres.address
}

resource "aws_ssm_parameter" "db_port" {
    name = "${local.ssm_prefix}/port"
    type = "String"
    value = tostring(aws_db_instance.postgres.port)
}

resource "aws_ssm_parameter" "db_name" {
    name = "${local.ssm_prefix}/name"
    type = "String"
    value = aws_db_instance.postgres.db_name
}

resource "aws_ssm_parameter" "db_username" {
  name = "${local.ssm_prefix}/username"
  type = "String"
  value = aws_db_instance.postgres.username
}

resource "aws_ssm_parameter" "db_password" {
    name = "${local.ssm_prefix}/password"
    type = "SecureString"
    value = local.is_main ? "set-manually-in-aws" : random_password.branch_db[0].result

    lifecycle {
      ignore_changes = [value] 
    }
}