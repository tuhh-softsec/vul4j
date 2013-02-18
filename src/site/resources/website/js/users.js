// Image size : 100x44
var $ = jQuery.noConflict();
 
var users = [{
	   "img": "apec.png",
	   "title": "APEC",
	   "url": "http://www.apec.fr",
	   "weight": Math.random()
	}, {
		"img": "capgemini.png",
		   "title": "Capgemini",
		   "url": "http://www.capgemini.com",
		   "weight": Math.random()
//	}, {
//		"img": "carif-oref.png",
//		   "title": "Carif-Oref",
//		   "url": "http://www.meformer.org",
//		   "weight": Math.random()
	}, {
		"img": "hec.png",
		   "title": "HEC Paris",
		   "url": "http://www.hec.fr",
		   "weight": Math.random()
//	}, {
//		"img": "idmacif.png",
//		   "title": "idmacif",
//		   "url": "http://www.idmacif.fr",
//		   "weight": Math.random()
	}, {
		"img": "smile.png",
		   "title": "Smile",
		   "url": "http://www.smile.fr",
		   "weight": Math.random()
	}, {
		"img": "sncf.png",
		   "title": "SNCF",
		   "url": "http://www.sncf.com",
		   "weight": Math.random()
	}, {
		"img": "vsc.png",
		   "title": "voyages-sncf.com",
		   "url": "http://www.voyages-sncf.com",
		   "weight": Math.random()
//	}, {
//		"img": "ag2r.png",
//		   "title": "AG2R La mondiale",
//		   "url": "http://www.ag2rlamondiale.fr",
//		   "weight": Math.random()
	}, {
		"img": "manitou.png",
		   "title": "Manitou Group",
		   "url": "http://www.manitou-group.fr",
		   "weight": Math.random()
	}, {
		"img": "nantes-metropole.png",
		   "title": "Nantes Métropole",
		   "url": "http://www.nantesmetropole.fr",
		   "weight": Math.random()
	}];

users.sort(function(a,b) { return parseFloat(a.weight) - parseFloat(b.weight) } );


$(document).ready(function() {
	
	var itemsNb = Math.floor( users.length / 6);
	
	for (var i = 0; i < itemsNb ; i++) {

		var newDiv = $('<ul class="item esi-users">');
		
		if( i == 0){
			newDiv.addClass( "active");
		}
		newDiv.appendTo('#usersCarousel .carousel-inner' );
		for( var j = i * 6; j < (i+1)*6; j ++ ){
			newDiv.append('<li><img src="website/img/users/'+users[j].img+'" title="'+users[j].title+'" /></li>'  );
//			newDiv.append('<li><a href="'+users[j].url+'"><img src="img/users/'+users[j].img+'" title="'+users[j].title+'" /></a></li>'  );
		}
	}	
	
	$('#usersCarousel').carousel({
		interval: 10000
	});
});
 

