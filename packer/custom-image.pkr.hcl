packer {
  required_plugins {
    googlecompute = {
      source  = "github.com/hashicorp/googlecompute"
      version = ">= 1"
    }
  }
}

variable "webapp_jar_path" {
  type    = string
  default = "./target"
}

variable "packer_path" {
  type    = string
  default = "./packer"
}

source "googlecompute" "autogenerated_1" {
  // account_file = "packer-svc.json"
  project_id   = "csye6225-omkar"
  source_image = "centos-stream-8-v20240110"
  ssh_username = "packer"
  zone         = "us-central1-a"
  image_name   = "centos-csye6225-{{timestamp}}"
  image_family = "centos-csye6225"
}

build {
  sources = ["source.googlecompute.autogenerated_1"]

  provisioner "file" {
    source      = "${var.webapp_jar_path}/webapp.jar"
    destination = "/tmp/webapp.jar"
  }

  provisioner "file" {
    source      = "${var.packer_path}/webapp.service"
    destination = "/tmp/webapp.service"
  }

  provisioner "shell" {
    scripts = ["${var.packer_path}/setup.sh"]
  }
}
