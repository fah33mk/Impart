# impart

 Android Based Network Scanning and Socket Based Communication Libarary
 Feature List:
 1. Scan the whole network you are connected with (LAN/WIFI)
 2. Returns a list of devices with their information available on the network
 3. You can also create a socket connection and impart itself manages the connections
 
 
 # Please help us improve this lib

How to include: [![](https://jitpack.io/v/fah33mk/impart.svg)](https://jitpack.io/#fah33mk/impart)

Project Level build.gradle:

allprojects {

	repositories {
  
	maven { url 'https://jitpack.io' }
  
	}
  
}

app level build.gradle :
dependencies {
	implementation 'com.github.fah33mk:impart:v0.1'
}
  
