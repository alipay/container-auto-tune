#!/usr/bin/env sh
chmod +x  /tmp/minio-server/minio

/tmp/minio-server/minio server /tmp/minio-db --address :9098 --console-address :9099
