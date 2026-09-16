variable "aws_region" {
  description = "AWS Region for github actions"
  type        = string
  default     = "eu-west-2"
}

variable "environment" {
  description = "Environment name"
  type        = string
  default     = "dev"
}

variable "project_name" {
  description = "Project Name"
  type        = string
  default     = "game-hive"
}

variable "branch_name" {
  description = "branch name"
  type        = string
  default     = "main"
}

variable "ec2_ssh_public_key" {
  description = "public ssh key"
  type        = string
}

variable "app_port" {
  description = "port for app to listen on"
  type        = number
  default     = 8080
}

variable "db_engine_version" {
  description = "postgres engine version"
  type        = string
  default     = "16.4"
}

variable "db_instance_class" {
  description = "RDS Instance Class"
  type        = string
  default     = "db.t3.micro"
}

variable "db_allocated_storage" {
  description = "RDS Storage Amount in GB"
  type        = number
  default     = 20
}

variable "db_storage_type" {
  description = "RDS Storage Type"
  type        = string
  default     = "gp3"
}

variable "db_master_username" {
  description = "RDS Master Username"
  type        = string
  default     = "postgres"
}

variable "db_name" {
  description = "Database Name"
  type        = string
  default     = "game_hub_dev"
}

variable "db_main_publicly_accessible" {
  description = "Checks if the main database is accessible"
  type        = bool
  default     = false
}

variable "db_main_multi_az" {
  description = "check if the main database is Multi-AZ"
  type        = bool
  default     = false
}