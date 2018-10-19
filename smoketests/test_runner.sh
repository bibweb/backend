#!/bin/sh
sed -i -E "s|host:[ ]*[\"\'][a-zA-Z0-9.\/:]*[\"\']|host: \'$HOST_URL\'|" common.yaml

py.test -v --junit-xml=out/results.xml