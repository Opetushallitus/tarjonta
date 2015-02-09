#!/bin/bash

BASE_DIR=`dirname $0`

echo ""
echo "Starting Karma Server (http://karma-runner.github.io)"
echo "-------------------------------------------------------------------"

cd $BASE_DIR/..
npm install
node_modules/karma/bin/karma start config/karma.conf.js $*
cd $BASE_DIR
