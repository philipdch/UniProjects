const submitButton = document.querySelector(".submit");

const form = document.getElementsByTagName("form");

const pwd1 = document.getElementById("password");
const pwd2 = document.getElementById("repeat-password");
const pwdStrengthError = document.querySelector('#password + span.error');
const pwdMismatchError = document.querySelector("#repeat-password + span.error");

const ccNumber = document.getElementById("card-number");
const isSameAddr = document.getElementById("is-same-address");
const caddr = document.getElementById("caddr");


const cvFile = document.getElementById("cv-file");

const birthDate = document.getElementById("birthdate");
var age;

const fullName = document.getElementsByClassName("fullname");
const email = document.getElementById("mail");
let nameLength = fullName.length;
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

birthDate.addEventListener('input', () => {
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
});

pwd1.addEventListener("input", function () { _checkPwdStrength(pwd1.value) });
pwd2.addEventListener("input", function () { _checkPwdMatch(pwd1.value, pwd2.value) });
ccNumber.addEventListener("input", function () { _findCardProvider(ccNumber.value) });
isSameAddr.addEventListener("change", _pasteAddress);
cvFile.addEventListener("input", checkFileType);

email.addEventListener("input", function () {
    if (email.validity.typeMismatch) {
        email.setCustomValidity("Please enter a valid e-mail address (username@example.com)");
    } else {
        email.setCustomValidity("");
    }
});

function _pasteAddress() {
    caddr.value = (isSameAddr.checked) ?
        `${document.getElementById("street").value} ${document.getElementById("st-number").value}, ${document.getElementById("zip").value}, ${document.getElementById("country").value}`
        : "";
}

function _checkPwdStrength(password) {
    var containsDigits = /(\d)/;
    var containsUppercase = /([A-Z]+)/;
    var containsSpecial = /(\W)/;

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

    if (password.length < 8 || password.length > 16) {
        pwdStrengthError.innerHTML = 'Password must be between 8 and 16 characters long'
        pwdStrengthError.className = 'active-error';
        pwd1.setCustomValidity("Password must be 8-16 characters long");
        pwd1.style.margin = '0.5% 0 1% 0'
    } else {
        pwdStrengthError.innerHTML = '';
        pwdStrengthError.className = 'error';
        pwd1.setCustomValidity("");
        pwd1.style.margin = '0.5% 0 3% 0'
    }
    console.log(count);
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

function _findCardProvider(cardNumber) {
    console.log(`CardNumber: ${cardNumber}`);
    let cardProvider = 'none';
    let ccIcon = document.getElementById("cc-icon");
    let visa = new RegExp(/^4/);
    let mastercard = new RegExp(/^(5[1-5])|(2[2-7])/);
    let diners = new RegExp(/^36/);
    if (mastercard.test(cardNumber)) {
        console.log('MASTERCARD');
        ccIcon.src = 'resources/mastercard.png';
        ccIcon.removeAttribute("hidden");
        cardProvider = 'mc';
    } else if (visa.test(cardNumber)) {
        console.log('VISA');
        ccIcon.src = 'resources/visa.jpg';
        ccIcon.removeAttribute("hidden");
        cardProvider = 'visa';
    } else if (diners.test(cardNumber)) {
        ccIcon.src = 'resources/diners.png';
        ccIcon.removeAttribute("hidden");
        cardProvider = 'dc';
    } else {
        ccIcon.setAttribute('hidden', '');
        cardProvider = 'none';
    }

    console.log(cardProvider);
    if (cardProvider === 'none') {
        ccNumber.setCustomValidity('Please provide a valid card number (VISA, Mastercard or Diner\'s Club)');
        return;
    }

    if (ccNumber.validity.patternMismatch) {
        if (cardProvider === 'mc') {
            ccNumber.setCustomValidity("Mastercard number must be 16 digits long");
        } else if (cardProvider === 'visa') {
            ccNumber.setCustomValidity("VISA numbers must be 13-19 digits long ")
        } else if (cardProvider === 'dc') {
            ccNumber.setCustomValidity("Diner's Club numbers must be 14 digits long");
        }
    } else {
        ccNumber.setCustomValidity('');
    }
}

function checkFileType() {
    console.log('in checkFIle');
    var cvFile = document.getElementById("cv-file");
    var files = cvFile.files;

    // If there is one file selected
    if (files.length != 1) {
        console.log('more than one file given');
        cvFile.setCustomValidity("Please provide only one file");
        cvFile.checkValidity();
        return;
    }
    if (cvFile.value.toString().split('.').pop() !== "pdf") {
        console.log(`${cvFile.value.toString()}, Not a pdf`);// Check the constraint
        cvFile.setCustomValidity("The selected file must be a pdf");
        cvFile.checkValidity();
        return;
    }
    console.log('PDF!');
    // No custom constraint violation
    cvFile.setCustomValidity("");
}