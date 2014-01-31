#!/bin/bash

mkdir -pv ~/.m2
cat > ~/.m2/settings.xml << "EOF"
<?xml version="1.0" encoding="UTF-8"?>
<settings xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.1.0 http://maven.apache.org/xsd/settings-1.1.0.xsd" xmlns="http://maven.apache.org/SETTINGS/1.1.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <servers>
    <server>
      <username>BINTRAY_USER</username>
      <password>BINTRAY_PASS</password>
      <id>central</id>
    </server>
    <server>
      <username>BINTRAY_USER</username>
      <password>BINTRAY_PASS</password>
      <id>snapshots</id>
    </server>
  </servers>
  <profiles>
    <profile>
      <repositories>
        <repository>
          <snapshots>
            <enabled>false</enabled>
          </snapshots>
          <id>central</id>
          <name>libs-release</name>
          <url>http://oss.jfrog.org/artifactory/libs-release</url>
        </repository>
        <repository>
          <snapshots />
          <id>snapshots</id>
          <name>libs-snapshot</name>
          <url>http://oss.jfrog.org/artifactory/libs-snapshot</url>
        </repository>
      </repositories>
      <pluginRepositories>
        <pluginRepository>
          <snapshots>
            <enabled>false</enabled>
          </snapshots>
          <id>central</id>
          <name>plugins-release</name>
          <url>http://oss.jfrog.org/artifactory/plugins-release</url>
        </pluginRepository>
        <pluginRepository>
          <snapshots />
          <id>snapshots</id>
          <name>plugins-snapshot</name>
          <url>http://oss.jfrog.org/artifactory/plugins-snapshot</url>
        </pluginRepository>
      </pluginRepositories>
      <id>artifactory</id>
    </profile>
  </profiles>
  <activeProfiles>
    <activeProfile>artifactory</activeProfile>
  </activeProfiles>
</settings>
EOF

sed -i s/BINTRAY_USER/$BINTRAY_USER/ ~/.m2/settings.xml
sed -i s/BINTRAY_PASS/$BINTRAY_PASS/ ~/.m2/settings.xml
