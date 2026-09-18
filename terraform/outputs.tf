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

output "db_password" {
  description = "Generated database password"
  value       = random_password.db.result
  sensitive   = true
}

output "ec2_public_ip" {
  description = "Public IP of the Game Hub application instance"
  value       = aws_instance.app[0].public_ip
}

output "website_url" {
  description = "URL for the deployed Game Hub application"
  value       = "http://${aws_instance.app[0].public_ip}:${var.app_port}"
}