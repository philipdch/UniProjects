window.onload = init();

var submitButton;

var htmlForm;

var pwd1;
var pwd2;
var pwdStrengthError;
var pwdMismatchError;
var birthDate;
var phone;
var telCode;
var street;
var stNumber;
var country;
var zipCode;
var degree;

var fullName;
var email;

var response;
var returnButton;

function init() {
    submitButton = document.querySelector(".submit");
    submitButton.addEventListener("click", sendRequest);

    returnButton = document.getElementById("home-button");
    returnButton.style.visibility = "hidden";
    returnButton.style.height = 0;

    htmlForm = document.querySelector("form");

    fullName = document.getElementsByClassName("fullname");
    birthDate = document.getElementById("birthdate");
    phone = document.getElementById("phone");
    telCode = document.getElementById("tel-code");
    street = document.getElementById("street");
    stNumber = document.getElementById("st-number");
    zipCode = document.getElementById("zip");
    country = document.getElementById("country");
    degree = document.getElementById("degree");

    response = document.getElementById("response-field");
    response.style.textAlign = "center";

    email = document.getElementById("mail");
    pwd1 = document.getElementById("password");
    pwd2 = document.getElementById("repeat-password");
    pwdStrengthError = document.querySelector('#password + span.error');
    pwdMismatchError = document.querySelector("#repeat-password + span.error");
    birthDate.addEventListener('input', checkAge);

    pwd1.addEventListener("input", function () { _checkPwdStrength(pwd1.value) });
    pwd2.addEventListener("input", function () { _checkPwdMatch(pwd1.value, pwd2.value) });

    email.addEventListener("input", function () {
        if (email.validity.typeMismatch) {
            email.setCustomValidity("Please enter a valid e-mail address (username@example.com)");
        } else {
            email.setCustomValidity("");
        }
    });

    for (const name of fullName) {
        name.addEventListener('input', () => {
            console.log('Checking name input');
            name.setCustomValidity('');
            name.checkValidity();
        });

        name.addEventListener('invalid', () => {
            if (name.value === '') {
                name.setCustomValidity('Please enter a name');
            } else {
                name.setCustomValidity('Names cannot contain numbers, spaces or special characters');
            }
        })
    }
}

function checkAge() {
    console.log(birthDate.value);
    let date = new Date(birthDate.value);
    let currentDate = new Date();
    let age = Math.abs(currentDate - date);
    age = Math.trunc(age / 1000 / 60 / 60 / 24 / 365); //convert ms to years
    console.log(age);
    if (age > 13) {
        birthDate.setCustomValidity('');
    } else {
        birthDate.setCustomValidity('You must be older than 13 years old to register');
    }
}

function _checkPwdStrength(password) {
    var containsDigits = /(\d)/;
    var containsUppercase = /([A-Z]+)/;
    var containsSpecial = /[!@#$%^&*]/;

    var count = 0;
    count += (containsDigits.test(password)) ? 1 : 0;
    count += (containsUppercase.test(password)) ? 1 : 0;
    count += (containsSpecial.test(password)) ? 1 : 0;
    if (count < 3) {
        pwdStrengthError.innerHTML = 'Password must contain at least one digit, one uppercase letter <br>as well as a special character (! @ # $ % ^ & * _)';
        pwdStrengthError.className = 'active-error';
        pwd1.setCustomValidity("Please enter a password that contains at least one digit, one uppercase and a special character");
        pwd1.style.margin = '0.5% 0 1% 0'
        return;
    } else {
        pwdStrengthError.innerHTML = '';
        pwdStrengthError.className = 'error';
        pwd1.setCustomValidity("");
        pwd1.style.margin = '0.5% 0 3% 0'
    }

    if (password.length < 8) {
        pwdStrengthError.innerHTML = 'Password must be at least 8 characters long'
        pwdStrengthError.className = 'active-error';
        pwd1.setCustomValidity("Password must be >8 characters long");
        pwd1.style.margin = '0.5% 0 1% 0'
    } else {
        pwdStrengthError.innerHTML = '';
        pwdStrengthError.className = 'error';
        pwd1.setCustomValidity("");
        pwd1.style.margin = '0.5% 0 3% 0'
    }
}

function _checkPwdMatch(password1, password2) {
    if (password1 === password2) {
        pwdMismatchError.innerHTML = '';
        pwdMismatchError.className = 'error';
        pwd2.style.margin = '0.5% 0 3% 0'
        pwd2.setCustomValidity("");
    } else {
        pwdMismatchError.innerHTML = 'The passwords do not match!';
        pwdMismatchError.className = 'active-error'
        pwd2.style.margin = '0.5% 0 1% 0'
        pwd2.setCustomValidity('Passwords must match.');
    }
}

function sendRequest(event) {
    console.log("sending request");
    event.preventDefault();
    event.stopPropagation();

    let fname = fullName[0].value;
    let lname = fullName[1].value;
    let address = `${street.value} ${stNumber.value}`;
    let phoneNumber = `${telCode.options[telCode.selectedIndex].text}${phone.value}`;
    let date = birthDate.valueAsDate;
    console.log(date);
    let dob = `${date.getFullYear()}-${('0' + (date.getMonth()+1)).slice(-2)}-${('0' + date.getDate()).slice(-2)}`;
    console.log(dob);
    let formData = new FormData();
    formData.append("firstname", fname);
    formData.append("lastname", lname);
    formData.append("birthdate", dob);
    formData.append("address", address);
    formData.append("zip", zipCode.value);
    formData.append("country", country.value);
    formData.append("phone", phoneNumber); 
    formData.append("degree", degree.value);
    formData.append("email", email.value); 
    formData.append("password", pwd1.value);

    let object = {};
    formData.forEach(function(value, key){
        object[key] = value;
    });
    let json = JSON.stringify(object);
    console.log(json);

    var request = new XMLHttpRequest();
    var url = "/signup";
    request.open("POST", url, true);
    request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    request.send(json);

    request.addEventListener("load", function(e){
        console.log("Form submission status: " + request.status);
    });

    request.addEventListener("error", function(e){
        console.log(e.type);
    });

    request.onreadystatechange = function () {
        if (request.readyState === 4) {
            var json = JSON.parse(request.responseText);
            if(json.result){
                htmlForm.style.visibility = "hidden";
                htmlForm.style.height = "0";
                response.innerHTML = "Succesfully registered!"
                response.className = "active-success";
                response.style.gridRowStart = "4";
                returnButton.style.visibility = "visible";
                returnButton.style.height = "10vh"
            }else{
                response.innerHTML = "Registered unsuccessful!\nAnother user with this e-mail already exists!";
                response.className = "active-error";
                returnButton.style.visibility = "hidden";
                htmlForm.style.visibility = "visible";
                returnButton.style.height = "0";
            }
            console.log(json);
        }
    };
}