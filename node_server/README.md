# Server inf

## Index.js
	routing.json - within the node directory, create a file called test. Copy the below json code. To launch a remote copy off ukko or akka, simply replace the url & change the port to any open port. This allows for an easy launch of a local server for testing, while allowing everyone to connect to the same database.
```
{
  "url": "http://localhost:",
  "port": "3000"
}
```
	Mongo connection: be sure to change the mongo server's url & port address on line 16

## Mongoose
	 for a faster and easier time working with mongoDB, we used mongoose and its schema to easily work with mongodb. Each schema is defined to interact with a particular database.
	 User.js: handles authentication and password hashing via bcrypt
	 Technical.js & flash.js: simple schema with the mongoose-simple-random plugin to work with .findOneRandom() to pull random questions to the app.

## Question Website
	A very simple website was created to make adding questions to the databases easy. Currently, it supports a few views
	Current Issues: Despite passwords being encrypted and stored, the website connection is not secure. This should not be a problem so long as users do not use important passwords, as they do tisk (likely a low risk) the possibility of the password being intercepted.
	Home - contains a login and registration page
	Profile - profile access page displays user info & provides navigation to question adding pages
	addFlash & addTechnical - a simple flashcard and technical (seperate but the same) submission page, records user id with every submission.


## How to register
		Currently registration is set up for only admin access, thus an additional field of the verificationCode must be given, its current answer is "Questioneers" which is on line 78 of index.js. This also sets the registry to ADMIN level access.
		It should be changed to better suit the later needs, if more types of users are needed.

## Pug View Enginer
	To create a simple website

## Mongo Server
	Port: 42222
	Collections
		technicalQuestions, flashQuestions, users
			note: users stores the passwords in encrypted form
	Access quickly with RoboT for easy management

## Potential to-do tasks
	1. Secure website connection: currently the website deosn't really need to launch a secure version, but it probably should be.
	2. Refractor server code