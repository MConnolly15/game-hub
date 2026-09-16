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
  value       = data.aws_db_instance.database.address
}

output "rds_port" {
  description = "RDS database port"
  value       = data.aws_db_instance.database.port
}