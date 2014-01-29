#!/bin/bash

mkdir -pv ~/.m2
echo "<server>" > ~/.m2/settings.xml
echo "<id>bintray-robxu9-SimpleSave-simplesave</id>" >> ~/.m2/settings.xml
echo "<username>$BINTRAY_USER</username>" >> ~/.m2/settings.xml
echo "<password>$BINTRAY_PASS</password>" >> ~/.m2/settings.xml
echo "</server>" >> ~/.m2/settings.xml

