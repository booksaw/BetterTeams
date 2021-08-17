
name: Bug report
about: Create a report to help BetterTeams improve
title: "[BUG] <Bug Name>"
labels: bug
assignees: booksaw

body: 
  - type: markdown
    attributes:
      value: |
        Thank you for reporting a bug for BetterTeams, please fill out the following information for it to get fixed
  
  - type: input
    id: Description
    attributes:
      label: Describe the bug
      description: A clear and concise description of what the bug is.
    validations:
      required: true
  
  - type: textarea
    id: Reproduce
    attributes:
      label: How to reproduce
      description: The steps required to reproduce the behaviour
      placeholder: 1. run command ...
    validations:
      required: true
  
  - type: textarea
    id: Error
    attributes:
      label: Error message
      description: Any error messages in the console (do not use hastebin, just paste the error in plain)
    validations:
      required: false
  
  - type: input
    id: Version
    attributes:
      label: What version of BetterTeams are you using?
      description: Found by running /teama version (do not just put latest)
      placeholder: 4.0.0
    validations:
      required: true
  
  - type: input
    id: McVersion
    attributes:
      label: What version of spigot are you using?
      description: /version
      placeholder: 
    validations:
      required: true
  
  - type: input
    id: Additional
    attributes:
      label: Additional Context
      description: Add any other context about the problem here (ie screenshots)
    validations:
      required: false

