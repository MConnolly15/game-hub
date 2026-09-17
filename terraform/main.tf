data "aws_caller_identity" "current" {
}

data "aws_vpc" "default" {
  default = true
}

data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}

locals {
  is_main     = var.branch_name == "main"
  branch_slug = lower(replace(var.branch_name, "/[^a-zA-Z0-9]+/", "-"))

  resource_prefix = local.is_main ? (
    "${var.project_name}-${var.environment}"
    ) : (
    "${var.project_name}-${var.environment}-${local.branch_slug}"
  )
}