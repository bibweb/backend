#!/bin/sh
sed -i -E "s|host:[ ]*[\"\'][a-zA-Z0-9.\/:]*[\"\']|host: \'$HOST_URL\'|" common.yaml
sed -i -E "s|testuser:[ ]*[\"\'][a-zA-Z0-9.\/:]*[\"\']|testuser: \'$TEST_USER\'|" common.yaml
sed -i -E "s|testpwd:[ ]*[\"\'][a-zA-Z0-9.\/:]*[\"\']|testpwd: \'$TEST_PWD\'|" common.yaml

py.test -v