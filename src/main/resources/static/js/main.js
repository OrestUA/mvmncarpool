var popupIndexCounter = 1;

function setLocale(locale) {
	$.post(window.carpoolApp.siteBaseUrl + "/api/locale/" + locale + "?_csrf=" + window.carpoolApp.csrfToken, function() { window.location.reload(); });
}

function toggleCollapse(sectionId) {
	var hide = $("#"+sectionId+"_hider").is(":visible"); 
	$("#"+sectionId+"_hider").css('display', hide ? 'none' : 'inline-block');
	$("#"+sectionId+"_revealer").css('display', hide ? 'inline-block' : 'none');
	$("#"+sectionId).slideToggle(100);
}

function showPopup(content) {
	var popupId = 'popup_' + (popupIndexCounter++);	
	var template = $.templates("#popupTempalte");
	var popup = $(template.render({popupId: popupId, content: content, closeButtonLabel: window.carpoolApp.l10n['label.close'] }));

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

function showConfirmation(content, noLabel, yesLabel, callback) {
	var popupId = 'popup_' + (popupIndexCounter++);	
	var template = $.templates("#confirmDialogTempalte");
	var popup = $(template.render({popupId: popupId, content: content, noButtonLabel: window.carpoolApp.l10n[noLabel], yesButtonLabel: window.carpoolApp.l10n[yesLabel] }));
	
	$(popup).find(".confirmButton").click(callback);

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

function makeApiCall(url, method, data, success, failure, complete) {
	var headers = {};
	headers[	window.carpoolApp.csrfHeaderName] = window.carpoolApp.csrfToken;
	headers['Content-Type'] = 'application/json';
	
	if(!failure) {
		failure = function(xhr, status, error) { if(xhr.responseJSON) { showPopup(exclamationSign + " " + xhr.responseJSON.message); } else { showPopup(exclamationSign + " " + xhr.statusText + " " + xhr.status); } }
	}
	
	$.ajax({
		method : method,
		url : window.carpoolApp.siteBaseUrl + url,
		headers : headers,
		data : data ? JSON.stringify(data) : null,
		success : success,
		error : failure,
		complete : complete
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
	headers[	window.carpoolApp.csrfHeaderName] = window.carpoolApp.csrfToken;
	
	if(json) {
		data = JSON.stringify(data);
		headers['Content-Type'] = 'application/json';
	}	
	$.ajax({
		method : 'POST',
		url : window.carpoolApp.siteBaseUrl + url,
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
		function() { window.location.href = window.carpoolApp.siteBaseUrl + '/'; }, 
		function() { showPopup(exclamationSign + " " + window.carpoolApp.l10n['error.bad_username_or_password']); }
	);
}

function doRegister() {
	var sendData = { email: $('#signinFormEmail').val() };
	sendData[window.carpoolApp.csrfParameterName] = window.carpoolApp.csrfToken;
	$.post(window.carpoolApp.siteBaseUrl + '/check_email', sendData, function(data) {
		if(data) {
			data = data.toLowerCase();
		}
		if(data == 'ok') {
			handleForm("/register", "signUpForm", 
				[ { id:"signinFormEmail", clear: { success:true } } ],  
				function() { showPopup(window.carpoolApp.l10n['message.register_send_success']); }, 
				function(xhr) { showPopup(exclamationSign + " " + xhr.responseJSON.message); }
			);
		} else {
			showPopup(exclamationSign + " " + window.carpoolApp.l10n['error.email.' + data]);
		}
	});
}

function doResetPassword() {
	handleForm("/reset_password", "resetPasswordForm", 
		[ { id:"signinFormEmail", clear: { success:true } } ],  
		function() { showPopup(window.carpoolApp.l10n['message.password_reset_send_success']); }, 
		function(xhr) { showPopup(exclamationSign + " " + xhr.responseJSON.message); }
	);
}

function doSetNewPassword() {
	handleForm("/set_new_password", "btnSetNewPassword", 
		[ { id:"resetPwdConfirmationId" }, { id:"resetPwdEmail" }, { id:"resetPwdPassword" }, { id:"resetPwdPasswordConfirmation" }, { id:"resetPwdFullName" } ],  
		function() { window.location.href = window.carpoolApp.siteBaseUrl + '/'; }, 
		function(xhr) {
			if(xhr.responseJSON && xhr.responseJSON.errors) {
				var errorMessages = [];
				for(var eridx in xhr.responseJSON.errors) {
					errorMessages.push(window.carpoolApp.l10n[xhr.responseJSON.errors[eridx]]);					
				}
				showPopup(exclamationSign + " " + errorMessages.join(". "));
			} else {
				showPopup(exclamationSign + " " + window.carpoolApp.l10n['generic_error']);
				console.error(xhr);
			}
		}
	);
}

function doUpdateProfile() {
	handleForm("/api/user/update", "profileUpdateForm", 
		[ { id:"profileFullName" } ],  
		null, 
		function(xhr) { showPopup(exclamationSign + " " + xhr.responseJSON.message); },
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
	return value && $.trim(value).length > 0 ? null : window.carpoolApp.l10n['error.cannot_be_empty'];
}

function validateEmail(value) {
	return window.carpoolApp.emailRegex.test(value) ? null : window.carpoolApp.l10n['error.email.invalid'];
}

function validatePasswordChars(value) {
	return window.carpoolApp.passwordRegex.test(value) ? null : window.carpoolApp.l10n['error.password.bad_characters'];
}

function fnValidateLength(minLength, maxLength, tooShortErrorKey, tooLongErrorKey) {
	return function(value) {
		var valLen = value? value.toString().length : 0;
		if(valLen<minLength) return window.carpoolApp.l10n[tooShortErrorKey];
		if(valLen>maxLength) return window.carpoolApp.l10n[tooLongErrorKey];
		return null;
	}
}

function fnValidateMatches(checkFieldId, localizationKey) {
	return function(value) {
		return value == $("#" + checkFieldId).val() ? null : window.carpoolApp.l10n[localizationKey];
	}
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
				break;
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

function fixNumericInput(input) {
	if(input) {
		if(input.value) {
			var newVal = input.value.replace(/[^0-9]/g, '');
			if(newVal != input.value) {
				input.value = newVal;
			}
		} else {
			input.value = "1";
		} 		
	}
}