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
var deleted = require('./models/Deleted.js');

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
    @author kottk055
 * @param req   the request sent to the server
       Needs:
         user: ID of user that reported question (String)
         question: Question being reported (String)
         questionType: Flash or technical question (String)
         reasonForReport: Dropdown option chosen for report (String)
       Optional:
         reasonForReportTextBox: Extra feedback given by user (String)
 * @param res   the response sent to the client, contains "1" if successful and "0" if unsuccessful
 */
app.post('/reportQuestion', function(req, res){
    console.log("Report Question called...");
    var newReport = {
        user: req.body.user,
        question: req.body.question,
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
    report.create(newReport, function(err, doc){
        if(err){
            console.log("Error: "+err);
            res.send("0: Error could not report question");
        }
        else{
            console.log("Successfully reported question");
            res.send("1: Successfully reported question");
        }
    });
});

/**Deletes a given question from the reportedQuestions database
   @author kottk055
   @param req   Request sent to server
                  question: The question to look for
                  questionType: Flash or Technical
   @param res   Response sent to user
*/
app.post('/deleteReport', function(req, res){
    console.log("deleteReport called...");
    var questionStr = req.body.question;
    var type = req.body.questionType;
    report.deleteOne({questionID:questionStr,questionType:type}, function(err,result){
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
});

// TRYING OUT A DELETE REQUEST
app.delete('/deleteTheReport/:id', (req, res) =>
 report.findOneAndRemove({
  _id: req.params.id
 }, (err, report) => {
  if(err) {
   res.send('error removing')
  } else {
   console.log(report);
   res.status(204);
 }
}));

/** Checks to see if the given question has been reported
        @author bock0077
	@param req Request from user
	@param res Response sent to user
*/
app.post('/isReported', function(req, res){
    console.log("isReported called...");
    var questionStr = req.body.question,
	error = 0;
    console.log("Checking if question is flagged");
    report.findOne({question:questionStr},function(err, doc){
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
   @author kottk055
   Returns a list of json objects with the following properties
   "user": user that reported the question
   "question": the actual question
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

/**
   ****Should only be accessed by an admin******
   Removes question from flash, technical and reported and puts it in deleted
   @author kottk055
   @req   type: Flash or Technical
          question: Question
*/
app.post('/deleteQuestion', function(req,res){
    console.log("/deleteQuestion called...");
    var type = req.body.type;
    var questionStr = req.body.question;
    var answer, author;
    var errorHandle = function(){
        deleted.deleteOne({question:questionStr}, function(err, result){
            if(err) console.log("Error removing deleted section",
                                err);
            else console.log("Error handled");
        });
    };
    var deleteReport = function(){
        console.log("Looking for report");
        report.deleteOne({question:questionStr}, function(err,result){
            if(err){
                console.log("Error: ", err);
                res.send("Question deleted from but there was an error finding a report");
            }else if(result.n === 0){
                console.log("Report not found");
                res.send("Question deleted from but no report was found");
            }else{
                console.log("Report deleted");
                res.send("Question and report deleted");
            }
        });
    };
    var deleteQuestion = function(){
        if(type === "Flash"){
            console.log("Looking in Flash...");
            flash.deleteOne({question:questionStr}, function(err,result){
                if(err){
                    console.log("Error", err);
                    res.send("There was an error deleting the question");
                    errorHandle();
                }else{
                    console.log("Flash question deleted");
                    deleteReport();
                }
            });
        }else if(type === "Technical"){
            technical.deleteOne({question:questionStr},function(err,result){
                if(err){
                    console.log("Error", err);
                    res.send("There was an error deleting the question");
                    errorHandle();
                }else{
                    console.log("Technical question deleted");
                    deleteReport();
                }
            });
        }
    };
    var moveToDeleted = function(){
        var removedQuestion = {
            question : questionStr,
            answer : answer,
            author : author
        };
        deleted.create(removedQuestion, function(err, doc){
            if(err){
                console.log("Error", err);
                res.send("An error occured");
            }
            else{
                console.log("Question created in deleted");
                deleteQuestion();
            }
        });
    };
    //Get extra data
    if(type === "Flash"){
        flash.findOne({question:questionStr}, function(err, doc){
            if(err){
                console.log("Error", err);
                res.send("Could not find question");
            }
            else{
                answer = doc.answer;
                author = doc.author;
                moveToDeleted();
            }
        });
    }
    else if(type === "Technical"){
        technical.findOne({question:questionStr}, function(err, doc){
            if(err){
                console.log("Error", err);
                res.send("Could not find question");
            }
            else{
                answer = null;
                author = doc.author;
                moveToDeleted();
            }
        });
    }
    else{
        console.log("Wrong type given");
        res.send("Error: Need type to be Flash or Technical");
    }
});

/**
	Blocks a user from reporting questions in case they are abusing the power
	@author bock0077
	@req
*/
app.post('/blockUser', function(req,res) {
	console.log("/blockUser called...");
	var blockedUser = req.body.user;
	console.log("Adding user to blockedUser database");
	block.create({user:blockedUser}, function(err,doc){
	if(blockedUser || !doc){
	 console.log("Could not find blockedUser in report");
            res.send("User was not successfully added to the database");
            error = 1;
	}
	else {
	console.log("User was found in the database");
	res.send("User was successfully added to the database");
	}
    });
});



/**
 * .listen on port:1234 with launch alter to console
 */
app.listen(jsonRoute.port, ()=>console.log("NULL SERVERED LAUNCHED. LISTENING ON PORT: " + jsonRoute.port));

