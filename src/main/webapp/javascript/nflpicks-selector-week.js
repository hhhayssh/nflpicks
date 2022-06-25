function onClickWeekSelector(event){
	event.stopPropagation();
	
	var wasSelectorVisible = isVisible('weekSelectorContainer'); 
	
	hideSelectorContainers();

	if (!wasSelectorVisible){
		resetWeekSelections();
		showWeekSelector();
	}
}

function onClickMultiselectWeekContainer(event){
	event.stopPropagation();
	
	var multiselectWeek = getMultiselectWeek();
	
	if (multiselectWeek){
		setMultiselectWeekValue(false);
	}
	else {
		setMultiselectWeekValue(true);
	}
	
	onClickMultiselectWeek(event);
}

function onClickMultiselectWeek(event){
	event.stopPropagation();
	
	var multiselectWeekChecked = $('#multiselectWeek').prop('checked');
	
	setMultiselectWeek(multiselectWeekChecked);
	
	if (multiselectWeekChecked){
		showMultiselectWeekContainer();
		showWeekCheckboxes();
		hideAllWeekSelectorContainer();
		hideWeekRadioButtons();
		showWeekSelectorFooterContainer();
	}
	else {
		hideMultiselectWeekContainer();
		showAllWeekSelectorContainer();
		showWeekRadioButtons();
		hideWeekCheckboxes();
		hideWeekSelectorFooterContainer();
	}
}

function setMultiselectWeekValue(value){
	if (value){
		$('#multiselectWeek').prop('checked', true);
	}
	else {
		$('#multiselectWeek').prop('checked', false);
	}
}

function showAllWeekSelectorContainer(){
	$('#week-selector-container-all').show();
}

function hideAllWeekSelectorContainer(){
	$('#week-selector-container-all').hide();
}

function showMultiselectWeekContainer(){
	$('#multiselectWeekContainer').show();
}

function hideMultiselectWeekContainer(){
	$('#multiselectWeekContainer').hide();
}

function showWeekSelectorFooterContainer(){
	$('#week-selector-footer-container').show();
}

function hideWeekSelectorFooterContainer(){
	$('#week-selector-footer-container').hide();
}

//week-checkbox-input-
function showWeekCheckboxes(){
	var weekValues = getAllWeekValues();
	
	for (var index = 0; index < weekValues.length; index++){
		var weekValue = weekValues[index];
		showWeekCheckbox(weekValue);
	}
}

function showWeekCheckbox(weekValue){
	var normalizedValue = normalizeWeekValue(weekValue);
	$('#week-checkbox-input-' + normalizedValue).show();
}

function hideWeekCheckboxes(){
	
	var weekValues = getAllWeekValues();
	
	for (var index = 0; index < weekValues.length; index++){
		var weekValue = weekValues[index];
		hideWeekCheckbox(weekValue);
	}
}

function hideWeekCheckbox(weekValue){
	var normalizedValue = normalizeWeekValue(weekValue);
	$('#week-checkbox-input-' + normalizedValue).hide();
}

//week-radio-input-
function showWeekRadioButtons(){
	var weekValues = getAllWeekValues();
	
	for (var index = 0; index < weekValues.length; index++){
		var weekValue = weekValues[index];
		showWeekRadioButton(weekValue);
	}
}

function showWeekRadioButton(weekValue){
	var normalizedValue = normalizeWeekValue(weekValue);
	$('#week-radio-input-' + normalizedValue).show();
}

function hideWeekRadioButtons(){
	
	var weekValues = getAllWeekValues();
	
	for (var index = 0; index < weekValues.length; index++){
		var weekValue = weekValues[index];
		hideWeekRadioButton(weekValue);
	}
}

function hideWeekRadioButton(weekValue){
	var normalizedValue = normalizeWeekValue(weekValue);
	$('#week-radio-input-' + normalizedValue).hide();
}


