const express = require('express')
var bodyParser = require('body-parser')
const path = require('path')
const bcrypt = require("bcrypt");
const jwt = require("jsonwebtoken");
const { pool } = require("./db");
const secret = require("./auth.config");
const { request } = require('express');
const { verify } = require('crypto');
const app = express()
const port = 8080
const salt = 10;

app.use(express.static(__dirname + '/public'));
/* 
    Serve static content from directory "public",
    it will be accessible under path /static, 
    e.g. http://localhost:8080/static/index.html
*/
app.use('/static', express.static(__dirname + '/public'))

app.use(bodyParser.json());
// parse url-encoded content from body
app.use(express.urlencoded({ extended: false }))

// parse application/json content from body
app.use(express.json())

app.use(function (req, res, next) {
    res.header("Access-Control-Allow-Headers", "x-access-token, Origin, Content-Type, Accept");
    next();
});

// serve index.html as content root
app.get('/', function (req, res) {

    var options = {
        root: path.join(__dirname, 'public')
    }
    res.sendFile('index.html', options, function (err) {
        if (err) {
            console.log(err);
        }
        else {
            console.log('Sent:', 'index.html');
        }
    })
});

//Endpoint where user registration requests are sent
app.post('/signup', function (request, response) {
    console.log(request.body);
    insertUser(request.body)
        .then((value) => {
            if (value) {
                console.log("200 OK");
                response.status(200).send({ "result": true });
            } else {
                console.log("409 CONFLICT");
                response.status(409).send({ "result": false });
            }
        }
        );
});

//Enpoint to authenticate users based on email and password
//Upon successful authentication a unique JWT is sent back to client
//as proof. JWT may be stored in DB (optional - not needed in this UC)
app.post('/login', function (request, response) {
    authenticateUser(request.body)
        .then((value) => {
            if (value.result === Responses.AUTHENTICATED) {
                console.log("200 OK");
                response.status(200).send(value);
            } else if (value.result === Responses.NO_USER) {
                console.log("404 NOT FOUND");
                response.status(404).send(value);
            } else if (value.result === Responses.PASSWORD_MISMATCH) {
                console.log("401 UNAUTHORIZED");
                response.status(401).send(value);
            } else if (value.result === Responses.COMPARE_ERROR || value.result === Responses.QUERY_ERROR) {
                console.log("500 SERVER ERROR");
                response.status(500).send(value);
            } else {
                console.log("418 Unknown error");
                response.status(418).send(value);
            }
        }
        );
});

//endpoint where requests to retrieve user info are sent
//Client MUST send their JWT in order to verify they have been authenticated
//before server can send back this resource
app.get('/user', function (request, response) {
    verifyToken(request)
        .then((val) => {
            console.log("Response= " + val);
            if (val === Responses.UNAUTHORIZED) {
                response.status(401).send({ "result": val });
            } else if (val === Responses.NO_TOKEN) {
                response.status(403).send({ "result": val });
            } else if (val === Responses.AUTHENTICATED) {
                //request.query -> get query parametes as fields
                fetchUser(request.query.email).then((value) => {
                    if (value.result === Responses.NO_USER) {
                        response.status(404).send(value);
                    } else {
                        response.status(200).send(value);
                    }
                });
            } else {
                response.status(418).send({ "result": val });
            }
        });
}
);

app.listen(port, () => console.log("running on port: " + port))

//define response messages
const Responses = {
    AUTHENTICATED: "User authenticated successfully",
    UNAUTHORIZED: "Not authorized to view this resource",
    USER_EXISTS: "User already exists",
    NO_USER: "No user with these credentials exists!",
    PASSWORD_MISMATCH: "Password does not match!",
    COMPARE_ERROR: "Error comparing passwords",
    QUERY_ERROR: "A database error occured",
    NO_TOKEN: "No Token provided!",
    UNKNOWN: "An unknown error occured."
}

//Define user object and helper methods
const User = function (fname = "", lname = "", email = "", dob = "", phone = "", address = "", zip = "", country = "", degree = "", password = "", isAuthenticated = "false") {
    this.fname = fname;
    this.lname = lname;
    this.email = email;
    this.dob = dob;
    this.phone = phone;
    this.address = address;
    this.zip = zip;
    this.country = country;
    this.degree = degree;
    this.password = password;
    this.isAuthenticated = isAuthenticated;
}

