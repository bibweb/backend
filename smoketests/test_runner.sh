#!/bin/sh
sed -i -E "s|host:[ ]*[\"\'][a-zA-Z0-9.\/:]*[\"\']|host: \'$HOST_URL\'|" common.yaml
cat common.yaml

cat /etc/hosts

wget --no-check-certificate $HOST_URL/book

py.test -q --junit-xml=out/results.xml