var initNodes = [{
  x: 100,
  y: 200,
  fixed: true,
  courseId: "15112"
},{
  x: 300,
  y: 200,
  courseId: "15122"
},{
  x: 50,
  y: 200,
  courseId: "15213"
},{
  x: 50,
  y: 400,
  courseId: "15410"
}]

var initLinks = [{
  source:0, target:1
},{
  source:1, target:2
},{
  source:2, target:3
}]

var width = window.innerWidth,
    height = window.innerHeight,
    fill = d3.scale.category20();

// mouse event vars
var selected_node = null;

// init svg
var outer = d3.select("#chart")
  .append("svg:svg")
    .attr("width", width)
    .attr("height", height)
    .attr("pointer-events", "all");

var vis = outer
  .append('svg:g')
    .call(d3.behavior.zoom().on("zoom", rescale))
    .on("dblclick.zoom", null)
  .append('svg:g');

vis.append('svg:rect')
    .attr('width', width)
    .attr('height', height)
    .attr('fill', 'white');

// init force layout
var force = d3.layout.force()
    .size([width, height])
    .nodes(initNodes)
    .links(initLinks)
    .linkDistance(10)
    .linkStrength(1)
    .charge(-3000)
    .gravity(0)
    .on("tick", tick);

// get layout properties
var nodes = force.nodes(),
    links = force.links();

var link = vis.selectAll(".link").data(links)
    .enter().insert("line", ".node")
    .attr("class", "link");

var node = vis.selectAll(".node").data(nodes)
    .enter().append("g")
    .attr("class", "node")
    .on("click", 
      function(d) {
        mousedown_node = d;
        if (mousedown_node == selected_node) selected_node = null;
        else selected_node = mousedown_node;
          node.classed("node_selected", function(d) {
            return d === selected_node; });
      })
node.append("circle")
    .attr("r", 5)
node.append("text")
    .attr("dx", 12)
    .attr("dy", ".35em")
    .text(function(d) { return d.courseId; })

force.start();

function tick() {
  link.attr("x1", function(d) { return d.source.x; })
      .attr("y1", function(d) { return d.source.y; })
      .attr("x2", function(d) { return d.target.x; })
      .attr("y2", function(d) { return d.target.y; });
      
  node.attr("transform",
      function(d,i) {return "translate(" + d.x + ", " + d.y + ")"});
}

// pan and scale
function rescale() {
  trans=d3.event.translate;
  scale=d3.event.scale;

  // keep in screen
  trans[0] = Math.max(trans[0], 0)
  trans[1] = Math.max(trans[1], 0)

  vis.attr("transform",
      "translate(" + trans + ")"
      + " scale(" + scale + ")");
}