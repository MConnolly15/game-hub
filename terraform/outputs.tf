output "aws_account_id" {
  description = "AWS ID"
  value       = data.aws_caller_identity.current.account_id
}

output "aws_caller_arn" {
  description = "ARN of identity"
  value       = data.aws_caller_identity.current.arn
}

output "rds_endpoint" {
  description = "RDS database endpoint"
  value       = aws_db_instance.postgres.address
}

output "rds_port" {
  description = "RDS database port"
  value       = aws_db_instance.postgres.port
}

output "rds_db_name" {
  description = "RDS database name"
  value       = aws_db_instance.postgres.db_name
}

output "rds_username" {
  description = "RDS master username"
  value       = aws_db_instance.postgres.username
}

output "branch_db_password" {
  description = "Generated password for branch database"
  value       = local.is_main ? null : random_password.branch_db[0].result
  sensitive   = true
}

output "ec2_public_ip" {
  description = "Public IP of the branch Game Hub app instance"
  value       = local.is_main ? null : aws_instance.app[0].public_ip
}

output "website_url" {
  description = "URL for the deployed branch Game Hub application"
  value       = local.is_main ? null : "http://${aws_instance.app[0].public_ip}:${var.app_port}"
}