var initNodes = [{
  x: 100,
  y: 200,
  fixed: true
},{
  x: 300,
  y: 200,
  fixed: true
}]

var width = 960,
    height = 500,
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
    .nodes(initNodes) // initialize with a single node
    .links([{source:0, target:1}])
    .charge(0)
    .gravity(0)
    .on("tick", tick);

// get layout properties
var nodes = force.nodes(),
    links = force.links(),
    node = vis.selectAll(".node"),
    link = vis.selectAll(".link");

link = link.data(links);
link.enter().insert("line", ".node")
    .attr("class", "link");

node = node.data(nodes);
node.enter().insert("circle")
    .attr("class", "node")
    .attr("r", 5)
    .on("click", 
      function(d) {
        mousedown_node = d;
        if (mousedown_node == selected_node) selected_node = null;
        else selected_node = mousedown_node;
          node
    .classed("node_selected", function(d) { return d === selected_node; });
      })

force.start();

function tick() {
  link.attr("x1", function(d) { return d.source.x; })
      .attr("y1", function(d) { return d.source.y; })
      .attr("x2", function(d) { return d.target.x; })
      .attr("y2", function(d) { return d.target.y; });

  node.attr("cx", function(d) { return d.x; })
      .attr("cy", function(d) { return d.y; });
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