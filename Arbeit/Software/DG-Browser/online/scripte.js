// JavaScript Document
var ge = null;
var dragInfo = null;
var placemark;
var lastmarker;
var iwform = 'Für diese Position muß noch der Name eines Wikieintrags festgelegt werden:<br>'
    +  '<form name="Eingabe">'
	+ '  <input name="InputFormData" type="text" /><br />'
    + '  <input type="button" value="Save" onClick="process(this.form.InputFormData.value);" />'
    +  '</form>';
var sammelpfad; // wird ein linestr
var pfadmodus; // wahr oder falsch


// Fehlerausgabe
function debugfenster(out){
	document.getElementById('testoutdiv').innerHTML = out;
//	alert(out);
}
							 
							 
// läd die GoogleEarthApi
google.load("earth", "1");

// Befiehlt google eine neue Plugininstanz zu starten bei Erfolg Mission initCallback zu beginnen
function init() {
	google.earth.createInstance("map3d", initCallback, failureCallback);

}

function initCallback(object) {
	ge = object;
	ge.getWindow().setVisibility(true);

	// startgebiet	
	
	sammelpfad = ge.createLineString ("Sammelpfad");
	
	// hintergrund dere.KML laden
	loadDereKmz();

	// Mousclicks irgendwohin
	if (!window.globeWindowEventListener) {
		window.globeWindowEventListener = function(event) {
			if (event.getShiftKey()) {
				// Erzeugt, wenn Shift gedrückt ist einen Marker
				createEingabeMarker(event.getLatitude(), event.getLongitude());
//				createMarker("new","test",event.getLatitude(), event.getLongitude());
			}
		}
	} // Ende des irgendwoclicks
		
	google.earth.addEventListener(ge.getGlobe(), "mousedown", window.globeWindowEventListener);
	
   
	// listen for mousedown on the window (look specifically for point placemarks)
	google.earth.addEventListener(ge.getWindow(), 'mousedown', function(event) {
		// wenn der Pfadmodus an ist																
		if (pfadmodus) {	
			sammelpfad.getCoordinates().pushLatLngAlt(event.getLatitude(), event.getLongitude(), event.getAltitude()); 
			debug (line2string(sammelpfad));
		} // Ende PFADMODUS
		
		
		// Wenn es ein Marker ist:
		if (event.getTarget().getType() == 'KmlPlacemark' &&
			event.getTarget().getGeometry().getType() == 'KmlPoint') {
			lastmarker = event.getTarget().getGeometry();
			// Wenn ctrl gedrückt ist
			if (event.getCtrlKey()) {
				var placemark = event.getTarget();
				dragInfo = {
					placemark: event.getTarget(),
					dragged: false
				};
			} //Ende if ctrl
		} // Ende Markerif
	}); // ENDE MousedownEvent
	
	// MousemoveEvent
	google.earth.addEventListener(ge.getGlobe(), 'mousemove', function(event) {
	  if (event.getCtrlKey()) {																	
		  if (dragInfo) {
			event.preventDefault();
			var point = dragInfo.placemark.getGeometry();
			point.setLatitude(event.getLatitude());
			point.setLongitude(event.getLongitude());
			dragInfo.dragged = true;
		  }
	  }
	});
	
	// Mouseupevent
	google.earth.addEventListener(ge.getWindow(), 'mouseup', function(event) {
	  	if (event.getCtrlKey()) {
			if (dragInfo) {
				if (dragInfo.dragged) {
					  // if the placemark was dragged, prevent balloons from popping up
					  event.preventDefault();
				}
				dragInfo = null;
			}
		}
	}); // ENDE Mouseupevent
	
// ENDE des Startgebietes		  
} // Ende initCallback

function failureCallback(object) {alert("Fehler beim GE-Laden");}

	//statische kmls einbinden
function loadDereKmz(){
//	var nix = loadKmz("http://www.dereglobus.orkenspalter.com/public/kml/bg.kml");
	var nix = loadKmz("http://www.dereglobus.orkenspalter.com/public/kml/link.kml");
//	var nix = loadKmz("http://www.dereglobus2.orkenspalter.com/download/Netzwerklink.kmz");
}    

