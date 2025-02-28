function onClickYearSelector(event){
	event.stopPropagation();
	
	var wasSelectorVisible = isVisible('yearSelectorContainer'); 
	
	hideSelectorContainers();

	if (!wasSelectorVisible){
		resetYearSelections();
		showYearSelector();
	}
}

function onClickMultiselectYearContainer(event){
	event.stopPropagation();
	
	var multiselectYear = getMultiselectYear();
	
	if (multiselectYear){
		setMultiselectYearValue(false);
	}
	else {
		setMultiselectYearValue(true);
	}
	
	onClickMultiselectYear(event);
}

function onClickMultiselectYear(event){
	event.stopPropagation();
	
	var multiselectYearChecked = $('#multiselectYear').prop('checked');
	
	setMultiselectYear(multiselectYearChecked);
	
	if (multiselectYearChecked){
		showMultiselectYearContainer();
		showYearCheckboxes();
		hideAllYearSelectorContainer();
		hideYearRadioButtons();
		showYearSelectorFooterContainer();
	}
	else {
		hideMultiselectYearContainer();
		showAllYearSelectorContainer();
		showYearRadioButtons();
		hideYearCheckboxes();
		hideYearSelectorFooterContainer();
	}
}

function setMultiselectYearValue(value){
	if (value){
		$('#multiselectYear').prop('checked', true);
	}
	else {
		$('#multiselectYear').prop('checked', false);
	}
}

function showAllYearSelectorContainer(){
	$('#year-selector-container-all').show();
}

function hideAllYearSelectorContainer(){
	$('#year-selector-container-all').hide();
}

function showMultiselectYearContainer(){
	$('#multiselectYearContainer').show();
}

function hideMultiselectYearContainer(){
	$('#multiselectYearContainer').hide();
}

function showYearSelectorFooterContainer(){
	$('#year-selector-footer-container').show();
}

function hideYearSelectorFooterContainer(){
	$('#year-selector-footer-container').hide();
}

//year-checkbox-input-
function showYearCheckboxes(){
	var yearValues = getAllYearValues();
	
	for (var index = 0; index < yearValues.length; index++){
		var yearValue = yearValues[index];
		showYearCheckbox(yearValue);
	}
}

function showYearCheckbox(yearValue){
	var normalizedValue = normalizeYearValue(yearValue);
	$('#year-checkbox-input-' + normalizedValue).show();
}

function hideYearCheckboxes(){
	
	var yearValues = getAllYearValues();
	
	for (var index = 0; index < yearValues.length; index++){
		var yearValue = yearValues[index];
		hideYearCheckbox(yearValue);
	}
}

function hideYearCheckbox(yearValue){
	var normalizedValue = normalizeYearValue(yearValue);
	$('#year-checkbox-input-' + normalizedValue).hide();
}

//year-radio-input-
function showYearRadioButtons(){
	var yearValues = getAllYearValues();
	
	for (var index = 0; index < yearValues.length; index++){
		var yearValue = yearValues[index];
		showYearRadioButton(yearValue);
	}
}

function showYearRadioButton(yearValue){
	var normalizedValue = normalizeYearValue(yearValue);
	$('#year-radio-input-' + normalizedValue).show();
}

function hideYearRadioButtons(){
	
	var yearValues = getAllYearValues();
	
	for (var index = 0; index < yearValues.length; index++){
		var yearValue = yearValues[index];
		hideYearRadioButton(yearValue);
	}
}

function hideYearRadioButton(yearValue){
	var normalizedValue = normalizeYearValue(yearValue);
	$('#year-radio-input-' + normalizedValue).hide();
}


function setMultiselectYear(value){
	NFL_PICKS_GLOBAL.multiselectYear = value;
}

function getMultiselectYear(){
	return NFL_PICKS_GLOBAL.multiselectYear;
}

function onClickYearSelectionOk(event){
	event.stopPropagation();
	//If it's multi select here, unselect the all option.
	var multiselectYear = getMultiselectYear();
	if (multiselectYear){
		removeYearFromCurrentSelection('all');
	}
	hideYearSelector();
	setSelectedYears(currentYearSelections);
	updateYearsLink();
	updateView();
}

function onClickYearSelectionCancel(event){
	event.stopPropagation();
	resetAndHideYearSelections();
}

function resetYearSelections(){
	unselectAllYearsByValue();
	currentYearSelections = getSelectedYearValues();
	selectYearsByValue(currentYearSelections);
}

function resetAndHideYearSelections(){
	resetYearSelections();
	hideYearSelector();
}

function showYearSelector(){
	$('#yearSelectorContainer').show();
}

function hideYearSelector(){
	$('#yearSelectorContainer').hide();
}

var currentYearSelections = [];


/*
 '<div style="display: inline-block; width: 48%; text-align: left;"><a href="javascript:void(0);" onClick="onClickClearYears(event);">Clear</a></div>' +
						   					'<div style="display: inline-block; width: 48%; text-align: right;"><a href="javascript:void(0);" onClick="onClickSelectAllYears(event);>Select all</a></div>' +
 */

