//Express-Session, Mongoose, and body-parser declarations
var express = require('express');
var app = express();
var mongoose = require('mongoose');
var session = require('express-session');
var bp = require('body-parser');


//Load routing data to launch local copy of server from routing.json
const fs = require('fs');
var route = fs.readFileSync('test/routing.json');
//var route = fs.readFileSync('test/test1.json'); /*testport for kottk055*/
var jsonRoute = JSON.parse(route);

//connecting mongoose to RestAPI, target URL stored in route
mongoose.connect('mongodb://ukko.d.umn.edu:29805/AppNull');
var db = mongoose.connection

//handles mongo connection error
db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function () {
  console.log("Connection established to" + "mongodb://ukko.d.umn.edu:29805/AppNull");
});

// Mongoose schema declartion
var technical = require('./models/Technical.js');
var flash = require('./models/flash.js');
var User = require('./models/User.js');
var report = require('./models/Report.js');

// set up for pug enginer
app.set('views', './views'); //folder where views are stored
app.set('view engine', 'pug');

//use session for tracking login
app.use(session({
  secret: 'hardly technical',
  resave: true,
  saveUninitialized: false
}));

app.use(bp.json());
app.use(bp.urlencoded({ extended: false }));

console.log("\n Starting Server, set-up complete \n");

/**
 * Basic .get that sends a simple hello message on connection
 * @param req 	the request sent to the server
 * @param res 	the response sent to the client
 * @return	sends a "hello" message to the server console
 */
app.get('/', (req, res) => {
	console.log("HELLO");
	res.render('home');
});

/**
 * Register command to create a new user from question-website homepage
 * @param req   the request sent to the server
 * @param next   used to handle error responses
 * @param res   the response sent to the client; must contain email, username, password, and passwordConf fields, fields must not be empty
 * @return  redirects user to profile on succesful creation, spits out error on error
 */
app.post('/register', function (req, res, next) {
  // confirm that user typed same password twice
  if (req.body.password !== req.body.passwordConf) {
  	console.log('passwords dont match');
    var err = new Error('Passwords do not match.');
    err.status = 400;
    res.send("passwords dont match");
    return next(err);
  }

  if (req.body.email &&
    req.body.username &&
    req.body.password &&
    req.body.passwordConf && (req.body.verificationCode == "Questioneers")) {

    console.log('creating new user');
    var userData = {
      email: req.body.email,
      username: req.body.username,
      password: req.body.password,
      passwordConf: req.body.passwordConf,
      accessLevel: "admin",
    }

    User.create(userData, function (error, user) {
      if (error) {
        return next(error);
      } else {
        req.session.userId = user._id;
        return res.redirect('/profile');
      }
    });

  } else  {
    var err = new Error('All fields required.');
    err.status = 400;
    return next(err);
  }
});

/**
 * profile call, redirects to profile page
 * @param req   the request sent to the server
 * @param res   the response sent to the client
 * @param next   used to handle error responses
 * @return  renders the profile page with the username, email, and access level of user
 */
app.get('/profile', function (req, res, next) {
  User.findById(req.session.userId)
    .exec(function (error, user) {
      if (error) {
        return next(error);
      } else {
        if (user === null) {
          var err = new Error('Not authorized! Go back!');
          err.status = 400;
          return next(err);
        } else {
          return res.render('profile', { user : user.username, email : user.email, access : user.accessLevel })
        }
      }
    });
});

/**
 * redirects to create a flashcard question page
 * @param req   the request sent to the server
 * @param res   the response sent to the client
 * @param next   used to handle error responses
 * @return  renders flashcard question page
 */
app.get('/makeFlash', function (req, res, next) {
  User.findById(req.session.userId)
    .exec(function (error, user) {
      if (error) {
        return next(error);
      } else {
        if (user === null) {
          var err = new Error('Not authorized! Go back!');
          err.status = 400;
          return next(err);
        } else {
          return res.render('addFlash', { user : user.username, email : user.email, access : user.accessLevel })
        }
      }
    });
});

/**
 * redirects to create a technical question page
 * @param req   the request sent to the server
 * @param res   the response sent to the client
 * @param next   used to handle error responses
 * @return  renders technical question page
 */
app.get('/makeTechnical', function (req, res, next) {
  User.findById(req.session.userId)
    .exec(function (error, user) {
      if (error) {
        return next(error);
      } else {
        if (user === null) {
          var err = new Error('Not authorized! Go back!');
          err.status = 400;
          return next(err);
        } else {
          return res.render('addTechnical', { user : user.username, email : user.email, access : user.accessLevel })
        }
      }
    });
});

/**
 * redirects to creat flashcard question page
 * @param req   the request sent to the server
 * @param res   the response sent to the client
 * @param next   used to handle error responses
 * @return  renders flashcard question page
 */
app.get('/logout', function (req, res, next) {
  if (req.session) {
    // delete session object
    req.session.destroy(function (err) {
      if (err) {
        return next(err);
      } else {
        return res.redirect('/');
      }
    });
  }
});

