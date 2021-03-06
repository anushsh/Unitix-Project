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

var changeSchema = new Schema({
    field_changed: String,
    prior_value: String,
    updated_value: String,
    time: Date
  })
  
  module.exports = mongoose.model('Change', changeSchema)