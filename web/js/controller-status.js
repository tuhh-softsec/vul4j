function controller_status(data_source){
/*    var data = [{name:'onos9vpc',  onos: 1, cassandra: 1},
		{name:'onos10vpc', onos: 0, cassandra: 1},
		{name:'onos11vpc', onos: 1, cassandra: 1},
		{name:'onos12vpc', onos: 0, cassandra: 1}] */

    var barWidth = 100;
    var width = (barWidth + 10) * 4
    var height = 50;

    var Servers = d3.select("#servers").
	append("svg:svg").
	attr("width", 800).
	attr("height", 30);

    var ContStatus = d3.select("#onos-status").
	append("svg:svg").
	attr("width", 800).
	attr("height", 50);

    var CassandraStatus = d3.select("#cassandra-status").
	append("svg:svg").
	attr("width", 800).
	attr("height", 50);

    d3.json(data_source, draw);
    setInterval(function() {
        $.ajax({
	    url: data_source,
	    success: function(json) {
		draw(json)
	    },
	    dataType: "json"
        });
    }, 5000); 

    function draw(json){
//	var data = json.data;
	var data = json;
	var server = Servers.selectAll("text").data(data);
	var controller_rect = ContStatus.selectAll("rect").data(data);
	var controller_text = ContStatus.selectAll("text").data(data);
	var cassandra_rect = CassandraStatus.selectAll("rect").data(data);
	var cassandra_text = CassandraStatus.selectAll("text").data(data);

	var x = d3.scale.linear().domain([0, data.length]).range([0, width]);
	var y = d3.scale.linear().domain([0, d3.max(data, function(datum) { return datum.onos; })]).rangeRound([0, height]);
	var y2 = d3.scale.linear().domain([0, d3.max(data, function(datum) { return datum.cassandra; })]).rangeRound([0, height]);

	console.log(data)
	server.
	    enter().
	    append("svg:text").
	    attr("x", function(datum, index) { return x(index); }).
	    attr("y", function(datum) { return 20; }).
	    attr("fill", function(datum, index) { 
		if (index == 0){
		    return "red"
		}else if (index == 1){
		    return "blue"
		}else if (index == 2){
		    return "green"
		}else if (index == 3){
		    return "orange"
		}else{
		    return "black"
		}
	    }).
	    text(function(datum) { return datum.name; });

	controller_rect.
	    enter().
	    append("svg:rect").
	    attr("x", function(datum, index) { return x(index); }).
	    attr("y", function(datum) { return height - y(datum.onos); }).
	    attr("height", function(datum) { return y(datum.onos); }).
	    attr("width", barWidth).
	    attr("fill", function(datum, index) { 
		if (index == 0){
		    return "red"
		}else if (index == 1){
		    return "blue"
		}else if (index == 2){
		    return "green"
		}else if (index == 3){
		    return "orange"
		}else{
		    return "black"
		}
	    });

	controller_text.
	    enter().
	    append("svg:text").
	    text(function(){return "ONOS"}).
	    attr("x", function(datum, index) { return x(index)+10; }).
	    attr("y", function(datum) { return 30 ; }).
	    attr("height", function(datum) { return y(datum.onos); }).
	    attr("width", barWidth).
	    attr('fill', 'white'); 

	cassandra_rect.
	    enter().
	    append("svg:rect").
	    attr("x", function(datum, index) { return x(index); }).
	    attr("y", function(datum) { return height - y2(datum.cassandra); }).
	    attr("height", function(datum) { return y2(datum.cassandra); }).
	    attr("width", barWidth).
	    attr("fill", "#aa0000");

	cassandra_text.
	    enter().
	    append("svg:text").
	    text(function(){return "Cassandra"}).
	    attr("x", function(datum, index) { return x(index); }).
	    attr("y", function(datum) { return 30 ; }).
	    attr("height", function(datum) { return y(datum.onos); }).
	    attr("width", barWidth).
	    attr('fill', 'white'); 


	controller_rect.
	    attr("x", function(datum, index) { return x(index); }).
	    attr("y", function(datum) { return height - y(datum.onos); }).
	    attr("height", function(datum) { return y(datum.onos); }).
	    attr("width", barWidth).
	    attr("fill", function(datum, index) { 
		if (index == 0){
		    return "red"
		}else if (index == 1){
		    return "blue"
		}else if (index == 2){
		    return "green"
		}else if (index == 3){
		    return "orange"
		}else{
		    return "black"
		}
	    });

	controller_text.
	    text(function(){return "ONOS"}).
	    attr("x", function(datum, index) { return x(index)+10; }).
	    attr("y", function(datum) { return 30 ; }).
	    attr("height", function(datum) { return y(datum.onos); }).
	    attr("width", barWidth).
	    attr('fill', 'white'); 

	cassandra_rect.
	    attr("x", function(datum, index) { return x(index); }).
	    attr("y", function(datum) { return height - y2(datum.cassandra); }).
	    attr("height", function(datum) { return y2(datum.cassandra); }).
	    attr("width", barWidth).
	    attr("fill", "#aa0000");

	cassandra_text.
	    text(function(){return "Cassandra"}).
	    attr("x", function(datum, index) { return x(index); }).
	    attr("y", function(datum) { return 30 ; }).
	    attr("height", function(datum) { return y(datum.cassandra); }).
	    attr("width", barWidth).
	    attr('fill', 'white'); 


	server.exit().remove();
	controller_rect.exit().remove();
	controller_text.exit().remove();
	cassandra_rect.exit().remove();
	cassandra_text.exit().remove();

    }
/*
    $("#more").click( function() {
        $.ajax({
	    url: 'http://gui.onlab.us:8080/controller_status1',
	    success: function(json) {
		draw(json);
	    },
	    dataType: "json"
        });
    });
    $("#less").click( function() {
        $.ajax({
	    url: 'http://gui.onlab.us:8080/controller_status2',
	    success: function(json) {
		draw(json);
	    },
	    dataType: "json"
        });
    });
*/
}