function setMultiselectWeek(value){
	NFL_PICKS_GLOBAL.multiselectWeek = value;
}

function getMultiselectWeek(){
	return NFL_PICKS_GLOBAL.multiselectWeek;
}

function onClickWeekSelectionOk(event){
	event.stopPropagation();
	//If it's multi select here, unselect the all option.
	var multiselectWeek = getMultiselectWeek();
	if (multiselectWeek){
		removeWeekFromCurrentSelection('all');
	}
	hideWeekSelector();
	var currentWeeks = getCurrentWeekSelections();
	setSelectedWeeks(currentWeeks);
	updateWeeksLink();
	updateView();
}

function onClickWeekSelectionCancel(event){
	event.stopPropagation();
	resetAndHideWeekSelections();
}

function resetWeekSelections(){
	unselectAllWeeksByValue();
	var selectedWeekValues = getSelectedWeekValues();
	setCurrentWeekSelections(selectedWeekValues);
	selectWeeksByValue(selectedWeekValues);
}

function resetAndHideWeekSelections(){
	resetWeekSelections();
	hideWeekSelector();
}

function showWeekSelector(){
	$('#weekSelectorContainer').show();
}

function hideWeekSelector(){
	$('#weekSelectorContainer').hide();
}

var currentWeekSelections = [];

function getCurrentWeekSelections(){
	return currentWeekSelections;
}

function setCurrentWeekSelections(updatedSelections){
	currentWeekSelections = updatedSelections;
}

function clearCurrentWeekSelections(){
	currentWeekSelections = [];
}


/*
 '<div style="display: inline-block; width: 48%; text-align: left;"><a href="javascript:void(0);" onClick="onClickClearWeeks(event);">Clear</a></div>' +
						   					'<div style="display: inline-block; width: 48%; text-align: right;"><a href="javascript:void(0);" onClick="onClickSelectAllWeeks(event);>Select all</a></div>' +
 */

function onClickSelectAllWeeks(event){
	
	event.stopPropagation();
	
	var weeks = getAllWeeks();
	
	clearCurrentWeekSelections();
	
	for (var index = 0; index < weeks.length; index++){
		var week = weeks[index];
		selectWeek(week.value);
		addWeekToCurrentSelection(week.value);
	}
}

function onClickClearWeeks(event){
	
	event.stopPropagation();
	
	var weeks = getAllWeeks();
	
	clearCurrentWeekSelections();
	
	for (var index = 0; index < weeks.length; index++){
		var week = weeks[index];
		unselectWeek(week.value);
		removeWeekFromCurrentSelection(week.value);
	}
}

function onClickWeek(event, value){
	event.stopPropagation();
	
	var multiselectWeek = getMultiselectWeek();
	
	if (multiselectWeek){
		var indexOfValue = currentWeekSelections.indexOf(value);
		if (indexOfValue >= 0){
			unselectWeek(value);
			removeWeekFromCurrentSelection(value);
		}
		else {
			selectWeek(value);
			addWeekToCurrentSelection(value);
		}
	}
	else {
		clearCurrentWeekSelections();
		selectWeek(value);
		addWeekToCurrentSelection(value);
		onClickWeekSelectionOk(event);
	}
}

function updateWeeksLink(){
	
	var selectedWeeks = getSelectedWeeks();
	
	//If there aren't any selected weeks, it should be "none"
	if (isEmpty(selectedWeeks)){
		$('#weeksLink').text('No weeks');
		return;
	}
	
	if (selectedWeeks.length == 1 && 'all' == selectedWeeks[0].value){
		$('#weeksLink').text('All weeks');
		return;
	}
	
	sortOptionsByNumericValue(selectedWeeks);
	
	var linkText = '';
	
	for (var index = 0; index < selectedWeeks.length; index++){
		var week = selectedWeeks[index];
		
		if (index > 0){
			linkText = linkText + ', ';
		}
		
		linkText = linkText + week.label;
	}
	
	$('#weeksLink').prop('title', linkText);
	
	if (linkText.length >= 25){
		linkText = linkText.substring(0, 25) + '...';
	}
	
	$('#weeksLink').text(linkText);
}