/** used to retrieve a random technical question from the technical question database
 * @param req 	the request sent to the server
 * @param res 	the response sent to the client
 * @return 	sends a random technical question as a string back to the client
 */
app.get('/getTechnical', (req, res) => {
  console.log("getTech called...");
  technical.findOneRandom(function(err, doc) {
       if(err) console.log(err);
      else {
       console.log("Question", doc.question)
       res.send(doc.question);}
     });

});

/** used to retrieve a random flashcard question from the flashcard question database
 * @param req   the request sent to the server
 * @param res   the response sent to the client
 * @return  sends a random flashcard question as a string back to the client
 */
app.get('/getFlash', (req, res) => {
	console.log("getFlash called...");
	flash.findOneRandom(function(err, doc) {
		if (err) console.log(err);
		else {
			console.log(doc.question);
			console.log(doc.answer);
			res.send(doc);
		}
	});
});

/** login call
 * @param req   the request sent to the server
 * @param res   the response sent to the client
 * @param next   used to handle error responses
 * @return  succesful login redirects the user to the profile page
 */
app.post('/login', function (req, res, next) {
	if (req.body.loguser && req.body.logpassword) {
    User.authenticate(req.body.loguser, req.body.logpassword, function (error, user) {
      if (error || !user) {
        var err = new Error('Wrong email or password.');
        err.status = 401;
        return next(err);
      } else {
        req.session.userId = user._id;
        return res.redirect('/profile');
      }
    });
  } else  {
    var err = new Error('All fields required.');
    err.status = 400;
    return next(err);
  }
});

/** sends a newly created flashcard question to the database from the website
 * @param req   the request sent to the server
 * @param res   the response sent to the client
 * @param next   used to handle error responses
 * @return  after sending the question, it redirects to the profile page
 */
app.post('/createFlash', function (req, res, next) {
  User.findById(req.session.userId)
    .exec(function (error, user) {
      if (error) {
        return next(error);
      } else {
        if (user === null) {
          var err = new Error('Not authorized! Go back!');
          err.status = 400;
          return next(err);
        } else if(req.body.question === "" || req.body.answer === "") {
        	var err = new Error('Field blank');
          	err.status = 400;
         	return next(err);
        } else {
		      var question = {
			      question: req.body.question,
			      answer: req.body.answer,
			      author: req.session.userId
		    }
        	flash.create(question, function (error, user) {
			      if (error) {
			        return next(error);
			      }
			    });
          return res.render('profile', { user : user.username, email : user.email, access : user.accessLevel })
        }
      }
    });
});

/** sends a newly created technical question to the database from the website
 * @param req   the request sent to the server
 * @param res   the response sent to the client
 * @param next   used to handle error responses
 * @return  after sending the question, it redirects to the profile page
 */
app.post('/createTechnical', function (req, res, next) {
  User.findById(req.session.userId)
    .exec(function (error, user) {
      if (error) {
        return next(error);
      } else {
        if (user === null) {
          var err = new Error('Not authorized! Go back!');
          err.status = 400;
          return next(err);
        } else if(req.body.question === "") {
        	var err = new Error('Field blank');
          	err.status = 400;
         	return next(err);
        } else {
		      var question = {
			      question: req.body.question,
			      author: req.session.userId
		    }
        	technical.create(question, function (error, user) {
			      if (error) {
			        return next(error);
			      }
			    });
          return res.render('profile', { user : user.username, email : user.email, access : user.accessLevel })
        }
      }
    });
});

/** Adds a question reported by user to the reportedQuestions database
 * @param req   the request sent to the server
       Needs:
         user: ID of user that reported question (String)
         questionID: ID of the question being reported (String)
         questionType: Flash or technical question (String)
         reasonForReport: Dropdown option chosen for report (String)
       Optional:
         reasonForReportTextBox: Extra feedback given by user (String)
 * @param res   the response sent to the client, contains "1" if successful and "0" if unsuccessful
 */
app.post('/reportQuestion', function(req, res){
    console.log("Report Question called...");
    var error;
    var newReport = {
        user: req.body.user,
        questionID: req.body.questionID,
        questionType: req.body.questionType,
        reasonForReport: req.body.reasonForReport,
        reasonForReportTextBox:""
    }
    if(req.body.reasonForReportTextBox)
        newReport.reasonForReportTextBox =
            req.body.reasonForReportTextBox;
    else{
        console.log("Reason for report text box left empty");
        newReport.reasonForReportTextBox = "None given";
    }
    var doc;
    error = report.create(newReport, function(err, doc){
        if(err){
            return err;
        }
        else return 0;
    });
    if(error){
        console.log("Error: "+error);
        res.send("0: Error could not report question");
    }
    else{
        console.log("Successfully reported question");
        res.send("1: Successfully reported question");
    }
});

