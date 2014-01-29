#!/bin/bash

mkdir -pv ~/.m2
cat > ~/.m2/settings.xml << "EOF"
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                          http://maven.apache.org/xsd/settings-1.0.0.xsd">
<servers>
EOF
echo "<server>" >> ~/.m2/settings.xml
echo "<id>bintray-robxu9-SimpleSave-simplesave</id>" >> ~/.m2/settings.xml
echo "<username>$BINTRAY_USER</username>" >> ~/.m2/settings.xml
echo "<password>$BINTRAY_PASS</password>" >> ~/.m2/settings.xml
echo "</server>" >> ~/.m2/settings.xml
cat >> ~/.m2/settings.xml << "EOF"
</servers>
</settings>
