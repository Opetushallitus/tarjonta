#!/bin/sh
#update angular libs
version=1.2.7
files=`ls *.js`

echo files:$files

for file in $files;do
   wget "http://ajax.googleapis.com/ajax/libs/angularjs/$version/$file" -O $file
done;
echo $version > version.txt
