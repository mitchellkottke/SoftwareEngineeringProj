var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var deletedSchema = new Schema({
    question: {type: String, required: true},
    answer: {type: String, required: false},
    author: {type: String, required: true}
},{collection: 'deletedQuestions'});

var deleted = mongoose.model('deleted', deletedSchema);
module.exports = deleted;
