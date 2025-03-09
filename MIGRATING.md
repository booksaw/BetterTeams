## 4.12.0

### Breaking Changes

#### Event Deprecation

* `PrePurgeEvent` has been deprecated and will be removed in the future. Please migrate to the new `PurgeEvent` class.
* `TeamPreMessageEvent` has been deprecated and will be removed in the future. Please migrate to the new
  `TeamSendMessageEvent` class.
* `TeamMessageEvent` has been deprecated and will be removed in the future. Please migrate to the new
  `PostTeamSendMessageEvent` class.

#### How to migrate

1. **Update imports**:
   ```java
   // Old code
   import com.booksaw.betterTeams.customEvents.PrePurgeEvent;
   import com.booksaw.betterTeams.customEvents.TeamPreMessageEvent;
   import com.booksaw.betterTeams.customEvents.TeamMessageEvent;
   
   // New code
   import com.booksaw.betterTeams.customEvents.PurgeEvent;
   import com.booksaw.betterTeams.customEvents.TeamSendMessageEvent;
   import com.booksaw.betterTeams.customEvents.post.PostTeamSendMessageEvent;
   ```

2. **Update event handlers**:
   ```java
   // Old code
   public void onPrePurge(PrePurgeEvent event) {
       // handler logic
   }
   
   // New code
   public void onPurge(PurgeEvent event) {
       // same handler logic
   }
   
   [ .. etc etc : same for the other events ]
   ```
