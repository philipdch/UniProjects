window.onload = init;

var templates = {};

function init() {
    templates.categoryDetails = Handlebars.compile(document.getElementById("category-template").innerHTML);
    fetchCategory();
}

function fetchCategory() {
    const urlSearchParams = new URLSearchParams(window.location.search);
    const catId = Object.fromEntries(urlSearchParams.entries()).category;
    console.log(catId);
    let result = document.getElementById("search-result");
    let myHeaders = new Headers();

    myHeaders.append('Accept', 'application/json');
    let init1 = {
        method: "GET",
        headers: myHeaders
    }

    let url = `https://elearning-aueb.herokuapp.com/courses/search?category=${catId}`;
    let url2 = "https://elearning-aueb.herokuapp.com/static/images/"
    fetch(url, init1)
        .then(response => response.json())
        .then(data => {
            console.log(data);
            if (!Object.keys(data).length) {
                result.innerHTML = "No courses found!";
                result.className = "active-error";
            } else {
                for (let i = 0; i < data.length; i++) {
                    data[i].img = url2 + data[i].img;
                }
                let wrapper = { courses: data }; //wrap JSON in order to reference its objects in handlebars
                let rendered = templates.categoryDetails(wrapper);
                console.log(wrapper);
                result.innerHTML = rendered;
                result.className = "";
                console.log(result);
            }
        })
        .catch(error => { console.log(error) });

}