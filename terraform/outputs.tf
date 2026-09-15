output "aws_account_id" {
  description = "AWS ID"
  value       = data.aws_caller_identity.current.account_id
}

output "aws_caller_arn" {
  description = "ARN of identity"
  value       = data.aws_caller_identity.current.arn
}
