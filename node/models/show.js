// initialization from mongoosejs documentation

var mongoose = require('mongoose')
mongoose.connect('mongodb://localhost/test', {useNewUrlParser: true})
var db = mongoose.connection
db.on('error', console.error.bind(console, 'connection error:'))
db.once('open', function() {
  // we're connected!
});

var Schema = mongoose.Schema

var showSchema = new Schema( {
    show_id: String,
    start_date: Date,
    end_date: Date,
    start_time: Date,
    end_time: Date,
    capacity: Number,
    tickets_sold: Number,
    location: String,
    description: String,
    price: Number
})

module.exports = mongoose.model('Event', showSchema)