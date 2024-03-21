#! /bin/bash

# Fail the script if any command fails
set -e

echo "======================== MOVING WEBAPP JAR ========================"
sudo mv /tmp/webapp.jar /opt/webapp.jar

echo "======================== CREATE USER ========================"
sudo adduser csye6225 --shell /usr/sbin/nologin
sudo chown csye6225:csye6225 /opt/webapp.jar

echo "======================== MOVING WEBAPP SERVICE ========================"
sudo mv /tmp/webapp.service /etc/systemd/system/webapp.service

echo "======================== INSTALLING JAVA ========================"
sudo rpm --import https://yum.corretto.aws/corretto.key
sudo curl -L -o /etc/yum.repos.d/corretto.repo https://yum.corretto.aws/corretto.repo
sudo yum install -y java-17-amazon-corretto-devel
java -version

echo "======================== ENABLING WEBAPP SERVICE ========================"
sudo systemctl daemon-reload
sudo systemctl enable webapp.service

echo "======================== INSTALLING OPS AGENT ========================"
curl -sSO https://dl.google.com/cloudagents/add-google-cloud-ops-agent-repo.sh
sudo bash add-google-cloud-ops-agent-repo.sh --also-install

echo "======================== MOVING OPS CONFIG AGENT ========================"
sudo mv /tmp/config.yaml /etc/google-cloud-ops-agent/config.yaml
sudo systemctl restart google-cloud-ops-agent

echo "======================== CREATE LOG FILE ========================"
sudo touch /var/log/webapp/webapp.log
sudo chown csye6225:csye6225 /var/log/webapp/webapp.log


#echo "======================== INSTALLING POSTGRESQL ========================"
#dnf module list postgresql
#sudo dnf module enable -y postgresql:16
#sudo dnf install -y postgresql-server
#
#echo "======================== INITIALIZING POSTGRES ========================"
#sudo postgresql-setup --initdb
#
#echo "======================== STARTING POSTGRES ========================"
#sudo systemctl start postgresql
#sudo systemctl enable postgresql
#
#echo "======================== CREATING DB ========================"
#sudo -u postgres createdb assignmentdb
## Setting password for postgres user
#sudo -u postgres psql -U postgres -d postgres -c "alter user postgres with password 'Logitech@135#';"
#
#echo "======================== CONFIGURING POSTGRES FOR PASSWORD ========================"
#sudo cp /var/lib/pgsql/data/pg_hba.conf /var/lib/pgsql/data/pg_hba.conf-backup
#PG_HBA_CONF="/var/lib/pgsql/data/pg_hba.conf"
#TMP_FILE=$(mktemp)
## changing the postgres config file
#sudo sed -e 's/peer/scram-sha-256/g' -e 's/ident/scram-sha-256/g' "$PG_HBA_CONF" > "$TMP_FILE"
#sudo cp "$TMP_FILE" "$PG_HBA_CONF"
#sudo rm "$TMP_FILE"

#echo "======================== RESTARTING POSTGRES ========================"
#sudo systemctl restart postgresql
