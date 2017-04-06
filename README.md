# HikingPal - Module 2 for CPEN 391

Feature List:
### Android Side:
      Path Track:
            - New Trail: Our main feature
                Start/stop/save user's path.
                Allow user to discover nearby
                Allow user to add any interesting spots' names to the (current) MapImage object

            - View History: Second main feature 
                View all saved path
                Allow user to select some specific image and send corresponding data to the DE2
                Allow user to view the corresponding data on the touch screen
                Allow user to remove some specific trail
                Allow user to send all info to the DE2 and display each image as a button on touch screen

      Communicate:
            - Group Chat
                Allow user to send message to DE2 Board
                Allow user to receive message sent from DE2 Board
                Allow user to save all messages in local database
                Brings up the message notification if the DE2 sends any message
                Allow user to delete all messages

            - Announcement
               Allow user to receive announcement from the DE2 Board
               Allow user to save all announcement in local database
               Allow user to delete all announcement
 
### DE2 Side:
   Components: DE2 Board, Touch screen, SD Card, Blue tooth (Swiches, Buttons, LCD, Virtual Keyboard, Graphics)
      - Generally:
            Send the image id to the Android side, and the matched MapImage pops up no matter which fragment the user is currently on
            Display the current coordinates on LCD
            Read Weather icons from the SD Card and display them on the touchscreen correspondingly
            
      - For New Trail Fragment:
            Allow user to rate the current path
      - For View History Fragment:
            Allow user to send corresponding info to the touch screen (all images/selected images)
      - For the Chat Fragment: bi-direction communication
            A keyboard has been implemented to send message to the Android side
            Be able to receive the message sent from Android
      - For the Announcement: DE2 Board -> Android, one direction communication
            Be able to send announcement to the Android side
            
      
            
