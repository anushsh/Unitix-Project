var Group = require('../models/group.js');
var User = require('../models/user.js');
var async = require('async')
const fs = require('fs');
const aws = require( 'aws-sdk' );
const keys = require('../keys');
const stripe = require('stripe')(keys.StripeSecretKey);
/**
 * PROFILE IMAGE STORING STARTS
 */
const s3 = new aws.S3({
    accessKeyId: keys.AWSClientKey,
    secretAccessKey: keys.AWSSecretKey,
    Bucket: 'unitixphotos'
   });

/**
 * @route POST api/profile/business-img-upload
 * @desc Upload post image
 * @access public
 */

 var storeImage = (req, res) => {
     console.log("REQ");
     console.log(req.files.file);
     console.log(typeof(req.files.file));
     uploadFile(req.files.file);
     res.redirect("/pictures");
    }

    const uploadFile = (file) => {
        // Read content from the file
        const fileContent = fs.readFileSync(file.path);
        console.log("AQUI");
        console.log(file);
        // Setting up S3 upload parameters
        const params = {
            Bucket: 'unitixphotos',
            Key: file.originalFilename, // File name you want to save as in S3
            Body: fileContent,
        };
    
        // Uploading files to the bucket
        s3.upload(params, function(err, data) {
            if (err) {
                throw err;
            }
            console.log(`File uploaded successfully. ${data.Location}`);
            return;
        });
    };

var readPic = (req, res) => {
    var params = {
        Bucket: "unitixphotos", 
       }

    s3.listObjects(params, function(err,data) {
        if (err) {
            console.log('There was an error viewing your photos: ' + err.message);
          }
        res.json({"data" : data});
    })
}

var msg // to send notifications at top of screen when getting to a page
var getMessage = function (req, res) {
    res.send(msg)
}

var getSplash = function (req, res) {
    req.session.user == null ? res.render('splash.ejs') : res.redirect('/home')
}

var getHome = function (req, res) {
    if (req.session.user == null) {
        res.redirect('/login');
    } else {
        if (req.query.msg) {
            msg = req.query.msg
        }
        res.render('home.ejs');

    }
}

//Login functions
var getLogin = (req, res) => {
    if (req.session.user) {
        res.redirect("/home");
    }
    res.render('login.ejs', { message: "" });
}

var getRegister = (req, res) => {
    res.render('register.ejs', { message: "" });
}

//Checking login details
var checkLogin = (req, res) => {
    Group.findOne({ email: req.body.email, password: req.body.password }, (err, user) => {
        if (err) {
            console.log(err);
            res.json({ 'status': err })
        } else {
            if (!user) {
                res.render('login.ejs', { message: "Invalid email credentials! Try again!" });
            } else {
                req.session.user = req.body.email;
                res.redirect('/home');
            }
        }
    })

}

var getProfile = (req, res) => {
    if (req.session.user) {
        Group.findOne({ email: req.session.user }, (err, user) => {
            if (err) {
                res.json({ success: false, error: err })
            } else {
                res.render('profile.ejs', {
                    email: user.email, password: user.password,
                    displayName: user.displayName, groupType: user.groupType,
                    bio: user.bio, followers: user.followers, stripe: user.stripe
                })
            }
        })
    } else {
        res.redirect('/');
    }
}

var getCreateEvent = function (req, res) {
    if (req.session.user == null) {
        res.redirect('/login');
    }
    res.render('create_event.ejs', { "group_email": req.session.user })
}

var getEditEvent = function (req, res) {
    if (req.session.user == null) {
        res.redirect('/login')
    }
    res.render('edit_event.ejs', { "name": req.params.event })
    // Event.findOne({name:req.params.event}, (err, event) => {
    //     !err && event ? res.render('edit_event.ejs', {"event":event}) : res.json({"err":err})
    // })
}

var getViewStats = function (req, res) {
    if (req.session.user == null) {
        res.redirect('/login')
    }
    res.render('view_event.ejs', {"name":req.params.event})
    // Event.findOne({name:req.params.event}, (err, event) => {
    //     !err && event ? res.render('edit_event.ejs', {"event":event}) : res.json({"err":err})
    // })
}


// TODO: need to talk to Michael about how adding tags will work
var addEventTag = function (req, res) {
    //TODO: Implement for real
    res.render('create_event.ejs')
}

var getLogout = function (req, res) {
    if (req.session.user) {
        req.session.destroy();
    }
    msg = ""
    res.redirect('/');
};

var getFollowerNames = function (req, res) {
    Group.findOne({ email: req.session.user }, (err, group) => {
        if (err) {
            res.json({ success: false, error: err })
        } else {
            var followerObjects = [];

            async.forEach(group.followers, (followerID, done) => {
                User.findById(followerID, (err, follower) => {
                    if (!err && follower) {
                        //follower = follower.toJSON();
                        followerObjects.push({ firstName: follower.first_name, lastName: follower.last_name });
                        done();
                    } else {
                        followerObjects = [] // something went wrong, just return empty array
                        done();
                    }
                })
            }, () => {
                res.json(followerObjects)
            });
        }
    })
}

var getFollowers = function (req, res) {
    if (req.session.user) {
        res.render('group_followers.ejs')
    } else {
        res.redirect('/');
    }
}

var getPayment = async (req, res) => {
    const stripeToken = req.body.token;
    const charge = await stripe.charges.create({
        amount: 1000,
        currency: 'usd',
        description: 'Demo charge',
        source: stripeToken,
    }, async function (err, charge) {
        if (err) {
            res.json({
                success: false,
                message: "Error"
            });
        } else {
            res.json(charge);
        }
    });
}

var getPictures = (req, res) => {
    if (req.session.user){
        res.render('pictures.ejs');
    } else {
        res.redirect('/');
    }
}

module.exports = {
    get_splash: getSplash,
    get_message: getMessage,
    get_home: getHome,
    get_create_event: getCreateEvent,
    get_login: getLogin,
    get_register: getRegister,
    check_login: checkLogin,
    get_logout: getLogout,
    get_profile: getProfile,
    get_edit_event: getEditEvent,
    get_view_stats: getViewStats,
    get_follower_names: getFollowerNames,
    get_followers: getFollowers,
    payment: getPayment,
    get_picture: getPictures,
    upload_image: storeImage,
    read_pic: readPic,
}