User.prototype.fromJSON = function (json) {
    this.fname = json.firstname;
    this.lname = json.lastname;
    this.email = json.email;
    this.address = json.address;
    this.zip = json.zip;
    this.country = json.country;
    this.dob = json.birthdate;
    this.phone = json.phone;
    this.degree = json.degree;
    this.password = json.password;
}

User.prototype.asArray = function () {
    return [this.fname, this.lname, this.email, this.dob, this.address, this.zip, this.country, this.phone, this.degree, this.password];
}

//insert user in DB as received from client. Password is hashed before being stored 
async function insertUser(data) {
    console.log("In INSERT");
    console.log(data);
    let newUser = new User();
    newUser.fromJSON(data);
    try {

        const query = await pool.query(`SELECT * FROM "Users" WHERE email= $1;`, [newUser.email]); //Checking if user already exists
        const arr = query.rows;
        console.log(arr);
        if (arr.length != 0) {
            console.log("User already exists!");
            return false;
        }

        bcrypt.hash(newUser.password, salt, async (err, hash) => {
            if (err) {
                throw "Error generating hash";
            } else {
                console.log("Add hashed pwd");
                newUser.password = hash;
            }
            let values = newUser.asArray();
            const res = await pool.query(
                'INSERT INTO "Users" (fname, lname, email, dob, street, zip, country, phonenumber, degree, password) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10)',
                values
            );
            console.log(`Added into User ${values}`);
        });
    } catch (error) {
        console.error(error);
        return false;
    }
    return true;
}

//find and return user by email
async function fetchUser(email) {
    console.log("IN FETCH USER");
    console.log(`Email: ${email}`);
    try {
        const query = await pool.query(`SELECT * FROM "Users" WHERE email= $1;`, [email]); //Checking if user already exists
        const arr = query.rows;
        console.log(arr);
        if (arr.length === 0) {
            console.log("No user with these credentials exists!");
            return { "result": Responses.NO_USER };
        } else {
            return arr[0];
        }
    } catch {
        console.error(error);
        return { "result": Responses.QUERY_ERROR };
    }
}

//retrieve user credentials from DB and authenticate user
async function authenticateUser(data) {
    console.log("IN AUTHENTICATE USER");
    console.log(data);
    let returnValue = { "token": null, "result": Responses.UNKNOWN };
    try {
        console.log("searching with email " + data.email);
        const query = await pool.query(`SELECT * FROM "Users" WHERE email= $1;`, [data.email]); //Checking if user already exists
        const arr = query.rows;
        console.log(arr);
        if (arr.length === 0) {
            console.log("No user with these credentials exists!");
            return { "token": null, "result": Responses.NO_USER };
        }
        returnValue = await compareAsync(data.password, arr[0]);
    } catch (error) {
        console.error(error);
        return { "token": null, "result": Responses.QUERY_ERROR };
    }
    return returnValue;
}

//compare hashed password stored in DB with plaintext password sent by client
function compareAsync(textPassword, user) {
    return new Promise(function (resolve, reject) {
        console.log("Comparing password");
        bcrypt.compare(textPassword, user.password, function (err, result) {
            if (err) {
                resolve({ "token": null, "result": Responses.COMPARE_ERROR });
            }
            if (result === true) {
                let token = jwt.sign({ email: user.email }, secret.secret, { expiresIn: 86400 });
                resolve({ "token": token, "result": Responses.AUTHENTICATED });
            } else {
                console.log("Password doesn't match");
                resolve({ "token": null, "result": Responses.PASSWORD_MISMATCH });
            }
        });
    });
}

//verify token sent by client (if provided)
async function verifyToken(request) {
    let token = request.query.token;
    console.log(token);
    let response = Responses.UNAUTHORIZED;
    if (!token) {
        return Responses.NO_TOKEN;
    }
    jwt.verify(token, secret.secret, (err, decoded) => {
        if (err) {
            console.log("unverified!");
            response = Responses.UNAUTHORIZED;
        } else {
            response = Responses.AUTHENTICATED;
        }
    });
    return response;
};