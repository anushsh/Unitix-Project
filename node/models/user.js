var mongoose = require('mongoose');
mongoose.connect('mongodb+srv://UNITIX_GLOBAL:UnitixCis350@cluster0-q0aj1.mongodb.net/test?retryWrites=true&w=majority', { useNewUrlParser: true })
var db = mongoose.connection
db.on('error', console.error.bind(console, 'connection error:'))
db.once('open', function () {
  // we're connected!
});

var User = require('./user.js');

var Schema = mongoose.Schema

var userSchema = new Schema ({
    email: String,
    password: String,
    firstName: String,
    lastName: String,
    phone: {type: String, default: null},
    following: [{type: String, default: null}],
    current_tickets: [String], //String for now, change to Ticket model once that's created
    past_tickets: [String] // Same deal here 
})

module.exports = mongoose.model('User', userSchema)