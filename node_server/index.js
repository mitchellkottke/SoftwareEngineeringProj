var express = require('express');
var app = express();
var mongojs = require('mongojs');
var mongoose = require('mongoose');
var session = require('express-session');
//var db = require('./myDB.js');
var bp = require('body-parser');


//Load routing data
const fs = require('fs');
var route = fs.readFileSync('test/routing.json');
var jsonRoute = JSON.parse(route);

//connecting mongoose to RestAPI, target URL stored in route
mongoose.connect('mongodb://ukko.d.umn.edu:42222/AppNull');
var db = mongoose.connection

//handle mongo error
db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function () {
  // we're connected!
});

// Mongoose schema declartion
var technical = require('./models/Technical.js');
var flash = require('./models/flash.js')
var User = require('./models/User.js')

//setting up random data collection


// set up for pug enginer
app.set('views', './views'); //folder where views are stored
app.set('view engine', 'pug');

console.log("\n Starting Server \n");

//use session for tracking login
app.use(session({
  secret: 'hardly technical',
  resave: true,
  saveUninitialized: false
}));

app.use(bp.json());
app.use(bp.urlencoded({ extended: false }));

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

app.get('/register', (req, res) => {
	console.log("User signing up");
	res.render('register')
})

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

/** this .get/getTechnical returns the first technical question from the technical question database
 * @param req 	the request sent to the server
 * @param res 	the response sent to the client
 * @return 	sends the first technical question as a string back to the client
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

/**
 * .listen on port:48821 with launch alter to console
 * IF PORT CHANGES ALERT TEAM NULL
 */
app.listen(jsonRoute.port, ()=>console.log("NULL SERVERED LAUNCHED. LISTENING ON PORT: " + jsonRoute.port));

