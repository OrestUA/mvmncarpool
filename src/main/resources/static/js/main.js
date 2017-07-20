var popupIndexCounter = 1;

function setLocale(locale, csrfToken) {
	$.post(siteBaseUrl + "/api/locale/" + locale + "?_csrf=" + csrfToken,
			function() {
				window.location.reload();
			});
}

var exclamationSign = '<i class="fa fa-exclamation-circle" aria-hidden="true"></i>';

function showPopup(content) {
	var popupId = 'popup_' + (popupIndexCounter++);
	
	// TODO: use jsrender templates
	var popup = $(
			'<div id="' + popupId + '" class="popup">'
			+'<div class="container"><div class="row"><div class="twelve columns">' + content + '</div></div>'
			+'<div class="row">&nbsp;</div><div class="row"><div class="twelve columns">'
			+'<input type="button" class="'+popupId+'_close u-full-width" value="' + window.l10n['label.close'] + '" /></div></div></div>'
		);
	$("body").append(popup);
	popup.popup({
		background : false,
		transition : 'all 0.3s',
		detach : true,
		autoopen : true
	});
}

function doLogin() {
	operationButtonSetState("btnLogin", true);

	var data = {};
	fillField("csrfToken", data);
	fillField("signinFormEmail", data);
	fillField("signinFormPassword", data);

	$.post(siteBaseUrl + "/login", data, function() {
		console.log("Success");
		console.log(arguments);
		operationButtonSetState("btnLogin", false);

		clearField("signinFormEmail");
		clearField("signinFormPassword");

		window.location.href = window.siteBaseUrl + '/';
	}).fail(function() {
		operationButtonSetState("btnLogin", false);
		clearField("signinFormPassword");

		showPopup(exclamationSign + " " + window.l10n['error.bad_username_or_password']);
	});
}

function doRegister() {
	operationButtonSetState("btnRegister", true);
	
	var data = {};
	fillField("csrfToken", data);
	fillField("signinFormEmail", data);

	$.post(siteBaseUrl + "/register", data, function() {
		console.log("Success");
		console.log(arguments);
		operationButtonSetState("btnRegister", false);

		clearField("signinFormEmail");
		
		showPopup(window.l10n['message.register_send_success']);
	}).fail(function() {
		console.log("Failure");
		console.log(arguments);
		operationButtonSetState("btnRegister", false);
		
		showPopup(exclamationSign + " " + arguments[0].responseJSON.message);
	});
}

function doResetPassword(emailOverride) {
	operationButtonSetState("btnResetPassword", true);

	var data = {};
	fillField("csrfToken", data);
	if(emailOverride) {
		data['email'] = emailOverride;
	} else {
		fillField("signinFormEmail", data);
	}

	$.post(siteBaseUrl + "/reset_password", data, function() {
		console.log("Success");
		console.log(arguments);
		operationButtonSetState("btnResetPassword", false);

		clearField("signinFormEmail");
		
		showPopup(window.l10n['message.message.password_reset_send_success']);
	}).fail(function() {
		console.log("Failure");
		console.log(arguments);
		operationButtonSetState("btnResetPassword", false);
		
		showPopup(exclamationSign + " " + arguments[0].responseJSON.message);
	});
}

function doSetNewPassword() {
	operationButtonSetState("btnSetNewPassword", true);

	var data = {};
	fillField("csrfToken", data);
	fillField("resetPwdConfirmationId", data);
	fillField("resetPwdEmail", data);
	fillField("resetPwdPassword", data);
	fillField("resetPwdPasswordConfirmation", data);
	fillField("resetPwdFullName", data);

	$.post(siteBaseUrl + "/set_new_password", data, function() {
		console.log("Success");
		console.log(arguments);
		operationButtonSetState("btnSetNewPassword", false);

		window.location.href = window.siteBaseUrl + '/';
	}).fail(function() {
		console.log("Failure");
		console.log(arguments);
		operationButtonSetState("btnSetNewPassword", false);
		
		showPopup(exclamationSign + " " + arguments[0].responseJSON.message);
	});
}

function doUpdateProfile() {
	operationButtonSetState("btnUpdateProfile", true);

	var userDto = {};
	fillField('profileFullName', userDto);
	
	var headers = {}
	headers[	window.csrfHeaderName] = $("#csrfToken").val();

	$.ajax({
		method : 'POST',
		url : siteBaseUrl + "/api/user/update",
		headers : headers,
		data : userDto,
		success : function() {
			console.log("Success");
			console.log(arguments);
			operationButtonSetState("btnUpdateProfile", false);
		},
		error : function() {
			console.log("Failure");
			console.log(arguments);
			operationButtonSetState("btnUpdateProfile", false);

			showPopup(exclamationSign + " "
					+ arguments[0].responseJSON.message);
		}
	});
}

function clearField(fieldId) {
	if($("#" + fieldId)) {
		$("#" + fieldId).val('')
	}
}

function fillField(fieldId, object) {
	if ($("#" + fieldId)) {
		object[$("#" + fieldId).attr("name")] = $("#" + fieldId).val();
	}
}

function operationButtonSetState(buttonId, state) {
	if($("#"+buttonId)) {
		if(state) {
			$("#"+buttonId).attr("disabled", "disabled");
			$("#"+buttonId+" .waitIndicator").css('display', 'inline-block');
		} else {
			$("#"+buttonId+" .waitIndicator").css('display', 'none');
			$("#"+buttonId).removeAttr("disabled");
		}
	}
}