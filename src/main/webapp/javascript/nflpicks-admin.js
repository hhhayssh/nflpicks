
$(document).ready(
	function(){
		checkKey();
});

function checkKey(){
	
	var urlParameters = getUrlParameters();
		
	var key = urlParameters.key;
	
	$.ajax({url: 'nflpicks?target=admin&key=' + key,
		contentType: 'application/json; charset=UTF-8'}
	)
	.done(function(data) {
		var statusContainer = $.parseJSON(data);
		
		if (statusContainer.error){
			$(document.body).empty();
			$(document.body).append('<div>Interception!  40 ... 30 ... 25 ... 20 ... He could go ... all ... the ... way! </div>');
			return;
		}
		else if (isDefined(statusContainer.status) && statusContainer.status == 'SUCCESS'){
			
		}
		else {
			$(document.body).empty();
			$(document.body).append('<div>Interception!  40 ... 30 ... 25 ... 20 ... He could go ... all ... the ... way! </div>');
			return;
		}
	})
	.fail(function() {
		$(document.body).empty();
		$(document.body).append('<div>Error!</div>');
	})
	.always(function() {
	});
}


function onClickReinitialize(){
	
	updateStatus('reinitializeStatusContainer', 'Reinitializing...');
	
	disableLink('reinitializeLinkContainer', 'reinitializeStatusContainer', 'Reinitializing...');
	
	var urlParameters = getUrlParameters();
			
	var key = urlParameters.key;
	
	$.ajax({url: 'admin?target=reinitialize&key=' + key, contentType: 'application/json; charset=UTF-8'})
	.done(function(data) {
		var result = $.parseJSON(data);
		
		if (result.status == 'SUCCESS'){
			updateStatus('reinitializeStatusContainer', 'Successfully reinitialized.');	
		}
		else {
			updateStatus('reinitializeStatusContainer', 'Unknown error when reinitializing.');
		}
		
		enableLink('reinitializeLinkContainer', 'reinitializeStatusContainer', false);
	})
	.fail(function() {
		updateStatus('Failed to reinitialize');
		enableLink('reinitializeLinkContainer', 'reinitializeStatusContainer', false);
	})
	.always(function() {
		enableLink('reinitializeLinkContainer', 'reinitializeStatusContainer', false);
	});
}

function disableLink(linkContainerId, linkStatusContainerId, message){
	
	$('#' + linkContainerId).hide();
	$('#' + linkStatusContainerId).empty();
	$('#' + linkStatusContainerId).append(message);
	$('#' + linkStatusContainerId).show();
	
}

function enableLink(linkContainerId, linkStatusContainerId, hideStatus){
	
	$('#' + linkContainerId).show();
	
	if (hideStatus){
		$('#' + linkStatusContainerId).hide();
	}
}

function updateStatus(statusContainerId, status){
	$('#' + statusContainerId).empty();
	$('#' + statusContainerId).append(status);
}