data "aws_ami" "al2023" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-*-x86_64"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}

resource "aws_key_pair" "app" {
  count = local.is_main ? 0 : 1

  key_name   = local.resource_prefix
  public_key = var.ec2_ssh_public_key
}

resource "aws_instance" "app" {
  count = local.is_main ? 0 : 1

  ami                         = data.aws_ami.al2023.id
  instance_type               = "t3.micro"
  subnet_id                   = sort(data.aws_subnets.default.ids)[0]
  vpc_security_group_ids      = [aws_security_group.ec2[0].id]
  key_name                    = aws_key_pair.app[0].key_name
  iam_instance_profile        = aws_iam_instance_profile.app[0].name
  associate_public_ip_address = true

  root_block_device {
    volume_type = "gp3"
    volume_size = 30
  }

  user_data = <<-EOF
    #!/bin/bash
    set -euxo pipefail

    dnf install -y java-21-amazon-corretto

    useradd --system --no-create-home --shell /sbin/nologin gamehub || true

    mkdir -p /opt/gamehub
    chown gamehub:gamehub /opt/gamehub

    cat <<'UNIT' > /etc/systemd/system/gamehub.service
    [Unit]
    Description=Game Hub
    After=network.target

    [Service]
    User=gamehub
    Group=gamehub
    WorkingDirectory=/opt/gamehub
    EnvironmentFile=-/opt/gamehub/app.env
    ExecStart=/usr/bin/java -jar /opt/gamehub/app.jar
    Restart=on-failure

    [Install]
    WantedBy=multi-user.target
    UNIT

    systemctl daemon-reload
    systemctl enable gamehub
  EOF

  tags = {
    Name   = "${local.resource_prefix}-app"
    Branch = var.branch_name
  }
}