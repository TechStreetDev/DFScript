[![Build](https://github.com/DFScripting/DFScript/actions/workflows/build.yml/badge.svg)
](https://github.com/DFScripting/DFScript/actions/workflows/build.yml)
# DFScript
DFScript is a mod created for DiamondFire to allow scripting on the client. This mod is a seperation and improvement from the shutdown mod [CodeUtilities](https://github.com/CodeUtilities/CodeUtilities).

- You can download DFScript from its [Github page](https://github.com/DFScripting/DFScript/releases/latest), or its [Modrinth page](https://modrinth.com/mod/dfscript).
- You can report issues on [the issues tab](https://github.com/DFScripting/DFScript/issues)

- You can join our [discord here](https://discord.gg/gtfFwWEapx)

## About
DFScript adds the ability to add new features through the added 
'scripting' ability. Scripting is constructing event based code (similar to DiamondFire) inside a web ui, with the events and actions being simple client-based activities. 

For example, a script could play a sound when the player receives a message with their name in it, or automatically add the @ character in front of sent messages.
___

To access the DFScript menu, run `/dfscript`, and a menu will appear showing many different options, you can then press **Installed Scripts** to view scripts you currently have installed, here you can toggle scripts on and off or press **Dashboard** to visit the scripting dashboard and create/install new scripts.
*(The same many can be accessed on the multiplayer connection screen)* 

To install a script you can locate the one you wish to install via the [public directory](https://dfscript.techstreet.dev/public/) and then press **Install Script** the script will then automatically install on your client and be enabled.

## Creating Scripts
To create a new script, access the [dashboard](https://dfscript.techstreet.dev/) from here you can press the **New Script** button, and then name and version your script, it will be created and displayed in the main menu. From here, you can edit, upload and delete. 

## Disabling Scripts - !!
Sometimes when you are creating a script, you may accidentally make your client complete actions which are against DiamondFire rules, such as making your client spam, or other similar actions. If this is the case, immediately leave the server. In the top left if your multiplayer screen, there will be a button button (![Scripts icon](https://i.imgur.com/Q2phpZz.png)) which will open up a version of the DFScript menu where scripts can be disabled. When you rejoin the server the script will now be disabled.

If the issue came from a script you did not make, report the script in the [discord](https://discord.gg/gtfFwWEapx) so it can be removed.

## Commands
All DFScript commands begin with `/dfscript`. 
- `/dfscript` Opens the main DFScript menu.
- `/dfscript recursion (number)` Changes the number of events that can trigger in a single tick.
- `/dfscript reload` Downloads and reloads all of the scripts on your machine. 
- `/dfscript vars (script name)` Lists the values of all the variables in the selected script. Similar to DiamondFire's /p vars command.
___

<p>
<img src="https://img.shields.io/github/downloads/DFScripting/DFScript/total?color=blue"/>
<img src="https://img.shields.io/github/languages/code-size/DFScripting/DFScript"/>
  <img src="https://img.shields.io/github/contributors/DFScripting/DFScript" />
  <img src="https://img.shields.io/github/release-date/DFScripting/DFScript" /> 
 </p>
