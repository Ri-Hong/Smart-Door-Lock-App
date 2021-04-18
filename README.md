# Smart-Door-Lock-App
**\*Note This app is the companion app of [Smart-Door-Lock](https://github.com/Ri-Hong/Smart-Door-Lock)**
## Description
The app consists of three fragments: the home fragment, the history fragment, and the settings fragment. 
The home fragment consists of a toggle button to let the user lock/unlock the door. When the door is unlocked, the red unlocked icon will appear and when the door is locked, the green locked icon will appear. 
The history fragment keeps track of everyone who has locked/unlocked the door and at what time. Whenever the door is locked or unlocked, the ESP32 figures out who the user is by their pin and sends that data to the app. The app then keeps track of the user's name, whether they locked or unlocked the door, and the time they locked/unlocked the door. 
The settings fragment allows the user to change their name and pin. This new information will be sent to the ESP32 once the user presses the save button.

<img src="https://github.com/Ri-Hong/Smart-Door-Lock/blob/main/Images/Home_Fragment_Unlocked.png" alt="Home Fragment Unlocked" width="200"/>
<img src="https://github.com/Ri-Hong/Smart-Door-Lock/blob/main/Images/Home_Fragment_Locked.png" alt="Home Fragment Locked" width="200"/>
<img src="https://github.com/Ri-Hong/Smart-Door-Lock/blob/main/Images/History_Fragment.png" alt="History Fragment" width="200"/>
<img src="https://github.com/Ri-Hong/Smart-Door-Lock/blob/main/Images/Settings_Fragment.png" alt="Settings Fragment" width="200"/>

Feel free to contact me at riri.hong@gmail.com if you have any questions!
