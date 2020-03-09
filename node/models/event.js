// initialization from mongoosejs documentation

var mongoose = require('mongoose')
mongoose.connect('mongodb://localhost/test', {useNewUrlParser: true})
var db = mongoose.connection
db.on('error', console.error.bind(console, 'connection error:'))
db.once('open', function() {
  // we're connected!
});

var Schema = mongoose.Schema

var eventSchema = new Schema( {
    event_id: String,
    group: String,
    shows: [Show],
    event_type: String
})

module.exports = mongoose.model('Event', eventSchema)