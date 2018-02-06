# philips hue spotify connector

This piece of software will match your philips hue lights to the album cover of the current song playing in your spotify account.

# Usage
* Configure Spotify and Phillips Hue Bridge
* Open your browser and go to http://localhost:3000
* Click `Connect Spotify` to authenticate the service
* Let the light matching begin

# Configuration
* Create a spotify api client at https://beta.developer.spotify.com/dashboard/applications.
	* Make sure to set the correct redirectUri for the service. If you're going default use `http://localhost:3000/`
* Copy the client id and client secre
* Use the script `connectHueBridge.sh` to aquire a brige username or use the web-ui once you started everything.
* Set clientId, clientSecret, bridge username and ip in the docker-compose file or pass the environment variables to the service if you start it directly.

# Run (from docker registry)
* Use the docker-compose file located in `compose/x86`
* Run `docker-compose up -d`

# Run on RaspberryPi (from docker registry)
* Copy the docker-compose file located in `compose/arm` to your RaspberryPi
* Run `docker-compose up -d`

# Run in development (with build)
* Use the docker-compose file located in the project root
* Run `docker-compose -f docker-compose.yml up --build`