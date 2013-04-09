// d3.xml("assets/map.svg", "image/svg+xml", function(xml) {
//   var importedNode = document.importNode(xml.documentElement, true);
//   var paths = importedNode.querySelectorAll('path');
//   var i;
//   for (i=0; i < paths.length; i+=1) {
//   	svg.append('svg:path')
//   		.attr('class', 'state')
//   		.attr('d', d3.select(paths.item(i)).attr('d'))
//   		.attr('transform', 'translate(-500 -500)scale(1 1.7)')
//   }
// });