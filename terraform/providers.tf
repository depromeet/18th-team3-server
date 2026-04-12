terraform {
  required_version = ">= 1.6"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

# -----------------------------------------------------------------------------
# AWS Provider
#
# 현재는 AWS 계정이 없어서 mock 자격증명으로 로컬 validate/plan 만 수행한다.
# 실제 계정이 준비되면 아래 3개의 skip_* 옵션과 mock key 를 제거하고
# `aws configure` 또는 환경변수(AWS_PROFILE, AWS_ACCESS_KEY_ID 등)로 교체한다.
# -----------------------------------------------------------------------------
provider "aws" {
  region = var.aws_region

  # TODO: 실제 계정 준비 시 아래 블록 전체 삭제
  access_key                  = "mock_access_key"
  secret_key                  = "mock_secret_key"
  skip_credentials_validation = true
  skip_requesting_account_id  = true
  skip_metadata_api_check     = true

  default_tags {
    tags = {
      Project     = var.project
      Environment = var.environment
      ManagedBy   = "terraform"
    }
  }
}
