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

function createSelectHtml0(selectId, options, selectedValue, cssClass, style){
	
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

function createSelectHtml(selectId, options, selectedValue, cssClass, style, onChange){
	
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
	
	if (isDefined(onChange)){
		selectHtml = selectHtml + ' onChange="' + onChange + '" ';
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

function setOptionsInSelect(selectId, options){
	
	$('#' + selectId).empty();
	
	for (var index = 0; index < options.length; index++){
		var option = options[index];
		var optionHtml = '<option ';
		
		if (isDefined(option.id)){
			optionHtml = optionHtml + ' id="' + option.id + '" ';
		}
		
		if (isDefined(option.value)){
			optionHtml = optionHtml + ' value="' + option.value + '" ';
		}
		
		optionHtml = optionHtml + '>' + option.label + '</option>';
		
		$('#' + selectId).append(optionHtml);
	}
}

function getUrlParameters() {
	
	if (isBlank(location.search)){
		return null;
	}
	
    var parameterNamesAndValues = location.search.substring(1, location.search.length).split('&');
    
    var urlParameters = {};
    
    for (var index = 0; index < parameterNamesAndValues.length; index++) {
        var parameterNameAndValue = parameterNamesAndValues[index].split('=');
        var name = decodeURIComponent(parameterNameAndValue[0]).toLowerCase();
        var value = decodeURIComponent(parameterNameAndValue[1]);
        urlParameters[name] = value;
    }
    return urlParameters;
}

function normalizeString(value){
	var normalizedValue = normalizeStringWithReplacement(value, '_');
	
	return normalizedValue;
}

function normalizeStringWithReplacement(value, spaceReplacement){
	
	//replace spaces with 
	var normalizedValue = value.replace(' ', spaceReplacement).toLowerCase();
	
	return normalizedValue;
}

function delimitedValueToArray(value, delimiter){
	
	if (!isDefined(delimiter)){
		delimiter = ',';
	}
	
	var values = [];
	
	var split = value.split(delimiter);
	
	for (var index = 0; index < split.length; index++){
		var splitValue = split[index];
		
		splitValue = splitValue.trim();
		
		values.push(splitValue);
	}
	
	return values;
}

function arrayToDelimitedValue(array, delimiter){
	
	if (!isDefined(delimiter)){
		delimiter = ',';
	}
	
	var delimitedValue = '';
	
	for (var index = 0; index < array.length; index++){
		var value = array[index];
		
		if (index > 0){
			delimitedValue = delimitedValue + delimiter;
		}
		
		delimitedValue = delimitedValue + value;
	}
	
	return delimitedValue;
}

function sortOptionsByLabel(options){
	
	options.sort(function (option1, option2){
		if (option1.label < option2.label){
			return -1;
		}
		else if (option1.label > option2.label){
			return 1;
		}
		return 0;
	});
}

function sortOptionsByValue(options){
	
	options.sort(function (option1, option2){
		if (option1.value < option2.value){
			return -1;
		}
		else if (option1.value > option2.value){
			return 1;
		}
		return 0;
	});
}

function sortOptionsByNumericValue(options){
	
	options.sort(function (option1, option2){
		if (Number(option1.value) < Number(option2.value)){
			return -1;
		}
		else if (Number(option1.value) > Number(option2.value)){
			return 1;
		}
		return 0;
	});
}

function getUniqueValuesFromArray(values){
	var uniqueValues = Array.from(new Set(values));
	
	return uniqueValues;
}