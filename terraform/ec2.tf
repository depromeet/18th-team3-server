# -----------------------------------------------------------------------------
# Ubuntu 24.04 LTS (Noble Numbat) arm64 최신 AMI 조회
#
# t4g.* 는 Graviton(ARM) 이므로 반드시 arm64 AMI 를 써야 한다.
# owner 099720109477 은 Canonical 공식 계정.
#
# 이 data source 는 실제 AWS API 호출이 필요하므로 mock provider 로는
# plan 시점에 실패한다. 계정 연결 후에만 활성화할 것.
# -----------------------------------------------------------------------------
data "aws_ami" "ubuntu_2404_arm64" {
  most_recent = true
  owners      = ["099720109477"] # Canonical

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd-gp3/ubuntu-noble-24.04-arm64-server-*"]
  }

  filter {
    name   = "architecture"
    values = ["arm64"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}

resource "aws_instance" "app" {
  ami                    = data.aws_ami.ubuntu_2404_arm64.id
  instance_type          = var.ec2_instance_type
  subnet_id              = aws_subnet.public.id
  availability_zone      = var.azs[0]
  vpc_security_group_ids = [aws_security_group.ec2.id]

  # IMDSv2 강제 — 2019 Capital One 사태의 원인이었던 IMDSv1 SSRF 취약점 방어.
  # http_tokens = "required" 로 두면 메타데이터 조회 시 토큰 세션이 필수가 된다.
  metadata_options {
    http_endpoint               = "enabled"
    http_tokens                 = "required"
    http_put_response_hop_limit = 1
  }

  root_block_device {
    volume_size           = 20
    volume_type           = "gp3"
    encrypted             = true
    delete_on_termination = true
  }

  tags = {
    Name = "${local.name_prefix}-app"
  }
}
