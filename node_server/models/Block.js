var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var blockSchema = new  Schema({
	user: {type: String, required: true}},
	{collection: 'blockedUsers'});

var block = mongoose.model('block', blockSchema);
module.exports = block;
