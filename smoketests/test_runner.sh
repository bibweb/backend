#!/bin/sh
sed -i -E "s|host:[ ]*[\"\'][a-zA-Z0-9.\/:]*[\"\']|host: \'$HOST_URL\'|" common.yaml
cat common.yaml

py.test --junit-xml=out/results.xml