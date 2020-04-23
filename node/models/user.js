var mongoose = require('mongoose');

mongoose.connect('mongodb+srv://UNITIX_GLOBAL:UnitixCis350@cluster0-q0aj1.mongodb.net/test?retryWrites=true&w=majority', { useNewUrlParser: true })

var db = mongoose.connection

db.on('error', console.error.bind(console, 'connection error:'))
db.once('open', function () {
  // we're connected!
});
var Schema = mongoose.Schema;


var userSchema = new Schema({
    email: String,
    password: String,
    first_name : String,
    last_name: String,
    phone: String,
    following: [String],
    past_tickets: [String], // string of ids
    curr_tickets: [String],  // string of ids
    saved_tickets: [String], // string of ids
    notifications: [String], // string of ids
    read_notifications: [String],
    favorite_events: [String]
});

module.exports = mongoose.model('User', userSchema);
