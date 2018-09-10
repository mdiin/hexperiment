module "vault" {
  source = "github.com/GoogleCloudPlatform/terraform-google-vault"
  project_id           = "${var.project_id}"
  region               = "${var.region}"
  zone                 = "${var.zone}"
  storage_bucket       = "${var.storage_bucket}"
  kms_keyring_name     = "${var.kms_keyring_name}"
  vault_version        = "0.11.1"
}
