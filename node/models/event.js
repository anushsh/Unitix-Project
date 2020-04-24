// initialization from mongoosejs documentation
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
  name: String,
  group: String, // this is the actual object ID
  group_name: String, // this is display name of the group
  shows: [String], // these are uuids of Show objs
  tags: [String],
  changes: [String],
})

module.exports = mongoose.model('Event', eventSchema)