var courses = new Array();
for (var i = 0; i < nodes.length; i++)
{
    var c = nodes[i];
    if (c.courseId.length == 5)
    {
        courses[c.courseId] = c.nodeId;
    }
}

function search(query) {
    if (query.length == 6){
        query = query.slice(0, 2) + query.slice(3, query.length);
    }
    if (courses[query])
    {
        selectNode(nodes[courses[query]],node[0][courses[query]]);
    }
}


function searchButton(){
  search(document.getElementById("search_text").value);
}

function searchBox(){
    if (event.keyCode == 13) 
    {
       search(document.getElementById("search_text").value);
       return false;
    }
}

function describe(courseId){
    var div = document.getElementById("courseDescription");
    if (!courseId){
        div.setAttribute("class","unactive");
        return;
    } else {
        var node = nodes[courses[courseId]];
        div.innerHTML = "<p style='font-weight: bold; text-align: center;'>" +
        node.courseId.slice(0,2) + "-" + node.courseId.slice(2,5) +": "+
        node.title + "</h1>" +
        "<p style=''>"+node.description;
        div.setAttribute("class","active");
    }
}

classSet = {}

function takeClasses(text) {
    if ((!text) || (text === "")) return
    var courseArray = text.match(/\d\d-\d\d\d/g);
    if (courseArray) {
        courseArray = courseArray.concat(text.match(/\d\d\d\d\d/g) || []);
    } else {
        courseArray = text.match(/\d\d\d\d\d/g);
    }
    if (!courseArray) return;
    for (var i=0; i<courseArray.length; i++){
        courseArray[i] = courseArray[i].replace('-', '');
    }
    courseArray.forEach(function(courseId){
        classSet[courseId] = "";
        if(courses[courseId])
            node[0][courses[courseId]].classList.add("nodeTaken");
    });
}

function uploadButton(){
  takeClasses(document.getElementById("upload_text").value);
}

function uploadBox(){
    if (event.keyCode == 13) 
    {
       takeClasses(document.getElementById("upload_text").value);
       return false;
    }
}

function saveCookie() {
    var currentClasses = "";
    Object.keys(classSet).forEach(function(s){
        currentClasses += s + ", "
    })
    console.log(currentClasses)
    window.localStorage["courses"] = currentClasses;
}

function deleteCookie() {
    classSet = {}
    delete window.localStorage["courses"];
    node.classed("nodeTaken", false);
}


takeClasses(window.localStorage["courses"]);