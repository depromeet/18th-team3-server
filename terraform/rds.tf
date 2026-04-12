# -----------------------------------------------------------------------------
# DB Subnet Group — RDS 는 최소 2개 AZ 의 서브넷이 필요하다
# -----------------------------------------------------------------------------
resource "aws_db_subnet_group" "main" {
  name       = "${local.name_prefix}-db-subnet-group"
  subnet_ids = aws_subnet.private[*].id

  tags = {
    Name = "${local.name_prefix}-db-subnet-group"
  }
}

# -----------------------------------------------------------------------------
# RDS MySQL 8.4 (LTS) — db.t4g.micro
# -----------------------------------------------------------------------------
resource "aws_db_instance" "mysql" {
  identifier     = "${local.name_prefix}-mysql"
  engine         = "mysql"
  engine_version = var.db_engine_version
  instance_class = var.db_instance_class

  allocated_storage     = var.db_allocated_storage
  max_allocated_storage = 100
  storage_type          = "gp3"
  storage_encrypted     = true

  db_name  = var.db_name
  username = var.db_username
  password = var.db_password
  port     = 3306

  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [aws_security_group.rds.id]
  publicly_accessible    = false
  multi_az               = false

  # EC2 와 동일 AZ 에 고정해서 cross-AZ 데이터 전송 요금을 피한다.
  # DB Subnet Group 은 2개 AZ 가 필요하지만 실제 인스턴스는 여기 한 곳만 사용.
  availability_zone = var.azs[0]

  backup_retention_period = 7
  backup_window           = "17:00-18:00" # KST 02:00-03:00
  maintenance_window      = "sun:18:00-sun:19:00"

  auto_minor_version_upgrade = true
  deletion_protection        = false # dev 단계에서는 false, prod 에서는 true 로 전환
  skip_final_snapshot        = true  # dev 단계에서만 true

  tags = {
    Name = "${local.name_prefix}-mysql"
  }
}
