function deleteVehicle(id) {
	showConfirmation(window.carpoolApp.l10n['label.confirm_delete_vehicle'].replace('{0}', $("#vehicle_name_"+id).val()), 'label.no', 'label.yes', function (){
		formSubmitBtnSetState("btn_delete_vehicle_"+id, true, true);
		makeApiCall("/api/vehicles/"+id, 'DELETE', null, function() {
			$("#vehicleRow_"+id).remove();
		});
	}, null, function() {
		formSubmitBtnSetState("btn_delete_vehicle_"+id, false, false);
	});
}

function updateVehicle(id) {
	var data = {
		id: id,
		name: $("#vehicle_name_"+id).val(),
		plateNumber: $("#vehicle_plate_"+id).val(),
		passengerSeats: parseInt($("#vehicle_seats_"+id).val()),
		description: $("#vehicle_desc_"+id).val(),
	};
	
	formSubmitBtnSetState("btn_update_vehicle_"+id, true, true);
	makeApiCall("/api/vehicles", 'POST', data, null, null, function() {
		formSubmitBtnSetState("btn_update_vehicle_"+id, false, false);
	});
}

function doAddVehicle() {
	var data = {
			id: 0,
			name: $("#newVehicleName").val(),
			plateNumber: $("#newVehiclePlate").val(),
			passengerSeats: parseInt($("#newVehicleSeats").val()),
			description: $("#newVehicleDescripton").val(),
		};
	
	formSubmitBtnSetState("btnAddVehicle", true, true);						
	makeApiCall("/api/vehicles", 'PUT', data, function(vehicleDto) {
		$("#profilePageCarList tbody").append($.templates("#vehicleRowTempalte").render(vehicleDto));
		$("#profile_addVehicleSection input").val("");
	}, null, function() {
		formSubmitBtnSetState("btnAddVehicle", false, false);
	});
}
