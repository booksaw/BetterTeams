---
sidebar_position: 100
---

# Maintainer Runbooks

Runbooks to ensure standard processes are followed during a release etc.

## Release checklist

Checklist of items to perform as a contributor before carrying out a release

### Prepare the release

- [ ] [Review all Issues marked as `inactive` or
  `bug` to ensure the release is complete](https://github.com/booksaw/BetterTeams/issues?q=is%3Aissue%20state%3Aopen%20(label%3Abug%20OR%20label%3Ainactive))
- [ ] Check #message-submissions in the discord to ensure all translations are up to date
- [ ] Review the CHANGELOG.md file to ensure all changes are included
- [ ] Ensure the jitpack build is passes for the latest commit

### Deploy the release

- [ ] Create the release in maven `mvn versions:set -DnewVersion="5.1.4" -DgenerateBackupPoms=false`
- [ ] Create a versioned release on GitHub with the changelog as the body
- [ ] Upload the release to spigot
- [ ] Notify the discord channel about the update
