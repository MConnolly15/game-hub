# Terraform

Use these commands from inside the `terraform` folder.

## Format Terraform files

```bash
terraform fmt -recursive
```

Use this to automatically format all Terraform files.

## Check Terraform formatting

```bash
terraform fmt -check -recursive
```

Use this to check that all Terraform files are formatted correctly.

## Initialise Terraform

```bash
terraform init
```

Use this when setting up Terraform for the first time or after changing providers.

## Validate Terraform

```bash
terraform validate
```

Use this to check that the Terraform configuration is valid.

## Preview changes

```bash
terraform plan
```

Use this to see what Terraform will change before applying anything.

## Apply changes

```bash
terraform apply
```

Use this to apply the Terraform changes to your AWS environment.