/**Deletes a given question from the reportedQuestions database
   @param req   Request sent to server
                  question: The question to look for
                  questionType: Flash or Technical
   @param res   Response sent to user
*/
app.post('/deleteReport', function(req, res){
    console.log("deleteReport called...");
    var error,
        questionStr = req.body.question,
        foundQuestion,
        type = req.body.questionType,
        deleteQuestionReport = function(){
            if(error !== 1){
                report.deleteOne({questionID:foundQuestion._id,questionType:type}, function(err,result){
                    console.log("Searching to delete");
                    if(err){
                        console.log("Error", err);
                        res.send("2: could not remove report");
                    }
                    else if(result.n === 0){
                        console.log("Question could not be found");
                        res.send("Question has not been reported or report "+
                                 "already deleted");
                    }
                    else{
                        console.log("Successfully deleted report");
                        res.send("Successfully deleted report");
                    }
                });
            }
        };
    if(type === "Flash"){
        console.log("Searching flash questions for ID");
        flash.findOne({question:questionStr},function(err, doc){
            if(err || !doc){
                console.log("Could not find question in Flash");
                error = 1;
            }
            else{
                console.log("Found: "+doc);
                foundQuestion = doc;
                deleteQuestionReport();
            }
        });
    }
    else if(type === "Technical"){
        console.log("Searching technical questions for ID");
        technical.findOne({question:questionStr}, function(err, doc){
            if(err || !doc){
                console.log("Could not find question in Technical");
                error = 1;
            }
            else{
                console.log("Found: "+doc);
                foundQuestion = doc;
                deleteQuestionReport();
            }
        });
    }
    else{
        console.log("Question type not given or wrong");
        res.send("Need questionType to be either flash or technical");
    }
});

/** Finds the id of the given question
   @param req Request from user
       question: the question ID is needed for
       type: Flash or Technical
   @param res Response sent to user
*/
app.post('/getQuestionID', function(req, res){
    console.log("GetQuestionID called...");
    var questionStr = req.body.question,
        questionType = req.body.type,
        questionID,
        error = 0;
    if(questionType === "Flash"){
        console.log("Searching flash questions for ID");
        flash.findOne({question:questionStr},function(err, doc){
            if(err || !doc){
                console.log("Could not find question in Flash");
                res.send("Question could not be found");
                error = 1;
            }
            else{
                console.log("Found: "+doc);
                res.send(doc._id);
            }
        });
    }
    else if(questionType === "Technical"){
        console.log("Searching technical questions for ID");
        technical.findOne({question:questionStr}, function(err, doc){
            if(err || !doc){
                console.log("Could not find question in Technical");
                res.send("Could not find question");
            }
            else{
                console.log("Found: "+doc);
                res.send(doc._id);
            }
        });
    }
});

/** Checks to see if the given question has been reported 
	@param req Request from user
	@param res Response sent to user
*/
app.post('/isReported', function(req, res){
	console.log("isReported called... ");
	var questionsStr = req.body.questionID,
	error = 0;
	console.log("Checking if question is flagged");
	report.findOne({questionID:questionStr},function(err, doc){
		if(err || !doc){
		console.log("Could not find question in report");
		res.send("Question is not flagged");
		error = 1;
	}
		else{
		console.log("Found: "+doc);
		res.send("Question has been flagged");
	}
});
});
/**
   Gets the list of reported questions from the database

   Returns a list of json objects with the following properties
   "user": user that reported the question
   "questionID": _id of the question in whichever collection its in
   "questionType": Which collection the question is in
   "reasonForReport": Option chosen by user
   "reasonForReportTextBox": Additional comments from the user
*/
app.get('/getReported', function(req,res){
    console.log("/getReported called...");
    report.find({}, function(err, docs){
        if(err){
            console.log("Error: ", err);
            res.send("Error");
        }else{
            console.log("Found: " + docs);
            res.send(docs);
        }
    });
});


app.post('/deleteQuestion', function(req,res){
    console.log("/deleteQuestion called...");
    var type = req.body.type;
    var id = req.body.id;
    if(type === "Flash"){
        flash.deleteOne({_id:id}, function(err,result){
            if(err){
                console.log("Error", err);
                res.send("There was an error deleting the question")
            }else if(result.n === 0){//No error but nothing deleted
                console.log("Question not found in flash")
                res.send("Question could not be found");
            }else{
                console.log("Flash question w/ id: "+id+" deleted");
                res.send("Question successfully deleted");
            }
        });
    }else if(type === "Technical"){
        technical.deleteOne({_id:id},function(err,result){
            if(err){
                console.log("Error", err);
                res.send("There was an error deleting the question")
            }else if(result.n === 0){//No error but nothing deleted
                console.log("Question not found in flash")
                res.send("Question could not be found");
            }else{
                console.log("Flash question w/ id: "+id+" deleted");
                res.send("Question successfully deleted");
            }
        });
    }else{
        console.log("Question type incorrect");
        res.send("Need question type to delete a question");
    }
});

/**
 * .listen on port:1234 with launch alter to console
 */
app.listen(jsonRoute.port, ()=>console.log("NULL SERVERED LAUNCHED. LISTENING ON PORT: " + jsonRoute.port));


