var courses = new Array();
for (var i = 0; i < nodes.length; i++)
{
    var c = nodes[i];
    courses[c.courseId] = c.nodeId;
}

function search(query) {
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