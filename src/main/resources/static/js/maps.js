function centerMapOnCurrentLocation() {
	if (navigator && "geolocation" in navigator) {
		navigator.geolocation.getCurrentPosition(function(position) {
			window.carpoolApp.mainMap.setCenter(new google.maps.LatLng(position.coords.latitude, position.coords.longitude));
		});
	}	
}
