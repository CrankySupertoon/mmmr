required knowlegde: programming in Java and using IDEs (Integrated Development Environment)

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