function showWeeksLink(){
	$('#weeksLink').show();
}

function hideWeeksLink(){
	$('#weeksLink').hide();
}

function selectAllWeeksByValue(){
	var weekValues = getAllWeekValues();
	selectWeeksByValue(weekValues);
}

function unselectAllWeeksByValue(){
	var weekValues = getAllWeekValues();
	unselectWeeksByValue(weekValues);
}

function selectWeeksByValue(values){
	for (var index = 0; index < values.length; index++){
		var value = values[index];
		selectWeek(value);
	}
}

function normalizeWeekValue(value){
	var normalizedValue = normalizeString(value);
	return normalizedValue;
}

function selectWeek(value){
	var normalizedValue = normalizeWeekValue(value);
	$('#week-checkbox-input-' + normalizedValue).prop('checked', true);
	$('#week-radio-input-' + normalizedValue).prop('checked', true);
}

function addWeekToCurrentSelection(value){
	currentWeekSelections.push(value);
}

/**
 * 
 * This function will completely unselect the week by its value.
 * It will make the week unselected on the ui, remove it from
 * the "current selections" array, and update the selected weeks
 * in the NFL_PICKS_GLOBAL variable.
 * 
 * @param value
 * @returns
 */
function unselectWeekFull(value){
	unselectWeek(value);
	removeWeekFromCurrentSelection(value);
	var currentWeeks = getCurrentWeekSelections();
	setSelectedWeeks(currentWeeks);
}

function unselectWeeksByValue(weeks){
	for (var index = 0; index < weeks.length; index++){
		var week = weeks[index];
		unselectWeek(week);
	}
}

function unselectWeek(week){
	var normalizedValue = normalizeWeekValue(week);
	$('#week-checkbox-input-' + normalizedValue).prop('checked', false);
	$('#week-radio-input-' + normalizedValue).prop('checked', false);
}

function removeWeekFromCurrentSelection(value){
	var indexOfValue = currentWeekSelections.indexOf(value);
	if (indexOfValue >= 0){
		currentWeekSelections.splice(indexOfValue, 1);
	}
}

function getAllWeeks(){
	return NFL_PICKS_GLOBAL.weeks;
}

function getRealWeeks(){
	return NFL_PICKS_GLOBAL.realWeeks;
}

function getAllWeekValues(){
	var weekValues = [];
	
	for (var index = 0; index < NFL_PICKS_GLOBAL.weeks.length; index++){
		var week = NFL_PICKS_GLOBAL.weeks[index];
		weekValues.push(week.value);
	}
	
	return weekValues;
}

function getWeeks(values){
	
	var weeks = [];
	
	for (var index = 0; index < values.length; index++){
		var value = values[index];
		var week = getWeek(value);
		if (week != null){
			weeks.push(week);
		}
	}
	
	return weeks;
}

function getWeek(value){
	
	for (var index = 0; index < NFL_PICKS_GLOBAL.weeks.length; index++){
		var week = NFL_PICKS_GLOBAL.weeks[index];
		if (value == week.value){
			return week;
		}
	}
	
	return null;
}

function getSelectedWeekValues(){
	
	var weekValues = [];
	
	var selectedWeeks = getSelectedWeeks();
	
	for (var index = 0; index < selectedWeeks.length; index++){
		var selectedWeek = selectedWeeks[index];
		weekValues.push(selectedWeek.value);
	}
	
	return weekValues;
}

