

FOR USERS
---------

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
- install and uninstall mods, warns about file conflicts (75% done)
- HQ font for Minecraft (needs OptiFine installed or MCPatcher run manually)

FEATURES IN PROGRESS
--------------------

- reorder mods to fix conflicts between compatible mods, or remove one of both if impossible (25% done)
- backup and restore world and character data and keyboard setting, includes load order list for reference (50% done)
- check if mod is updated (only MinecraftForum)
- visit mod site links to check updates, information
- create mod install configurations with wizard based on existing or create new ones (0% done)
- create merged patches (to fix incompatible mods) with wizard and install them as mod (0% done)
- useful links (0% done)
- mail bugs, feature-request and new and updated mod install configurations to include in MMMR (0% done)
- help webpages available in MMMR (0% done)







FOR DEVELOPERS
--------------

FEATURES
--------

- Google Code: http://mmmr.googlecode.com
- Google Group, Mailing List: http://groups.google.com/group/mmmrlist , mmmrlist@googlegroups.com
- LGPL, open source, transparent
- use of open source libraries using equal or comparable licenses
- uses Eclipse, Subversion, Maven development tools which are commonly used
- uses JAXB (xml), Hibernate and Derby (database) for object persistence
- MMMR source commit permission can be obtained
- Windows focused development
- support other OS (0% done)
  * sevenzipbinding
  * shell scripts: start MMMR/Minecraft with/without console and Maven scripts
  * Windows registry querying is used to find out what JDK/JRE are installed, find alternative for other OS's
- mailing list (see Maven site genaration)
- Maven site generation gives a LOT of info about the project

START DEVELOPMENT
-----------------

WARNING: required knowledge: programming in Java and using IDEs (Integrated Development Environment)
WARNING: if you have a Windows 64 bit version download the 64bit versions of software

1) download "Java Development Kit" (JDK) version 1.7 64bit/32bit [JDK includes JRE]: http://www.oracle.com/technetwork/java/javase/downloads/index.html

2) download Slick SVN 64bit/32bit
http://www.sliksvn.com/en/download

3) download "Eclipse IDE for Java Developers" (Indigo or higher) 64bit/32bit
http://www.eclipse.org/downloads/
it comes with Maven integration

4) menu 'Help', 'Eclipse Marketplace', search/install 'Subclipse'

5) close and restart Eclipse so all new installed software can be loaded

6) menu 'File', 'New', 'Other', 'SVN', 'Checkout Projects from SVN', 'Create a new repository location', input 'http://mmmr.googlecode.com/svn/trunk/' as 'Url' and finish, if you have commit rights use 'https://mmmr.googlecode.com/svn/trunk/' instead
whenever you want to update the project, rightclick and under 'Team', choose 'Update to HEAD'

7) rightclick the project and choose 'Configure', 'Convert to Maven Project'
it will download all libraries that MMMR uses to a directory called '.m2' under you Windows user home directory

8) if you installed more than 1 JDK/JRE: menu 'Window', 'Preferences', 'Java', 'Installed JRE's' and chech if the default selected options is the JDK you just installed, when not: click 'Add', 'Standard JVM', click 'Directory' and browse to 'C:\Program Files\Java\jdk1.7.0' and check it, you can remove the other ones

9) the project is now ready to use

EXTRA
-----

other installed software: menu 'Help', 'Install New Software', click 'Add' to add update sites:

1) use Eclipse Mylyl to connect to MMMR Google Code Issue list (use 'Task Repositories' and 'Tasks List' tab): http://code.google.com/p/googlecode-mylyn-connector/
update site: http://knittig.de/googlecode-mylyn-connector/update/

2) decompile classes when you don't have the code with JD Eclipse: http://java.decompiler.free.fr/?q=jdeclipse
update site: http://java.decompiler.free.fr/jd-eclipse/update

3) use Jakarta Commons in Eclipse more easily with Commons4E: http://developer.berlios.de/projects/commons4e/
update site: http://commons4e.berlios.de/updatesite31

TODO
----

- move glazedlist extension to new project and include as library (probably hosted on Google Code)
- see TODO and FIXME in code (Eclipse Task tab)
- integration of other Minecraft user and developer tools if permission is granted
- follow up Google Code Issues (bugs and feature request) (Eclipse Task Repositories & Task List tab)
- follow up updating mod install configurations send by users (mailing list)
- localization to other languages (like messages_en_UK.properties, default is English)


