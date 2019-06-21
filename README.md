# impart

 Android Based Network Scanning and Socket Based Communication Libarary
 Feature List:
 1. Scan the whole network you are connected with (LAN/WIFI)
 2. Returns a list of devices with their information available on the network
 3. You can also create a socket connection and impart itself manages the connections
 
 #Usage
 1. Create Configurations for socket server
 		
 		config=Config.newConfig()
		.setCallback(this) .  // message, clients and other info callbacks
		.setMaxClients(20) . // maximum allowed clients
		.setPort(3838) . // Server Port 
		.setPingClients(false) . // Auto Ping Clients to see if they are alive?
 2. Start Server
 
 		Impart.instance().start(config)
 3. Create Client & Send Message		
 
	        Impart.instance().createClient("hostName", 3421, true)
        	Impart.instance().sendMessage("hostname:port", "message")
 3. Scan Network for other devices		
 
	        NetworkScanner.getInstance().startScan(this, this, ""/*Get Your Device Ip and place here*/);
 
 
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
  