function getRegularSeasonWeeksForSelectedYears(){
	
	var selectedYearValues = getSelectedYearValues();
	var integerYearValues = getValuesAsIntegers(selectedYearValues);
	
	var regularSeasonWeeksBefore2021 = ['1', '2', '3', '4', '5', '6', '7',
								    '8', '9', '10', '11', '12', '13', '14',
								    '15', '16', '17', 'wildcard', 'divisional', 'conference_championship',
								    'superbowl'];
	
	var regularSeasonWeeksAfter2021 = ['1', '2', '3', '4', '5', '6', '7',
	    '8', '9', '10', '11', '12', '13', '14',
	    '15', '16', '17', '18', 'wildcard', 'divisional', 'conference_championship',
	    'superbowl'];
	
	for (var index = 0; index < integerYearValues.length; index++){
		var integerYearValue = integerYearValues[index];
		
		if (integerYearValue >= 2021){
			return regularSeasonWeeksAfter2021;
		}
	}
	
	return regularSeasonWeeksBefore2021;
}

function getAllWeekValuesForSelectedYears(){
	
	var selectedYearValues = getSelectedYearValues();
	
	var integerYearValues = getValuesAsIntegers(selectedYearValues);
	
	//this needs to be redone
	//there needs to be a map or something
	//the weeks for a year maybe could be loaded from the database and turned
	//into a map ... but then the ... yeah it needs to be like that
	var availableWeeksBefore2021 = ['1', '2', '3', '4', '5', '6', '7',
								    '8', '9', '10', '11', '12', '13', '14',
								    '15', '16', '17', 'wildcard', 'divisional', 'conference_championship',
								    'superbowl'];
	
	var availableWeeksAfter2021 = ['1', '2', '3', '4', '5', '6', '7',
	    '8', '9', '10', '11', '12', '13', '14',
	    '15', '16', '17', '18', 'wildcard', 'divisional', 'conference_championship',
	    'superbowl'];
	
	for (var index = 0; index < selectedYearValues.length; index++){
		var selectedYearValue = selectedYearValues[index];
		
		if ('all' == selectedYearValue){
			return availableWeeksAfter2021;
		}
		
		var integerYearValue = parseInt(selectedYearValue);
		
		if (integerYearValue >= 2021){
			return availableWeeksAfter2021;
		}
	}
	
	return availableWeeksBefore2021;
}

function getPlayoffWeeksForSelectedYears(){
	
	var playoffWeeks = ['wildcard', 'divisional', 'conference_championship', 'superbowl'];
	
	return playoffWeeks;
}

function getWeekValuesForRequest(){
	
	var selectedValues = getSelectedWeekValues();
	
	var valuesToSend = [];
	
	for (var index = 0; index < selectedValues.length; index++){
		var selectedValue = selectedValues[index];

		//If all is a selected value, then we want to send all the
		//possible weeks.		
		if ('all' == selectedValue){
			var allWeeks = getAllWeekValuesForSelectedYears();
			valuesToSend = valuesToSend.concat(allWeeks);
//			for (var index2 = 0; index2 < allWeeksForSelectedYears.length; index2++){
//				var week = allWeeksForSelectedYears[index2];
//				valuesToSend.push(week);
//			}
		}
		//If they picked the regular season, get the regular season weeks for
		//the selected years.
		else if ('regular_season' == selectedValue){
			var regularSeasonWeeks = getRegularSeasonWeeksForSelectedYears();
			valuesToSend = valuesToSend.concat(regularSeasonWeeks);
		}
		else if ('playoffs' == selectedValue){
			var playoffWeeks = getPlayoffWeeksForSelectedYears();
			valuesToSend = valuesToSend.concat(playoffWeeks);
		}
		else {
			valuesToSend.push(selectedValue);
		}
	}
	
	var uniqueValues = getUniqueValuesFromArray(valuesToSend);
	
	var weekValuesForRequest = arrayToDelimitedValue(uniqueValues);
	
	return weekValuesForRequest;
	
}

function getSelectedWeeks(){
	return NFL_PICKS_GLOBAL.selections.weeks;
}

