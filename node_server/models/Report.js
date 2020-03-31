var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var reportSchema = new Schema({
	user: {type: String, required: false},
	report: {type: String, required: false},
	reportType: {type: String, required: true}},
	{collection: 'reportedQuestions'});

var report = mongoose.model('report', reportSchema);
module.exports = report;


