var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var reportSchema = new Schema({
	user: {type: String, required: false},
	questionID: {type: String, required: true},
	questionType: {type: String, required: true},
	reasonForReport: {type: String, required: true},
	reasonForReportTextBox: {type: string, required: false}},
	{collection: 'reportedQuestions'});

var report = mongoose.model('report', reportSchema);
module.exports = report;


