#!/usr/bin/env bash
git checkout develop
mvn gitflow:release-start && \
 conventional-changelog -p angular -i CHANGELOG.md -s -r 0 && \
 git add CHANGELOG.md && \
 git commit -m "Changelog" && \
 mvn gitflow:release-finish -DnoDeploy=true && \
 git push origin master && git push --tags && \
 git checkout develop