function getWeekSelectorContainerId(week){
	
	var normalizedWeekValue = normalizeWeekValue(week);
	
	var weekSelectorContainerId = 'week-selector-container-' + normalizedWeekValue;
	
	return weekSelectorContainerId;
}

function showWeekItem(week){
	var weekSelectorContainerId = getWeekSelectorContainerId(week);
	$('#' + weekSelectorContainerId).show();
}

function hideWeekItem(week){
	var weekSelectorContainerId = getWeekSelectorContainerId(week);
	$('#' + weekSelectorContainerId).hide();
}

function showWeekItems(weeks){
	for (var index = 0; index < weeks.length; index++){
		var week = weeks[index];
		showWeekItem(week);
	}
}

function hideWeekItems(weeks){
	for (var index = 0; index < weeks.length; index++){
		var week = weeks[index];
		hideWeekItem(week);
	}
}

function updateCurrentWeekSelections(){
	var selectedWeekValues = getSelectedWeekValues();
	setCurrentWeekSelections(selectedWeekValues);
}

function setMultiselectWeek(value){
	NFL_PICKS_GLOBAL.multiselectWeek = value;
}

function getMultiselectWeek(){
	return NFL_PICKS_GLOBAL.multiselectWeek;
}



/**
 * 
 * A convenience function for checking whether a single week is selected or not.
 * 
 * It'll return false if:
 * 		1. There are no selected weeks.
 * 		2. There's more than one selected week.
 * 		3. There's one selected week, but it's the regular season, playoffs, or all.
 * 
 * If all of those 3 things are false, it'll return true because that means there's a single
 * week selected and it's not one of the ones that represents multiple weeks.
 * 
 * @returns
 */
function isASingleWeekSelected(){
	
	var selectedWeeks = getSelectedWeekValues();
	
	if (isEmpty(selectedWeeks)){
		return false;
	}
	
	if (selectedWeeks.length > 1){
		return false;
	}
	
	var selectedWeek = selectedWeeks[0];
	
	if ('all' == selectedWeek || 'regular_season' == selectedWeek || 'playoffs' == selectedWeek){
		return false;
	}
	
	return true;
}

/** 
 * 
 * This function will say whether a "specific" week was selected.
 * If the week is all, "regular-season", or "playoffs", then it takes
 * that to mean a specific one isn't and a "range" is instead.
 * 
 * @returns
 */
function isSpecificWeekSelected(){

	var selectedWeeks = getSelectedWeeks();
	
	if (isEmpty(selectedWeeks)){
		return false;
	}
	
	var selectedWeek = selectedWeeks[0].value;
	
	if ('all' == selectedWeek || 'regular_season' == selectedWeek || 'playoffs' == selectedWeek){
		return false;
	}
	
	return true;
}


/**
 * 
 * This function will toggle the visibility the weeks for the given week records
 * at the given index.  If they're shown, it'll hide them.  If they're hidden,
 * it'll show them.  It's specific to the "weeks won" stat thing for now.
 * 
 * @param index
 * @returns
 */
function toggleShowWeeks(index){
	
	//Steps to do:
	//	1. Get whether the week records are shown now.
	//	2. If they are, then hide them and change the link text.
	//	3. Otherwise, show them and change the link text.
	
	var isVisible = $('#week-records-' + index).is(':visible');
	
	if (isVisible){
		$('#week-records-' + index).hide();
		$('#show-weeks-link-' + index).text('show weeks');
	}
	else {
		$('#week-records-' + index).show();
		$('#show-weeks-link-' + index).text('hide weeks');
	}
}


function showWeekContainer(){
	$('#weekContainer').show();
}

function hideWeekContainer(){
	$('#weekContainer').hide();
}

