/**
 * 
 * A convenience function that says whether the given value is blank or not.
 * It's blank if:
 * 
 * 		1. It's not defined.
 * 		2. It is defined, but it's empty after it's trimmed.
 * 
 * Otherwise, it's not blank.
 * 
 * @param value
 * @returns
 */
function isBlank(value){
	
	if (!isDefined(value)){
		return true;
	}
	
	if (value.trim().length == 0){
		return true;
	}
	
	return false;
}

/**
 * 
 * A pretty dumb function that says whether the given value is
 * defined or not.  If it's null or undefined, it'll return false.
 * Otherwise, it'll return true.
 * 
 * @param value
 * @returns
 */
function isDefined(value){
	
	if (value == null || value == undefined){
		return false;
	}
	
	return true;
}

/**
 * 
 * This function will check whether the given value is "empty" or not.
 * It's considered empty if:
 * 
 * 		1. It's not defined.
 * 		2. Or it is defined but its length is 0.
 * 
 * Otherwise, it'll say it's not empty.
 * 
 * @param value
 * @returns
 */
function isEmpty(value){
	
	if (!isDefined(value)){
		return true;
	}
	
	if (value.length == 0){
		return true;
	}
	
	return false;
}

/**
 * 
 * A convenience function for making a select element with the different variables.
 * It'll only add in the defined ones and it'll skip the ones that aren't.
 * 
 * @param selectId
 * @param options
 * @param selectedValue
 * @param cssClass
 * @param style
 * @param onChange
 * @returns
 */
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

/**
 * 
 * A dumb convenience function that will check to see whether the given
 * select element has an option with the given value or not.  Just calls
 * jquery to do the work.
 * 
 * @param selectId
 * @param value
 * @returns
 */
function doesSelectHaveOptionWithValue(selectId, value){
	
	var option = $('#' + selectId + ' option[value="' + value + '"]');
	
	if (isDefined(option) && option.length > 0){
		return true;
	}
	
	return false;
}

/**
 * 
 * This function will put the given options in the select element
 * with the given id.  Not much to it!
 * 
 * @param selectId
 * @param options
 * @returns
 */
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

/**
 * 
 * A convenience function that will get the parameters out of the url for the
 * current "address".  It'll get the value out of the "location.search" variable
 * and then pull the stuff out of it.  
 * 
 * It'll return the name and value parameter values in a struct with the name being
 * the keys and the values being the values.
 * 
 * @returns
 */
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

/**
 * 
 * This function will "normalize" the string so that it's all in lowercase
 * and the spaces are replaced with underscores.  Basically, it makes the value
 * easier to use in places where we need it to be a single and consistent value.
 * 
 * @param value
 * @returns
 */
function normalizeString(value){
	var normalizedValue = normalizeStringWithReplacement(value, '_');
	
	return normalizedValue;
}

/**
 * 
 * This function will "normalize" the given value by replacing the space character
 * with the given "spaceReplacement" and then lowercase it.
 * 
 * @param value
 * @param spaceReplacement
 * @returns
 */
function normalizeStringWithReplacement(value, spaceReplacement){
	
	var normalizedValue = value.replace(' ', spaceReplacement).toLowerCase();
	
	return normalizedValue;
}

/**
 * 
 * A convenience function for splitting the given delimited value into an array.
 * If the given delimiter isn't defined, it'll assume it should be a comma.
 * 
 * @param value
 * @param delimiter
 * @returns
 */
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

/**
 * 
 * A convenience function that will turn the given array into a string
 * of delimited values.  If the given delimiter isn't given, it'll use
 * a comma.
 * 
 * @param array
 * @param delimiter
 * @returns
 */
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

/**
 * 
 * This function will sort the given options by each one's "label"
 * variable.  Not much to it.
 * 
 * @param options
 * @returns
 */
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

/**
 * 
 * This function will sort the given options by each one's "value"
 * variable.  Not much to it.
 * 
 * @param options
 * @returns
 */
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

/**
 * 
 * This function will sort the given options by each one's "value"
 * variable and treat it as a number.  It will convert the value into
 * a number before doing the comparisons.
 * 
 * @param options
 * @returns
 */
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

