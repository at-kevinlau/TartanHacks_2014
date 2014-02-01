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
        div.innerHTML = "<p style='font-size:5em'>" + 
        node.courseId.slice(0,2) + "-" + node.courseId.slice(2,5) +"</h1>"+
        "<p style='font-size:6em'>" + node.title + "</h1>" +
        "<p style='font-size:3.5em'>"+node.description+"</p>";
        div.setAttribute("class","active");
    }
}