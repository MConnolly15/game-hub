data "aws_caller_identity" "current" {
}

data "aws_db_instance" "database" {
  db_instance_identifier = "game-hive-db"
}