name: Bug report
description: Create a report to help us improve
title: "[BUG] "
labels: ["bug"]
assignees: 
- booksaw

body:
  - type: markdown
    attributes:
      value: |-
        Thank you for taking the time to create a Bug Report. 
        In order to process this bug report quickly please provide as much information as possible,
        If your bug report does not include enough detail to reproduce and fix the bug the delay in getting the bug fixed is much longer.       
        Before making a bug report, please ensure you have updated to the latest release of [BetterTeams](https://www.spigotmc.org/resources/better-teams.17129/)
   - type: textarea
     attributes:
       label: "What is the bug?"
       description: "Explain what bug you are encountering, try to explain in as much detail as possible"
    validations:
      required: true
   - type: textarea
     attributes:
       label: "Expected Behaviour"
       description: "What do you expect BetterTeams to do?"
     validations:
       required: true
   - type: textarea
     attributes: 
       label: "How to Reproduce"
       description: "List the steps required to reproduce the bug"
       placeholder: |-
         1. Run command...
         2. ...
         3. Something happens
       validations:
         required: true
  - type: textarea
    attributes: 
      label: "Error message"
      description: "Paste any error messages in the console here. **NOTE: Do not use pastebin/hastebin etc., please just paste the error directly**"
  - type: textarea
    attributes: 
      label: "Output of /teama version" 
      description: "Paste the output of /teama veresion here"
    validations:
      required: true
  - type: textarea
    attributes:
      label: "Additional Information"
      description: "Add any extra information you think has been missed" 
