var mongoose = require('mongoose');
mongoose.connect('mongodb+srv://UNITIX_GLOBAL:UnitixCis350@cluster0-q0aj1.mongodb.net/test?retryWrites=true&w=majority', { useNewUrlParser: true })
var db = mongoose.connection
db.on('error', console.error.bind(console, 'connection error:'))
db.once('open', function () {
  // we're connected!
});

var Group = require('./group.js');

var Schema = mongoose.Schema

var groupSchema = new Schema ({
    email: String,
    password: String,
    displayName: String,
    currentEvents: [String], //String for now, change to Ticket model once that's created
    pastEvents: [String], // Same deal here 
    groupType: String,
    bio: String,
    followers: Number
})

module.exports = mongoose.model('Group', groupSchema)