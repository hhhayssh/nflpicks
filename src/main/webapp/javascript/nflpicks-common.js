function isBlank(value){
	
	if (!isDefined(value)){
		return true;
	}
	
	if (value.trim().length == 0){
		return true;
	}
	
	return false;
}

function isDefined(value){
	if (value == null || value == undefined){
		return false;
	}
	
	return true;
}

function isEmpty(value){
	
	if (!isDefined(value)){
		return true;
	}
	
	if (value.length == 0){
		return true;
	}
	
	return false;
}

function createSelectHtml(selectId, options, selectedValue, cssClass, style){
	
	var selectHtml = '<select ';
	
	if (isDefined(selectId)){
		selectHtml = selectHtml + ' id="' + selectId + '" ';
	}
	
	if (isDefined(cssClass)){
		selectHtml = selectHtml + ' class="' + cssClass + '" ';
	}
	
	if (isDefined(style)){
		selectHtml = selectHtml + ' style="' + style + '" ';
	}
	
	selectHtml = selectHtml + '>';
	
	for (var index = 0; index < options.length; index++){
		var option = options[index];
		
		selectHtml = selectHtml + 
					 '<option value="' + option.value + '" ';
		
		if (option.value == selectedValue){
			selectHtml = selectHtml + ' selected ';
		}
		
		selectHtml = selectHtml + '>' + option.label + '</option>';
	}
	
	selectHtml = selectHtml + '</select>';
	
	return selectHtml;
}

function doesSelectHaveOptionWithValue(selectId, value){
	
	var option = $('#' + selectId + ' option[value="' + value + '"]');
	
	if (isDefined(option) && option.length > 0){
		return true;
	}
	
	return false;
}