function getAvailableWeeksForYears(yearValues){
	
	var integerYearValues = getValuesAsIntegers(currentSelectedYearValues);
	
	var availableWeeksBefore2021 = ['1', '2', '3', '4', '5', '6', '7',
								    '8', '9', '10', '11', '12', '13', '14',
								    '15', '16', '17', 'wildcard', 'divisional', 'conference_championship',
								    'superbowl'];
	
	var availableWeeksAfter2021 = ['1', '2', '3', '4', '5', '6', '7',
	    '8', '9', '10', '11', '12', '13', '14',
	    '15', '16', '17', '18', 'wildcard', 'divisional', 'conference_championship',
	    'superbowl'];
	
	for (var index = 0; index < integerYearValues.length; index++){
		var integerYearValue = integerYearValues[index];
		
		if (integerYearValue >= 2021){
			return availableWeeksAfter2021;
		}
	}
	
	return availableWeeksBefore2021;
	
}

function updateAvailableWeekOptions(){
	
	var currentSelectedYearValues = getYearValuesForSelectedYears();
	
	var weeksToShow = ['1', '2', '3', '4', '5', '6', '7',
	    '8', '9', '10', '11', '12', '13', '14',
	    '15', '16', '17', 'wildcard', 'divisional', 'conference_championship',
	    'superbowl'];
	var weeksToHide = ['18'];
	
	for (var index = 0; index < currentSelectedYearValues.length; index++){
		var yearValue = currentSelectedYearValues[index];
		
		if (yearValue >= 2021){
			weeksToShow = ['1', '2', '3', '4', '5', '6', '7',
			    '8', '9', '10', '11', '12', '13', '14',
			    '15', '16', '17', '18', 'wildcard', 'divisional', 'conference_championship',
			    'superbowl'];
			
			weeksToHide = [];
		}
	}
	
	showWeekItems(weeksToShow);
	hideWeekItems(weeksToHide);
	
	//And, we have to go through and unselect the ones we should hide in case they 
	//were selected.  If we just hide them and they're still selected, they'll still
	//show up on the ui, just not in the selection dropdown.
	for (var index = 0; index < weeksToHide.length; index++){
		var weekToHide = weeksToHide[index];
		unselectWeekFull(weekToHide);
	}
}


/**
 * 
 * This function will set the given weeks as being selected in the UI and in the
 * NFL_PICKS_GLOBAL variable (NFL_PICKS_GLOBAL.selections.weeks).  
 * 
 * It expects the given weeks variable to either be...
 * 	1. An array of week numbers.
 * 	2. A comma separated string of week numbers.
 * 	3. A single week number.
 * 
 * It will put the actual week objects into the NFL_PICKS_GLOBAL variable for
 * each week number that's given.
 * 
 * @param weeks
 * @returns
 */
function setSelectedWeeks(weeks){
	
	//Steps to do:
	//	1. Check whether the weeks variable is an array.
	//	2. If it is, just keep it.
	//	3. Otherwise, it's a string so check to see if it has multiple values.
	//	4. If it does, then turn it into an array.
	//	5. Otherwise, just put it in there as a single value.
	//	6. Go through each week in the array, get the actual object for the week
	//	   and put it in the global variable.  And, "select" it in the ui.
	
	var weekValuesArray = [];
	
	var isArray = Array.isArray(weeks);
	
	if (isArray){
		weekValuesArray = weeks;
	}
	else {
		var hasMultipleValues = doesValueHaveMultipleValues(weeks);
		
		if (hasMultipleValues){
			weekValuesArray = delimitedValueToArray(weeks);
		}
		else {
			weekValuesArray.push(weeks);
		}
	}
	
	var weeksArray = [];
	
	for (var index = 0; index < weekValuesArray.length; index++){
		var value = weekValuesArray[index];
		selectWeek(value);

		var week = getWeek(value);
		weeksArray.push(week);
	}
	
	//THIS was the key ... update the current week selections... geez this is too complicated
	setCurrentWeekSelections(weekValuesArray);
	
	NFL_PICKS_GLOBAL.selections.weeks = weeksArray;
}