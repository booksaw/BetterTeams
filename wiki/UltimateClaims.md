## [UltimateClaims](https://songoda.com/marketplace/product/ultimateclaims-the-ultimate-claiming-plugin.65)

_Click on UltimateClaims to go to the plugin page_

Claims with ultimate claims are changed from per player to instead being per team. This means that only team owners can
claim land for their team and that all members of the team are trusted in that claim.

* Other players who are not in the claiming team can be trusted on an individual basis.

### Disabling the integration

If you do not want this functionality with UltimateClaims you can disable it in the config near the bottom:

```yaml
# INTEGRATION WITH ULTIMATECLAIMS 
# Plugin link: https://songoda.com/marketplace/product/ultimateclaims-the-ultimate-claiming-plugin.65
ultimateClaims:
   # This is used to determine if the utlimateclaims integration is active 
   # If it is, only team owners will be able to make claims
   # All members of a team are trusted in the claim
   enabled: false
```

Just set enabled to false and restart your server and BetterTeams will not interfere with UltimateClaims