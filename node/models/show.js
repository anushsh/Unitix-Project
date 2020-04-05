// initialization from mongoosejs documentation

var mongoose = require('mongoose')
mongoose.connect('mongodb+srv://UNITIX_GLOBAL:UnitixCis350@cluster0-q0aj1.mongodb.net/test?retryWrites=true&w=majority', { useNewUrlParser: true })
var db = mongoose.connection
db.on('error', console.error.bind(console, 'connection error:'))
db.once('open', function () {
  // we're connected!
});

var Schema = mongoose.Schema

var showSchema = new Schema({
  name: String,
  start_date: Date,
  end_date: Date,
  start_time: String,
  end_time: String,
  capacity: Number,
  tickets_sold: Number,
  location: String,
  description: String,
  tickets: [String],
  event: String, // uses UUID of Event obj
  price: Schema.Types.Decimal128 // allows for non-integer prices
})

module.exports = mongoose.model('Show', showSchema)