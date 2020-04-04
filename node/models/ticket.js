// initialization from mongoosejs documentation
var Ticket = require('./ticket.js')

var mongoose = require('mongoose')

// need to use "test" (for example) and not "admin" bc admin won't allow for raw queries
mongoose.connect('mongodb+srv://UNITIX_GLOBAL:UnitixCis350@cluster0-q0aj1.mongodb.net/test?retryWrites=true&w=majority', { useNewUrlParser: true })

var db = mongoose.connection

db.on('error', console.error.bind(console, 'connection error:'))
db.once('open', function () {
  // we're connected!
});

var Schema = mongoose.Schema

var ticketSchema = new Schema({
  show: String, // showID
  redeemed: Boolean,
  requested: Boolean
})

module.exports = mongoose.model('Ticket', ticketSchema)