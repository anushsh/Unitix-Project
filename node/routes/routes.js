var Group = require('../models/group.js');

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
    res.render('login.ejs', {message: ""});
}

var getRegister = (req, res) => {
    res.render('register.ejs', {message: ""});
}

//Checking login details
var checkLogin = (req, res) => {
    Group.findOne({email: req.body.email, password: req.body.password}, (err, user) => {
        if (err) {
            console.log(err);
            res.json({'status': err})
        } else {
            if (!user) {
                res.render('login.ejs', {message: "Invalid email credentials! Try again!"});
            } else {
                req.session.user = req.body.email;
                res.redirect('/home');
            }
        }
    })

}

var getProfile = (req, res) => {
    if (req.session.user) {
        Group.findOne({email: req.session.user}, (err, user) => {
            if (err) {
                res.render({succes: false, error: err})
            } else {
                res.render('profile.ejs', {email: user.email, password: user.password, 
                    displayName: user.displayName, groupType: user.groupType, 
                    bio: user.bio, followers: user.followers})
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
    res.render('create_event.ejs', {"group_email": req.session.user})
}

var getEditEvent = function (req, res) {
    if (req.session.user == null) {
        res.redirect('/login')
    }
    res.render('edit_event.ejs', {"name":req.params.event})
    // Event.findOne({name:req.params.event}, (err, event) => {
    //     !err && event ? res.render('edit_event.ejs', {"event":event}) : res.json({"err":err})
    // })
}


// TODO: need to talk to Michael about how adding tags will work
var addEventTag = function (req, res) {
    //TODO: Implement for real
    res.render('create_event.ejs')
}

var getLogout = function(req, res) {
    if (req.session.user){
        req.session.destroy();
    }
    msg = ""
    res.redirect('/');
};

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
    get_edit_event: getEditEvent
}
