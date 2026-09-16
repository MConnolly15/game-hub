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
  description = "Generated password for a branch database (null for main, which is unmanaged)"
  value       = local.is_main ? null : random_password.branch_db[0].result
  sensitive   = true
}

output "ec2_public_ip" {
  description = "Public IP of the Game Hub app instance (main branch only)"
  value       = local.is_main ? aws_instance.app[0].public_ip : null
}

output "website_url" {
  description = "Click this to open the running app in a browser (main branch only)"
  value       = local.is_main ? "http://${aws_instance.app[0].public_ip}:${var.app_port}" : null
}