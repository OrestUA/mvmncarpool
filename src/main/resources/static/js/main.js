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
	var template = $.templates("#popupTempalte");
	var popup = $(template.render({popupId: popupId, content: content, closeButtonLabel: window.l10n['label.close'] }));

	$("body").append(popup);
	popup.popup({
		background : false,
		transition : 'all 0.3s',
		detach : true,
		autoopen : true,
		onclose: function() { 
			setTimeout(function() {
				// Workaround for leftover wrappers
				$("#"+popupId+"_wrapper").remove();
			}, 3000); 
		}
	});
}

function handleForm(url, submitButtonId, fields, success, failure, json) {
	operationButtonSetState(submitButtonId, true);

	var data = {};
	if(fields) {
		for(var idx in fields) {
			fillField(fields[idx].id, data);
		}
	}

	var headers = {};
	headers[	window.csrfHeaderName] = $("#csrfToken").val();
	
	if(json) {
		data = JSON.stringify(data);
		headers['Content-Type'] = 'application/json';
	}	
	$.ajax({
		method : 'POST',
		url : siteBaseUrl + url,
		headers : headers,
		data : data,
		success : function() {
			operationButtonSetState(submitButtonId, false);
	
			for(var idx in fields) {
				if(fields[idx].clear && fields[idx].clear.success) {
					clearField(fields[idx].id);
				} 
			}
	
			if(success) {
				success.apply(this, arguments);
			}
		},
		error: function() {
			operationButtonSetState(submitButtonId, false);
	
			for(var idx in fields) {
				if(fields[idx].clear && fields[idx].clear.failure) {
					clearField(fields[idx].id);
				}
			}
	
			if(failure) {
				failure.apply(this, arguments)
			}
		}
	});
}

function doLogin() {
	handleForm("/login", "btnLogin", 
		[ 
			{ id:"signinFormEmail", clear: { success:true } },
			{ id:"signinFormPassword", clear: { success:true, failure: true } }
		],  
		function() { window.location.href = window.siteBaseUrl + '/'; }, 
		function() { showPopup(exclamationSign + " " + window.l10n['error.bad_username_or_password']); }
	);
}

function doRegister() {
	handleForm("/register", "btnRegister", 
		[ { id:"signinFormEmail", clear: { success:true } } ],  
		function() { showPopup(window.l10n['message.register_send_success']); }, 
		function() { showPopup(exclamationSign + " " + arguments[0].responseJSON.message); }
	);
}

function doResetPassword() {
	handleForm("/reset_password", "btnResetPassword", 
		[ { id:"signinFormEmail", clear: { success:true } } ],  
		function() { showPopup(window.l10n['message.password_reset_send_success']); }, 
		function() { showPopup(exclamationSign + " " + arguments[0].responseJSON.message); }
	);
}

function doSetNewPassword() {
	handleForm("/set_new_password", "btnSetNewPassword", 
		[ { id:"resetPwdConfirmationId" }, { id:"resetPwdEmail" }, { id:"resetPwdPassword" }, { id:"resetPwdPasswordConfirmation" }, { id:"resetPwdFullName" } ],  
		function() { window.location.href = window.siteBaseUrl + '/'; }, 
		function() { showPopup(exclamationSign + " " + arguments[0].responseJSON.message); }
	);
}

function doUpdateProfile() {
	handleForm("/api/user/update", "btnUpdateProfile", 
		[ { id:"profileFullName" } ],  
		null, 
		function() { showPopup(exclamationSign + " " + arguments[0].responseJSON.message); },
		true
	);
}

function clearField(fieldId) {
	if($("#" + fieldId).length) {
		$("#" + fieldId).val('')
	}
}

function fillField(fieldId, object) {
	if ($("#" + fieldId).length) {
		object[$("#" + fieldId).attr("name")] = $("#" + fieldId).val();
	}
}

function operationButtonSetState(buttonId, state) {
	if($("#"+buttonId).length) {
		if(state) {
			$("#"+buttonId).attr("disabled", "disabled");
			$("#"+buttonId+" .waitIndicator").css('display', 'inline-block');
		} else {
			$("#"+buttonId+" .waitIndicator").css('display', 'none');
			$("#"+buttonId).removeAttr("disabled");
		}
	}
}