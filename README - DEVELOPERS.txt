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
- support other OS
  * sevenzipbinding (available for other OS)
  * shell scripts: start MMMR/Minecraft with/without console and Maven scripts
  * Windows registry querying is used to find out what JDK/JRE are installed, find alternative for other OS's
- mailing list (see Maven site genaration)
- Maven site generation gives a LOT of info about the project

START DEVELOPMENT
-----------------

WARNING: required knowledge: programming in Java and using IDEs (Integrated Development Environment)
WARNING: if you have a Windows 64 bit version downloading the 64bit versions of software is recommended or required in a few cases

1) download "Java Development Kit" (JDK) version 1.7 64bit/32bit [JDK includes JRE]: http://www.oracle.com/technetwork/java/javase/downloads/index.html

2) download Slick SVN 64bit/32bit
http://www.sliksvn.com/en/download

3) download "Eclipse IDE for Java Developers" : Eclipse Helios (3.6.x) or Eclipse Indigo (3.7.x) or higher, 64bit/32bit
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

4) Maven (standalone) version 2.0.0 up to 2.2.1: http://maven.apache.org/

5) TortoisSVN, use SVN in the Windows explorer shell, always use 64 bit version if you have a Windows 64 version:  http://tortoisesvn.tigris.org/

TODO
----

- move glazedlist extension to new project and include as library (probably hosted on Google Code)
- see TODO and FIXME in code (Eclipse Task tab)
- integration of other Minecraft user and developer tools if permission is granted
- follow up Google Code Issues (bugs and feature request) (Eclipse Task Repositories & Task List tab)
- follow up updating mod install configurations send by users (mailing list)
- localization to other languages (like messages_fr.properties, default messages.properties is English)
- see 'FEATURES IN PROGRESS'
- start server en install mods in server
