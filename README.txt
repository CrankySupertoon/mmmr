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
it will download all libraries that MMMR uses to 

the project is now ready to use