# HikingPal
Module 2 for CPEN 391

Notice:
   1. Try create one branch for each task
   2. Remember to delete the branch after it is merged to the master
   3. Open a new issue as long as you find it is necesary

About Map Background:
    Currently, we have the same map as background for each fragment. Later when we implement the new trail feature, we need to pass a new MapViewFragment to fragment_container, which overlays the original background.

Fragments:
- New Trail: Our main feature. 
    Start/continue/stop/save user's path.
    Allow user to discover nearby
    Allow user to put markers on whatever interests him

- View History: Second main feature. 
    View all saved path
    Allow user to select some trail
    Allow user to view the detailed info about the selected trail
    Allow user to remove the selected trail
    Allow user to fav the selected trail (so we can view this trail in "My Fav Trails" fragment)

- My Fav Trails
    View all faved trailes 
    Allow user to select some trail
    Allow user to view the detailed info about the selected trail

- Share
    Allow user to select a trail from the history
    Allow user to share the selected trail to other people via bluetooth, that is, other Android devices

- Send
    Allow user to select a trail from the history
    Allow user to send detailed information of the selected trail to DE2 Board (and display it on touchscreen)

WE NEED TO IMPLEMENT A LOCAL DATABASE FOR THE INFO LINKED TO EACH TRAIL!
