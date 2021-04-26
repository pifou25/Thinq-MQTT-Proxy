#!/bin/bash

cat <<EOF >/etc/systemd/system/thinq-mqtt-proxy.service
[Unit]
Description=Thinq Mqtt Proxy Service
StartLimitIntervalSec=0
After=syslog.target network.target

[Service]
SuccessExitStatus=0 143
RestartSec=5
Restart=on-failure
TimeoutStopSec=120
Type=simple
User=root
WorkingDirectory=`pwd`
ExecStart=java -jar ./target/thinq-mqtt-proxy.jar run

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
systemctl enable thinq-mqtt-proxy
systemctl start thinq-mqtt-proxy