function onClickSelectAllYears(event){
	
	event.stopPropagation();
	
	var years = getAllYears();
	
	currentYearSelections = [];
	
	for (var index = 0; index < years.length; index++){
		var year = years[index];
		selectYear(year.value);
		addYearToCurrentSelection(year.value);
	}
}

function onClickClearYears(event){
	
	event.stopPropagation();
	
	var years = getAllYears();
	
	currentYearSelections = [];
	
	for (var index = 0; index < years.length; index++){
		var year = years[index];
		unselectYear(year.value);
		removeYearFromCurrentSelection(year.value);
	}
}

function onClickYear(event, value){
	event.stopPropagation();
	
	var multiselectYear = getMultiselectYear();
	
	if (multiselectYear){
		var indexOfValue = currentYearSelections.indexOf(value);
		if (indexOfValue >= 0){
			unselectYear(value);
			removeYearFromCurrentSelection(value);
		}
		else {
			selectYear(value);
			addYearToCurrentSelection(value);
		}
	}
	else {
		currentYearSelections = [];
		selectYear(value);
		addYearToCurrentSelection(value);
		onClickYearSelectionOk(event);
	}
}

function updateYearsLink(){
	
	var selectedYears = getSelectedYears();
	
//	if (selectedYears.length == 1 && 'all' == selectedYears[0].value){
//		$('#yearsLink').text('All years');
//		$('#yearsLink').prop('title', 'All years');
//		return;
//	}
	
	sortOptionsByNumericValue(selectedYears);
	
	var linkText = '';
	
	if (selectedYears.length > 0){
		for (var index = 0; index < selectedYears.length; index++){
			var year = selectedYears[index];
			
			if (index > 0){
				linkText = linkText + ', ';
			}
			
			linkText = linkText + year.label;
		}
	}
	else {
		linkText = 'All years';
	}
	
	$('#yearsLink').prop('title', linkText);
	
	if (linkText.length >= 25){
		linkText = linkText.substring(0, 25) + '...';
	}
	
	$('#yearsLink').text(linkText);
}

function showYearsLink(){
	$('#yearsLink').show();
}

function hideYearsLink(){
	$('#yearsLink').hide();
}

function selectAllYearsByValue(){
	var yearValues = getAllYearValues();
	selectYearsByValue(yearValues);
}

function unselectAllYearsByValue(){
	var yearValues = getAllYearValues();
	unselectYearsByValue(yearValues);
}

function selectYearsByValue(values){
	for (var index = 0; index < values.length; index++){
		var value = values[index];
		selectYear(value);
	}
}

function normalizeYearValue(value){
	var normalizedValue = normalizeString(value);
	return normalizedValue;
}

function selectYear(value){
	var normalizedValue = normalizeYearValue(value);
	$('#year-checkbox-input-' + normalizedValue).prop('checked', true);
	$('#year-radio-input-' + normalizedValue).prop('checked', true);
}

function addYearToCurrentSelection(value){
	currentYearSelections.push(value);
}

function unselectYearsByValue(years){
	for (var index = 0; index < years.length; index++){
		var year = years[index];
		unselectYear(year);
	}
}

function unselectYear(year){
	var normalizedValue = normalizeYearValue(year);
	$('#year-checkbox-input-' + normalizedValue).prop('checked', false);
	$('#year-radio-input-' + normalizedValue).prop('checked', false);
}

function removeYearFromCurrentSelection(value){
	var indexOfValue = currentYearSelections.indexOf(value);
	if (indexOfValue >= 0){
		currentYearSelections.splice(indexOfValue, 1);
	}
}

function getAllYears(){
	return NFL_PICKS_GLOBAL.years;
}

function getRealYears(){
	return NFL_PICKS_GLOBAL.realYears;
}

function getAllYearValues(){
	var yearValues = [];
	
	for (var index = 0; index < NFL_PICKS_GLOBAL.years.length; index++){
		var year = NFL_PICKS_GLOBAL.years[index];
		yearValues.push(year.value);
	}
	
	return yearValues;
}

function getYears(values){
	
	var years = [];
	
	for (var index = 0; index < values.length; index++){
		var value = values[index];
		var year = getYear(value);
		if (year != null){
			years.push(year);
		}
	}
	
	return years;
}

function getYear(value){
	
	for (var index = 0; index < NFL_PICKS_GLOBAL.years.length; index++){
		var year = NFL_PICKS_GLOBAL.years[index];
		if (value == year.value){
			return year;
		}
	}
	
	return null;
}

function getSelectedYearValues(){
	
	var yearValues = [];
	
	var selectedYears = getSelectedYears();
	
	for (var index = 0; index < selectedYears.length; index++){
		var selectedYear = selectedYears[index];
		yearValues.push(selectedYear.value);
	}
	
	return yearValues;
}

