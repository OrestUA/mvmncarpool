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

function handleForm(url, formId, fields, success, failure, json) {
	updateFormSubmitButton(formId, true);

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
			updateFormSubmitButton(formId, false);
	
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
			updateFormSubmitButton(formId, false);
	
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
	handleForm("/login", "signInForm", 
		[ 
			{ id:"signinFormEmail", clear: { success:true } },
			{ id:"signinFormPassword", clear: { success:true, failure: true } }
		],  
		function() { window.location.href = window.siteBaseUrl + '/'; }, 
		function() { showPopup(exclamationSign + " " + window.l10n['error.bad_username_or_password']); }
	);
}

function doRegister() {
	handleForm("/register", "signUpForm", 
		[ { id:"signinFormEmail", clear: { success:true } } ],  
		function() { showPopup(window.l10n['message.register_send_success']); }, 
		function(data) { showPopup(exclamationSign + " " + data.responseJSON.message); }
	);
}

function doResetPassword() {
	handleForm("/reset_password", "resetPasswordForm", 
		[ { id:"signinFormEmail", clear: { success:true } } ],  
		function() { showPopup(window.l10n['message.password_reset_send_success']); }, 
		function(data) { showPopup(exclamationSign + " " + data.responseJSON.message); }
	);
}

function doSetNewPassword() {
	handleForm("/set_new_password", "btnSetNewPassword", 
		[ { id:"resetPwdConfirmationId" }, { id:"resetPwdEmail" }, { id:"resetPwdPassword" }, { id:"resetPwdPasswordConfirmation" }, { id:"resetPwdFullName" } ],  
		function() { window.location.href = window.siteBaseUrl + '/'; }, 
		function(data) { showPopup(exclamationSign + " " + data.responseJSON.message); }
	);
}

function doUpdateProfile() {
	handleForm("/api/user/update", "btnUpdateProfile", 
		[ { id:"profileFullName" } ],  
		null, 
		function(data) { showPopup(exclamationSign + " " + data.responseJSON.message); },
		true
	);
}

function clearField(fieldId) {
	if($("#" + fieldId).length) {
		$("#" + fieldId).val('');
	}
}

function fillField(fieldId, object) {
	if ($("#" + fieldId).length) {
		object[$("#" + fieldId).attr("name")] = $("#" + fieldId).val();
	}
}

function updateFormSubmitButton(formId, submissionInProgress) {
	var hasInvalidFields = formInvalidFields[formId] && formInvalidFields[formId].length > 0; 
	formSubmitBtnSetState(formId + "_submit", submissionInProgress || hasInvalidFields, submissionInProgress);
}

function formSubmitBtnSetState(buttonId, disabled, inProgress) {
	if($("#"+buttonId).length) {
		if(disabled || inProgress) {
			$("#"+buttonId).attr("disabled", "disabled");
		} else {
			$("#"+buttonId).removeAttr("disabled");
		}
		if(inProgress) {
			$("#"+buttonId+" .waitIndicator").css('display', 'inline-block');
		} else {
			$("#"+buttonId+" .waitIndicator").css('display', 'none');
		}
	}
}

function validateNotEmpty(value) {
	return value && $.trim(value).length > 0 ? null : window.l10n['error.cannot_be_empty'];
}

var formInvalidFields = {};

function validateField(fieldId, validations, formIds) {
	if(validations && validations.length > 0 && $("#" + fieldId).length > 0) {
		for(var fidx in formIds) {
			var formFieldValidities = formInvalidFields[formIds[fidx]];
			if(!formFieldValidities) {
				formFieldValidities = []
				formInvalidFields[formIds[fidx]] = formFieldValidities;
			}
			var ffvIdx = formFieldValidities.indexOf(fieldId);
			if(ffvIdx >= 0) {
				formFieldValidities.splice(ffvIdx, 1);
			}		
		}
		
		var value = $("#" + fieldId).val();
		var validationMessages = [];
		for(var idx in validations) {
			var validationMessage = validations[idx].apply(null, [value]);
			if(validationMessage && validationMessage.length > 0) {
				validationMessages.push(validationMessage);
			}
		}
		if(validationMessages.length > 0) {
			var msg = validationMessages.join(", ")+".";
			$("#" + fieldId + "_validation").text(msg);
			$("#" + fieldId + "_validation").css('display', 'inline-block');
			
			for(var fidx in formIds) {
				formInvalidFields[formIds[fidx]].push(fieldId);
			}			
		} else {
			$("#" + fieldId + "_validation").css('display', 'none');
		}
		
		for(var fidx in formIds) {
			updateFormSubmitButton(formIds[fidx]);
		}
	}
}
