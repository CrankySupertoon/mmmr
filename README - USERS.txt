COMPLETED FEATURES
------------------

- user needs to do as less as possible outside MMMR
- MMMR updates itself automatically
- downloads everything, except mod archives, automatically; this includes:
  * libraries, fonts, etc and other resources MMMR uses
  * Minecraft client and server executables
  * mod installation info (xml files)
  * start first run Minecraft from within MMMR
- backup of Minecraft jar
- prepare Minecraft jar for modding (convert to directory, delete META-INF)
- optional: start with YogBox as base modpack, starts installation from within MMMR, backup Minecraft jar after installation
- enables multiple installations of Minecraft by setting APPDATA on startup (bat files) so you can play with two version of Minecraft containing incompatible mods or different Minecraft versions
- performance configuration and selection of JVM (startup Minecraft with or without console):
  * requires Sun's/Oracle JVM
  * supports JVM version 1.7 and 1.6, both 64bit and 32bit versions, both JRE and JDK
  * JVM 1.7 is sometimes required if you use mods that are compiled to only work with JVM 1.7
  * JVM 64bit is required to use more than ~1.5GB RAM for Minecraft
- manages different OptiFine installations, quickly switch between options or remove it
- HQ font for Minecraft (requires OptiFine to be installed)
- installing mods
  * easy filtering between installed, (un)available [depends if archive is downloaded]
  * check if mod is updated (only MinecraftForum)
  * visit download site via column Link and DL when not checked
- visit mod site links to check updates, information
- favorite links (additional ones can be added by dragging a hyperlink from the browser to the 'data\links' directory)

FEATURES IN PROGRESS
--------------------

- install and uninstall mods, warns about file conflicts (85% done)
- reorder mods to fix conflicts between compatible mods, or remove one of both if impossible, drag&drop (25% done)

FEATURES NOT STARTED
--------------------

- backup and restore world and character data and keyboard setting, includes load order list for reference
- check mod install configurations (drag and drop) when a user creates one himself by hand
- create mod install configurations with wizard based on existing or create new ones
- create merged patches (to fix incompatible mods) with wizard and install them as mod
- mail bugs, feature-request and new and updated mod install configurations to include in MMMR
- help webpages available in MMMR
- support other OS
- mod install config description as html, trplace links if not encapsulated in '<a href'
- load mod install configurations also from subdirectories
- hide mods so you won't be bugged by mods that are incompatible with your favorite ones (and unhide)
- show different versions of the same mod as a single line with selectable version,
  select highest version by default, gray out when only a single version is available
- management of minecraft server
- management of minecraft server mods

SCRAPPED FEATURES OR FEATURES ON HOLD
-------------------------------------

- update LWJGL to latest version (http://www.minecraftwiki.net/wiki/Tutorials/Update_LWJGL)
  => upgrading seems to make MC unstable (F11 doesn't work) and slower

EXTRA
-----

MCPatcher: use this when OptiFine is not ready for the Minecraft version you are using and install it after you started up Minecraft for the very first time (after it downloaded the client files) before continuing in MMMR: http://www.minecraftforum.net/topic/232701-181-19pre3update-103-mcpatcher-hd-fix-212/

