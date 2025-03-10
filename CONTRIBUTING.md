# Contributing to BetterTeams

Reading and following these guidelines will help us make the contribution process easy and effective for everyone
involved. It also communicates that you agree to respect the time of the developers managing and developing these open
source projects. In return, we will reciprocate that respect by addressing your issue, assessing changes, and helping
you finalize your pull requests.

## Quicklinks

* [Code of Conduct](#code-of-conduct)
* [Getting Started](#getting-started)
    * [Issues](#issues)
    * [Pull Requests](#pull-requests)
    * [Setting Up](#Setting-up)
* [Getting Help](#getting-help)

## Code of Conduct

We take our open source community seriously and hold ourselves and other contributors to high standards of
communication. By participating and contributing to this project, you agree to uphold
our [Code of Conduct](https://github.com/booksaw/BetterTeams/blob/master/CODE_OF_CONDUCT.md).

## Getting Started

Contributions are made to this repo via Issues and Pull Requests (PRs). A few general guidelines that cover both:

- Search for existing Issues and PRs before creating your own.
- We work hard to makes sure issues are handled in a timely manner but, depending on the impact, it could take a while
  to investigate the root cause. A friendly ping in the comment thread to the submitter or a contributor can help draw
  attention if your issue is blocking.

### Issues

Issues should be used to report problems with the library, request a new feature, or to discuss potential changes before
a PR is created. When you create a new Issue, a template will be loaded that will guide you through collecting and
providing the information we need to investigate.

If you find an Issue that addresses the problem you're having, please add your own reproduction information to the
existing issue rather than creating a new one. Adding
a [reaction](https://github.blog/2016-03-10-add-reactions-to-pull-requests-issues-and-comments/) can also help be
indicating to our maintainers that a particular problem is affecting more than just the reporter.

### Pull Requests

PRs to our libraries are always welcome and can be a quick way to get your fix or improvement slated for the next
release. In general, PRs should:

- Only fix/add the functionality in question **OR** address wide-spread whitespace/style issues, not both.
- Add unit or integration tests for fixed or changed functionality (if a test suite already exists).
- Address a single concern in the least number of changed lines as possible.
- Include documentation in the repo.
- Be accompanied by a complete Pull Request template (loaded automatically when a PR is created).

For changes that address core functionality or would require breaking changes (e.g. a major release), it's best to open
an Issue to discuss your proposal first. This is not required but can save time creating and reviewing changes.

1. Create an issue detailing the proposed changes (either a bug report or feature request)
2. Mention in the issue you are going to be coding for that you are taking the issue on so multiple people dont code a
   solution for the same issue
3. Fork the repository to your own Github account
4. Clone the project to your machine
5. Create a branch locally with a succinct but descriptive name
6. Commit changes to the branch
7. Following any formatting and testing guidelines specific to this repo
8. Push changes to your fork
9. Open a PR in our repository and follow the PR template so that we can efficiently review the changes.

## Setting up

Setting up of this project is relativly simple due to the usage of maven, all but two required repos will are included
in the pom.xml

The following dependencies are setup in a way which does not allow for a maven dependency, either due to not setting up
releases on github or not making their code open source, for both repositories you will have to add a build path to the
jar file for that repo.

If the section of the project you are working on does not require any of the following plugins, you should be able to
build the plugin without downloading the corresponding jar files.

Missing dependencies:

* UltimateClaims
* zKoth

See the [dependencies page](https://github.com/booksaw/BetterTeams/wiki/Dependencies) of the wiki for more information
on the dependencies.

## Getting Help

Join us on [discord](https://discord.gg/JF9DNs3) and post your question there in the correct channel.