/**
 * 
 * A dumb convenience function that will get the unique values from the
 * given array.  Not much to it.
 * 
 * @param values
 * @returns
 */
function getUniqueValuesFromArray(values){
	var uniqueValues = Array.from(new Set(values));
	
	return uniqueValues;
}

/**
 * 
 * A dumb convenience function that will turn the given values
 * array into an array of integer values.  Just calls parseInt
 * on each value in the array and that's it.
 * 
 * @param values
 * @returns
 */
function getValuesAsIntegers(values){
	
	if (values == null || values == undefined){
		return null;
	}
	
	var valuesAsIntegers = [];
	
	for (var index = 0; index < values.length; index++){
		var value = values[index];
		
		var integerValue = parseInt(value);
		
		valuesAsIntegers.push(integerValue);
	}
	
	return valuesAsIntegers;
}

/**
 * 
 * This function will copy the given text to the clipboard.  I stole
 * it off stackoverflow.
 * 
 * @param text
 * @returns
 */
function copyToClipboard(text) {
    var textarea = document.createElement('textarea');
    textarea.innerHTML = text;
    textarea.style.position = 'fixed';
    document.body.appendChild(textarea);
    textarea.select();
    var result = document.execCommand('copy');
    document.body.removeChild(textarea);
    return result;
}


/**
 * 
 * This function switches the element with the given id from visibile to 
 * hidden or back (with the jquery "hide" and "show" functions).
 * 
 * It decides whether something is visible by using the ":visible" property
 * in jquery.  If it's visible, it hides it.  Otherwise, it shows it.
 * 
 * @param id
 * @returns
 */
function toggleVisibilty(id){
	
	//Steps to do:
	//	1. Get whether the element is visible.
	//	2. Hide it if it is and show it if it's not.
	
	var isElementVisible = isVisible(id);
	
	if (isVisible){
		$('#' + id).hide();
	}
	else {
		$('#' + id).show();
	}
}

/**
 * 
 * A really dumb function for checking whether an element with the given
 * id is visible or not.  Just calls jquery to do the work.
 * 
 * @param id
 * @returns
 */
function isVisible(id){
	
	var isElementVisible = $('#' + id).is(':visible');
	
	return isElementVisible;
}

/**
 * 
 * This function will get the parameters in a map from the url in the browser.  If there
 * aren't any parameters, it'll return null.  Otherwise, it'll return a map with the parameter
 * names as the keys and the values as the url.
 * 
 * @returns
 */
function getUrlParameters() {
	
	//Steps to do:
	//	1. If there aren't any parameters, there's nothing to do.
	//	2. Otherwise, each parameter should be separated by an ampersand, so break them apart on that.
	//	3. Go through each parameter and get the key and value and that's a parameter.
	//	4. That's it.
	
	if (isBlank(location.search)){
		return null;
	}
	
    var parameterNamesAndValues = location.search.substring(1, location.search.length).split('&');
    
    var urlParameters = {};
    
    for (var index = 0; index < parameterNamesAndValues.length; index++) {
        var parameterNameAndValue = parameterNamesAndValues[index].split('=');
        //Make sure to decode both the name and value in case there are weird values in them.
        var name = decodeURIComponent(parameterNameAndValue[0]);
        var value = decodeURIComponent(parameterNameAndValue[1]);
        urlParameters[name] = value;
    }
    
    return urlParameters;
}

/**
 * 
 * Here for when we're sorting something alphabetically and we don't
 * care what kind of string it is.
 * 
 * @param values
 * @returns
 */
function sortAlphabetically(values){
	
	values.sort(function (value1, value2){
		if (value1 < value2){
			return -1;
		}
		else if (value1 > value2){
			return 1;
		}
		return 0;
	});
}

/**
 * 
 * This function will check whether the given value has multiple values
 * in it or not.  Basically, it'll return true if the given value is defined
 * and it has a comma in it.  It assumes the given value is a string and multiple
 * values are separated by commas in that string.
 * 
 * @param value
 * @returns
 */
function doesValueHaveMultipleValues(value){
	
	if (isDefined(value) && value.indexOf(',') != -1){
		return true;
	}
	
	return false;
}