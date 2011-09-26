

FOR USERS
---------
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
  * supports JVM 1.7 and 1.6, 64bit and 32bit versions
  * JVM 1.7 is sometimes required if you use mods that are compiled to only work with JVM 1.7
  * JVM 64bit is required to use more than ~1.5GB RAM
- manages different OptiFine installations, quickly switch between options or remove it
- install and uninstall mods, warns about file conflicts (75% done)
- reorder mods to fix conflicts between compatible mods, or remove one of both if impossible (25% done)
- backup and restore world and character data and keyboard setting, includes load order list for reference (50% done)
- HQ font for Minecraft (needs OptiFine installed or MCPatcher run manually) (75% done)
- check if mod is updated (only MinecraftForum)
- visit mod site links to check updates, information
- create mod install configurations with wizard based on existing or create new ones (0% done)
- create merged patches (to fix incompatible mods) with wizard and install them as mod (0% done)
- useful links (0% done)
- mail bugs, feature-request and new and updated mod install configurations to include in MMMR (0% done)
- help webpages available in MMMR (0% done)
 
 
 
 
 
 
 
 
FOR DEVELOPERS
--------------
- LGPL, open source, transparent
- use of open source libraries using equal or comparable licenses
- uses Eclipse, Subversion, Maven development tools all commonly used
- uses JAXB (xml), Hibernate and Derby (database) for object persistence
- MMMR source commit permission can be obtained
- Windows focused development
- support other OS (0% done)
  * sevenzipbinding
  * shell scripts: start MMMR/Minecraft with/without console and Maven scripts
  * Windows registry querying is used to find out what JDK/JRE are installed, find alternative for other OS's
- mailing list
- Maven site generation gives a LOT of info about the project





required knowledge: programming in Java and using IDEs (Integrated Development Environment)



if you have a Windows 64 bit version download the 64bit versions of software



download Slick SVN 64bit/32bit
http://www.sliksvn.com/en/download

download "Java Development Kit" (JDK) version 1.7 64bit/32bit [JDK includes JRE]: http://www.oracle.com/technetwork/java/javase/downloads/index.html

download "Eclipse IDE for Java Developers" (Indigo or higher) 64bit/32bit
http://www.eclipse.org/downloads/
it comes with Maven integration

menu 'Help', 'Eclipse Marketplace', search/install 'Subclipse'

close and restart Eclipse if you didn't do already so all new installed software can be loaded

menu 'File', 'New', 'Other', 'SVN', 'Checkout Projects from SVN', 'Create a new repository location', input 'http://mmmr.googlecode.com/svn/trunk/' as 'Url' and finish, if you have commit rights use 'https://mmmr.googlecode.com/svn/trunk/' instead
whenever you want to update the project, rightclick and under 'Team', choose 'Update to HEAD'

rightclick the project and choose 'Configure', 'Convert to Maven Project'
it will download all libraries that MMMR uses to a directory called '.m2' under you Windows user home directory

if you installed more than 1 JDK/JRE: menu 'Window', 'Preferences', 'Java', 'Installed JRE's' and chech if the default selected options is the JDK you just installed, when not: click 'Add', 'Standard JVM', click 'Directory' and browse to 'C:\Program Files\Java\jdk1.7.0' and check it, you can remove the other ones

the project is now ready to use



other installed software: menu 'Help', 'Install New Software', click 'Add' to add update sites:

use Eclipse Mylyl to connect to Google code Issues (use 'Task Repositories' and 'Tasks List' tab): http://code.google.com/p/googlecode-mylyn-connector/
update site: http://knittig.de/googlecode-mylyn-connector/update/

decompile classes when you don't have the code with JD Eclipse: http://java.decompiler.free.fr/?q=jdeclipse
update site: http://java.decompiler.free.fr/jd-eclipse/update

use Jakarta Commons in Eclipse more easily with Commons4E: http://developer.berlios.de/projects/commons4e/
update site: http://commons4e.berlios.de/updatesite31

