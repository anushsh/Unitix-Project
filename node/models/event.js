// initialization from mongoosejs documentation
var Show = require('./show.js')

var mongoose = require('mongoose')

// need to use "test" (for example) and not "admin" bc admin won't allow for raw queries
mongoose.connect('mongodb+srv://UNITIX_GLOBAL:UnitixCis350@cluster0-q0aj1.mongodb.net/test?retryWrites=true&w=majority', { useNewUrlParser: true })

var db = mongoose.connection

db.on('error', console.error.bind(console, 'connection error:'))
db.once('open', function () {
  // we're connected!
});

var Schema = mongoose.Schema

var eventSchema = new Schema({
  event_id: String,
  name: String,
  group: String,
  shows: [String], //can't have other MongoDB schemas as fields, so we are going to use the string id
  event_type: String
})

module.exports = mongoose.model('Event', eventSchema)