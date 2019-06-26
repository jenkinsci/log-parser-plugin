# Release new version
```
git checkout develop
mvn jgitflow:release-start && \
 conventional-changelog -p angular -i CHANGELOG.md -s -r 0 && \
 git add CHANGELOG.md && \
 git commit -m "Changelog" && \
 mvn jgitflow:release-finish -DnoDeploy=true && \
 git push origin master && git push --tags && \
 git checkout develop
```

or just run `release.sh`
