window.onload = init;

var templates = {};
var error;

function init() {
    templates.courseDetails = Handlebars.compile(document.getElementById("profile-template").innerHTML);
    let submitButton = document.querySelector(".submit");
    submitButton.addEventListener("click", sendRequest);
    error = document.querySelector(".error");
}

function sendRequest(event) {
    console.log("sending login request");
    event.preventDefault();
    event.stopPropagation();

    let email = document.getElementById("email").value;
    let password = document.getElementById("password").value;

    let credentials = new FormData();
    credentials.append("email", email);
    credentials.append("password", password);

    let object = {};
    credentials.forEach(function (value, key) {
        object[key] = value;
    });
    let json = JSON.stringify(object);
    console.log(json);

    var request = new XMLHttpRequest();
    var url = "/login";
    request.open("POST", url, true);
    request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    request.send(json);

    request.addEventListener("load", function (e) {
        console.log("Form submission status: " + request.status);
    });

    request.addEventListener("error", function (e) {
        console.log(e.type);
    });

    request.onreadystatechange = function () {
        if (request.readyState === 4) {
            let json = JSON.parse(request.responseText);
            console.log(json.result);
            if (request.status === 200) {
                error.innerHTML = "";
                error.className = "error";
                fetchUser(email, json.token); //when user authenticates successfuly, a JWT is expected to be sent from the server
            } else {
                error.innerHTML = json.result;
                error.className = "active-error";
            }
        }
    }
}

//retrieve user info by providing only the user's email and the JWT we have received from the server after authentication
function fetchUser(email, accessToken) {
    console.log("Sending user request");
    let requestValues = "email=" + email + "&token=" + accessToken; //GET parameters
    console.log(requestValues);
    var userRequest = new XMLHttpRequest();
    var url = "/user";
    userRequest.open("GET", url + "?" + requestValues, true);
    userRequest.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    userRequest.send(null);

    userRequest.addEventListener("load", function (e) {
        console.log("User fetching status: " + userRequest.status);
    });

    userRequest.addEventListener("error", function (e) {
        console.log(e.type);
    });

    userRequest.onreadystatechange = function () {
        if (userRequest.readyState === 4) {
            if(userRequest.status === 200){
                console.log(userRequest.responseText);
                let loginSection = document.querySelector(".main-section");
                loginSection.style.visibility = "hidden";
                loginSection.style.height = "0";
                let template =  Handlebars.compile(document.getElementById("profile-template").innerHTML);
                let jsonResult = JSON.parse(userRequest.responseText);
                date = new Date(jsonResult.dob);
                jsonResult.dob = `${date.getFullYear()}-${('0' + (date.getMonth()+1)).slice(-2)}-${('0' + date.getDate()).slice(-2)}`;
                let rendered = template(jsonResult);
                let result = document.getElementById("search-result");
                result.innerHTML = rendered;
                error.innerHTML = "";
                error.className = "error";
            }else{
                error.innerHTML = json.result;
                error.className = "active-error";
            }
        }
    }
}