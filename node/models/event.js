// initialization from mongoosejs documentation
var Show = require('./show.js')

var mongoose = require('mongoose')
mongoose.connect('mongodb://localhost/test', { useNewUrlParser: true })
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