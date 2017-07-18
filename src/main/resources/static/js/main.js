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
	
	// TODO/FIXME: localize close button, use jsrender templates
	var popup = $(
			'<div id="' + popupId + '" class="popup">'
			+'<div class="container"><div class="row"><div class="twelve columns">' + content + '</div></div>'
			+'<div class="row">&nbsp;</div><div class="row"><div class="twelve columns">'
			+'<input type="button" class="'+popupId+'_close u-full-width" value="close" /></div></div></div>'
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
		// FIXME: l10n
		showPopup(exclamationSign + " Invalid username or password");
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
	}).fail(function() {
		console.log("Failure");
		console.log(arguments);
		operationButtonSetState("btnRegister", false);
		showPopup(exclamationSign + " " + arguments[0].responseJSON.message);
	});
}

function doResetPassword() {
	operationButtonSetState("btnResetPassword", true);

	var data = {};
	fillField("csrfToken", data);
	fillField("signinFormEmail", data);

	$.post(siteBaseUrl + "/reset_password", data, function() {
		console.log("Success");
		console.log(arguments);
		operationButtonSetState("btnResetPassword", false);

		clearField("signinFormEmail");
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

function clearField(fieldId) {
	$("#" + fieldId).val('')
}

function fillField(fieldId, object) {
	if ($("#" + fieldId)) {
		object[$("#" + fieldId).attr("name")] = $("#" + fieldId).val();
	}
}

function operationButtonSetState(buttonId, state) {
	if(state) {
		$("#"+buttonId).attr("disabled", "disabled");
		$("#"+buttonId+" .waitIndicator").css('display', 'inline-block');
	} else {
		$("#"+buttonId+" .waitIndicator").css('display', 'none');
		$("#"+buttonId).removeAttr("disabled");
	}
}