function getYearValuesForRequest(){
	
	var selectedValues = getSelectedYearValues();
	
	var valuesToSend = [];
	
	for (var index = 0; index < selectedValues.length; index++){
		var selectedValue = selectedValues[index];
		
		var yearsForSelectedValue = getYearsForYearValue(selectedValue);
		
		valuesToSend = valuesToSend.concat(yearsForSelectedValue);
	}
	
	var uniqueValues = getUniqueValuesFromArray(valuesToSend);
	
	var yearValuesForRequest = arrayToDelimitedValue(uniqueValues);
	
	return yearValuesForRequest;
}

function getSelectedYears(){
	return NFL_PICKS_GLOBAL.selections.years;
}

function getYearValuesForSelectedYears(){
	
	var selectedValues = getSelectedYearValues();
	
	var yearValues = [];
	
	for (var index = 0; index < selectedValues.length; index++){
		var selectedValue = selectedValues[index];
		
		var yearsForSelectedValue = getYearsForYearValue(selectedValue);
		
		yearValues = yearValues.concat(yearsForSelectedValue);
	}
	
	return yearValues;
}

/**
 * 
 * This function is here to get the years we should use for the given year value.
 * It will just do the "translation" from something like "jurassic-period" to 
 * values like ["2010", "2011", "2012", ...].
 * 
 * Here so that "mapping" can hopefully be done in one place.  I could make it a constant I guess,
 * but doing it this way kind of puts in the rule that "every year 2017 and over is the modern
 * era" and that would be hard to turn into a constant value.
 * 
 * @param yearValue
 * @returns
 */
function getYearsForYearValue(yearValue){
	
	var yearsForYearValue = [];

	var realYears = getRealYears();
	
	if ('all' == yearValue){
		for (var index = 0; index < realYears.length; index++){
			var realYear = realYears[index];
			yearsForYearValue.push(realYear.value);
		}
	}
	else if ('jurassic-period' == yearValue){
		yearsForYearValue = ['2010', '2011', '2012', '2013', '2014', '2015'];
	}
	else if ('first-year' == yearValue){
		yearsForYearValue = ['2016'];
	}
	else if ('modern-era' == yearValue){
		
		for (var index = 0; index < realYears.length; index++){
			var realYear = realYears[index];

			var realYearValue = parseInt(realYear.value);

			if (realYearValue >= 2017){
				yearsForYearValue.push(realYear.value);
			}
		}
	}
	else {
		yearsForYearValue.push(yearValue);
	}
	
	return yearsForYearValue;
}

function setMultiselectYear(value){
	NFL_PICKS_GLOBAL.multiselectYear = value;
}

function getMultiselectYear(){
	return NFL_PICKS_GLOBAL.multiselectYear;
}




/**
 * 
 * This function will say whether a "specific" year was selected
 * (basically if the year isn't "all" or one of the special ones).
 * 
 * This should go in the selectors javascript file i think.
 * 
 * @returns
 */
function isSpecificYearSelected(){

	var selectedYears = getSelectedYears();
	
	if (selectedYears.length > 1){
		return false;
	}
	
	var selectedYear = selectedYears[0].value;
	
	if ('all' == selectedYear || 'jurassic-period' == selectedYear || 'first-year' == selectedYear || 'modern-era' == selectedYear){
		return false;
	}
	
	return true;
}


function showYearContainer(){
	$('#yearContainer').show();
}

function hideYearContainer(){
	$('#yearContainer').hide();
}


/**
 * 
 * This function will set the given years as being selected in the UI and in the
 * NFL_PICKS_GLOBAL variable (NFL_PICKS_GLOBAL.selections.years).  
 * 
 * It expects the given years variable to either be...
 * 	1. An array of year values.
 * 	2. A comma separated string of year values.
 * 	3. A single year value.
 * 
 * It will put the actual year objects into the NFL_PICKS_GLOBAL variable for
 * each year value that's given.
 * 
 * @param years
 * @returns
 */
function setSelectedYears(years){
	
	//Steps to do:
	//	1. Check whether the years variable is an array.
	//	2. If it is, just keep it.
	//	3. Otherwise, it's a string so check to see if it has multiple values.
	//	4. If it does, then turn it into an array.
	//	5. Otherwise, just put it in there as a single value.
	//	6. Go through each year in the array, get the actual object for the year
	//	   and put it in the global variable.  And, "select" it in the ui.
	
	var yearValuesArray = [];
	
	var isArray = Array.isArray(years);
	
	if (isArray){
		yearValuesArray = years;
	}
	else {
		var hasMultipleValues = doesValueHaveMultipleValues(years);
		
		if (hasMultipleValues){
			yearValuesArray = delimitedValueToArray(years);
		}
		else {
			yearValuesArray.push(years);
		}
	}
	
	var yearsArray = [];
	
	for (var index = 0; index < yearValuesArray.length; index++){
		var value = yearValuesArray[index];
		selectYear(value);

		var year = getYear(value);
		yearsArray.push(year);
	}
	
	NFL_PICKS_GLOBAL.selections.years = yearsArray;
}