function loadKmz(inUrl){
	var networkLink = ge.createNetworkLink("");
	networkLink.setDescription("NetworkLink open to fetched content");
	networkLink.setName("Open NetworkLink");
	networkLink.setFlyToView(true);  
	var link = ge.createLink("");
	link.setHref(inUrl);
	networkLink.setLink(link);
	ge.getFeatures().appendChild(networkLink);
}    
	
	
/*
 method : POST/GET
 url    : Call url
 func   : custom function which is used to process returned data,
		  take only one parameter
*/
function CDownloadUrl(method, url, func) {
 var httpObj;
 var browser = navigator.appName;
 if(browser.indexOf("Microsoft") > -1)
	httpObj = new ActiveXObject("Microsoft.XMLHTTP");
 else
	httpObj = new XMLHttpRequest();
 httpObj.open(method, url, true);
 httpObj.onreadystatechange = function() {
	if(httpObj.readyState == 4){
	   if (httpObj.status == 200) {
		  var contenttype = httpObj.getResponseHeader('Content-Type');
		  if (contenttype.indexOf('xml')>-1) {
			 func(httpObj.responseXML);
		  } else {
			 func(httpObj.responseText);
		  }
	   } else {
		  func('Error: '+httpObj.status);
	   }
	 }
  };
 httpObj.send(null);
}



// Läd die daten aus der phpdatei	  
// UNFERTIG 
// FUNKTIONIERT NICHT ...
// XML PARSER NICHT VORHANDEN
function loadphpxml() {	  
	GDownloadUrl("phpsqlajax_genxml.php", function(data) {
	  var xml = data;
	  var markers = xml.documentElement.getElementsByTagName("marker");
	  for (var i = 0; i < markers.length; i++) {
		var name = markers[i].getAttribute("name");
		var address = markers[i].getAttribute("address");
		var type = markers[i].getAttribute("type");
		createMarker (name, adress,parseFloat(markers[i].getAttribute("lat")), parseFloat(markers[i].getAttribute("lng")))
		document.getElementById('testoutdiv').innerHTML = "name=" + name +" adresse=" + adress + "lat" + parseFloat(markers[i].getAttribute("lat")) + " lng=" + parseFloat(markers[i].getAttribute("lng"));
	  }
	});
} //END func loadphpxml */
	

// Läd aus den Textfelder saveort
function saveData() {
	var name = escape(document.getElementById("in_name").value);
	var adress = escape(document.getElementById("in_address").value);
	var type = document.getElementById("in_type").value;
	var lat = document.getElementById("in_lat").value;
	var lng = document.getElementById("in_lng").value;
	saveOrt(name, adress, type, lat, lng);
}
// Speichert einen Ort in der SQL-Datenbank
function saveOrt(inName, adress, type, lat, lng) {
	var murl = "action=save&titel=" + inName + 
				"&adress=" + adress + 
				"&type=" + type + 
				"&lat=" + lat + 
				"&lng=" + lng;
	setRequest(murl);
}
	


// DIE LÖSUNG IST VON HIER : http://ajax.frozenfox.at/ajax_002.html
var request = false;
// Request senden
function setRequest(value) {
	// Request erzeugen
	if (window.XMLHttpRequest) {request = new XMLHttpRequest();} // Mozilla, Safari, Opera
	else if (window.ActiveXObject) {
		try {request = new ActiveXObject('Msxml2.XMLHTTP');} // IE 5
		catch (e) {try {request = new ActiveXObject('Microsoft.XMLHTTP');} // IE 6
				   catch (e) {}				} 
	} //Ende elseif
	// überprüfen, ob Request erzeugt wurde
	if (!request) {
		alert("Kann keine XMLHTTP-Instanz erzeugen");
		return false;
	} else {
		var url = "save.php";
		// Request öffnen
		request.open('post', url, true);
		// Requestheader senden
		request.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		// Request senden
		request.send(value);
		// Request auswerten
		request.onreadystatechange = interpretRequest;   		} // ENDE ELSE
} // Ende setRequest
// Request auswerten
function interpretRequest() {
	switch (request.readyState) {
		// wenn der readyState 4 und der request.status 200 ist, dann ist alles korrekt gelaufen
		case 4:
			if (request.status != 200) {
				alert("Der Request wurde abgeschlossen, ist aber nicht OK\nFehler:"+request.status);
			} else {
				var content = request.responseText;
				// den Inhalt des Requests in das <div> schreiben
				document.getElementById('testoutdiv').innerHTML = content;	} 
			break;
		default:
			break;
	}
} // Ende interpret Request
//-->




// erzeugt einen Marker mit eingabefeld
function createEingabeMarker(inLat, inLon){
	createMarker("NeuePosition", iwform, inLat, inLon);
	openBalloon(aktuellerOrt);
}

// Rückgabefunction Namensänderung
function process(inName) {
	var details = inName;
	var lat = lastmarker.getLatitude();
	var lng = lastmarker.getLongitude();
	ge.setBalloon(null);
	saveOrt(details, details, "neu", lat, lng);
//      == remove the input marker and replace it with a completed marker
//      map.closeInfoWindow();
//      var marker = createMarker(lastmarker.getPoint(),details);
//      GEvent.trigger(marker,"click");
}

