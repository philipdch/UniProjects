window.onload = init;

var templates = {};

function init() {
    templates.courseDetails = Handlebars.compile(document.getElementById("courses-template").innerHTML);
    let submit = document.getElementById("submit-button");
    submit.addEventListener("click", searchTitle);

    templates.categories = Handlebars.compile(document.getElementById("categories-template").innerHTML);
    fetchCategories();

    document.querySelector(".open-btn").addEventListener("click", openNav);
    document.querySelector(".close-btn").addEventListener("click", closeNav);
}

function openNav() {
    document.getElementById("categories-sidebar").style.width = "25%";
    document.querySelector("main").style.width = "70%";
    document.querySelector("main").style.marginLeft = "25%";
    document.querySelector(".open-btn").style.visibility = "hidden";
}

function closeNav() {
    document.getElementById("categories-sidebar").style.width = "0";
    document.querySelector("main").style.width = "90%";
    document.querySelector("main").style.marginLeft = "0";
    document.querySelector(".open-btn").style.visibility = "visible";
}

function searchTitle(event) {
    let result = document.getElementById("search-result");
    event.preventDefault(); //prevent button from submitting form, otherwise error is thrown
    var value = document.getElementById('search-field').value;
    value = encodeURIComponent(value);
    let myHeaders = new Headers();

    myHeaders.append('Accept', 'application/json');
    let init1 = {
        method: "GET",
        headers: myHeaders
    }

    let url = `https://elearning-aueb.herokuapp.com/courses/search?title=${value}`;
    let url2 = "https://elearning-aueb.herokuapp.com/static/images/"
    fetch(url, init1)
        .then(response => response.json())
        .then(data => {
            if (!Object.keys(data).length) {
                result.innerHTML = "No courses found!";
                result.className = "active-error";
            } else {
                for(let i =0 ; i < data.length; i++){
                    data[i].img = url2 + data[i].img;
                    console.log(data[i].img);
                }
                let wrapper = { courses: data }; //wrap JSON in order to reference its objects in handlebars
                let rendered = templates.courseDetails(wrapper);
                result.innerHTML = rendered;
                result.className = "";
            }
        })
        .catch(error => { console.log(error) });
}

function fetchCategories() {
    let result = document.getElementById("menu-categories");
    let requestHeader = new Headers();
    requestHeader.append('Accept', 'application/json');
    let request = {
        method: "GET",
        headers: requestHeader
    }
    let url = "https://elearning-aueb.herokuapp.com/categories";
    fetch(url, request)
        .then(response => response.json())
        .then(data => {
                let wrapper = { categories: data }; //wrap JSON in order to reference its objects in handlebars
                let rendered = templates.categories(wrapper);
                result.innerHTML = rendered;
        })
        .catch(error => { console.log(error) })
}
