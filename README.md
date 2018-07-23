This is Calendar API developed for education purposes - Please do not use for production.

1. Clone the repo using git clone or download a zipped file from github
2. It is a maven project, so create a maven build script or run mvn clean install -DskipTests to download the dependencies.
3. Run using java -jar <jar file will be created in the target directory in your project structrue)
4. Once the application is up, access http://localhost:8899/swagger-ui.html to test the application.

examples:
To create a calendar with two events.
{
	"name": "ABC",
	"events": [
		{
			"title":"Meeting2",
			"eventDate":"05/23/2018 08:00:00",
			"location": "Rome",
			"attendeeList": ["Tom","Harry"]
		},
		{
			"title":"RealMeeting",
			"eventDate":"05/23/2018 18:00:00",
			"location": "India",
			"attendeeList": ["Two","Three"]
		}
	]
}
- This will return a response, grab the ID's for Calendar and events and communicate with the API via swagger.