// erzeugt einen Marker
var aktuellerOrt = null;
function createMarker(inName, inText, inLat, inLon){
	aktuellerOrt = ge.createPlacemark('');
	aktuellerOrt.setName(inName);
	ge.getFeatures().appendChild(aktuellerOrt);
	// Create style map for placemark
	var normal = ge.createIcon('');
	normal.setHref('http://maps.google.com/mapfiles/kml/paddle/red-circle.png');
	var iconNormal = ge.createStyle('');
	iconNormal.getIconStyle().setIcon(normal);
	var highlight = ge.createIcon('');
	highlight.setHref('http://maps.google.com/mapfiles/kml/paddle/red-circle.png');
	var iconHighlight = ge.createStyle('');
	iconHighlight.getIconStyle().setIcon(highlight);
	var styleMap = ge.createStyleMap('');
	styleMap.setNormalStyle(iconNormal);
	styleMap.setHighlightStyle(iconHighlight);
	aktuellerOrt.setStyleSelector(styleMap);
	  
	// Create point
	var point = ge.createPoint('');
	point.setLatitude(Number(inLat));
	point.setLongitude(Number(inLon));
	
/*	var la = ge.getView().copyAsLookAt(ge.ALTITUDE_RELATIVE_TO_GROUND);
	var point = ge.createPoint('');
	point.setLatitude(la.getLatitude());
	point.setLongitude(la.getLongitude());*/
	aktuellerOrt.setGeometry(point);
	aktuellerOrt.setDescription(inText);
	
	// Immer wend draufgeklickt wird, soll ein Ballon geöffnet werden
	google.earth.addEventListener(aktuellerOrt, 'click', function(event) {
		// prevent the default balloon from popping up
		lastmarker = event.getTarget().getGeometry();
		event.preventDefault();
		openBalloon(event.getTarget());
	}); // Ende des Ballonaufmachevents
} // Ende von CreateMarker

function openBalloon(inTarget){
	var balloon = ge.createHtmlStringBalloon('');
	balloon.setFeature(inTarget);
	balloon.setMaxWidth(300);
	// Einbau der Description
	balloon.setContentString(inTarget.getDescription());
	ge.setBalloon(balloon);	
}

function GetAllMarkers() {
//  var all = ge.getFeatures().getChildNodes(); // ist ein GEFeatureContainer 
//	
}

function Ortsuche(inName) {
;
}
function addToLineString(lineString, lat, lng, latOffset, lngOffset) {
  lineString.getCoordinates().pushLatLngAlt(
      lat + latOffset, lng + lngOffset, 0);
}

function Pfadinitiieren() {
// create the linestring geometry
var lineString = ge.createLineString('');
lineString.setTessellate(true);	
window.sammelpfad = lineStringPlacemark;
}
function Pfadzeigen() {
// sammelpfad.show
// create the linestring placemark and add it to Earth
ge.getFeatures().appendChild(lineStringPlacemark);
}

// Serialisierung von Pfaden
// grundlage ist das KmlCoordArray
// x1,y1,z1;x2,y2,z2;x3,y3,z3
function pkt2string(inpkt) {
	return inpkt.getLatitude()+","+inpkt.getLongitude()+","+inpkt.getAltitude ();
}
function string2pkt(istr) {
	var tmpkmlcoord;
	tmpkmlcoord = new KmlCoord;
	tmpkmlcoord.setLatLngAlt (istr.split(",")[0],istr.split(",")[1],istr.split(",")[2]);
	return tmpkmlcoord;
}

// kmlcoordarray als text serialisieren
function pfad2string(pfad) {
	var tmpstr = "";
	var tmparray = pfad.getCoordinates(); // sollte jetzt ein kmlcoordarray sein
	for (i=0;i<=tmparray.getLength();i++) 
	{
		tmpstr = tmpstr + pkt2str(tmparray.pop()) + ";";
	} 
	return tmpstr;
}
// pfad sollte schon ein kmlcoordarray sein
function string2pfad(istr, pfad) {
	sitr = istr.split(";");
	var tmpcoordarray;
	tmpcoordarray = new KmlCoordArray;
	for (i=0;i<=sitr.length();i++) {tmpcoordarray.push(strin2pkt(istr[i]));}
	return tmpcoordarray;
}

// linestrings als texte serialisieren
// name:x,y,z,x,y,z
function line2string(iline) {return iline.getId() + ":" + pfad2string(iline.getCoordinates());}
function string2line(istr) {
	var tline = ge.createLineString(istr.split(":")[0]);
	istr = istr.split(":")[1].split(";");
	for (i=0;i<=sitr.length();i++) {tline.getCoordinates().push(strin2pkt(istr[i]));}
	return tline;
}