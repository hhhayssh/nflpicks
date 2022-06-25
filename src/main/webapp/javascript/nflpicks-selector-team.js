function onClickTeamSelector(event){
	event.stopPropagation();
	
	var wasSelectorVisible = isVisible('teamSelectorContainer'); 
	
	hideSelectorContainers();

	if (!wasSelectorVisible){
		resetTeamSelections();
		showTeamSelector();
	}
}

function onClickMultiselectTeamContainer(event){
	event.stopPropagation();
	
	var multiselectTeam = getMultiselectTeam();
	
	if (multiselectTeam){
		setMultiselectTeamValue(false);
	}
	else {
		setMultiselectTeamValue(true);
	}
	
	onClickMultiselectTeam(event);
}

function onClickMultiselectTeam(event){
	event.stopPropagation();
	
	var multiselectTeamChecked = $('#multiselectTeam').prop('checked');
	
	setMultiselectTeam(multiselectTeamChecked);
	
	if (multiselectTeamChecked){
		showMultiselectTeamContainer();
		showTeamCheckboxes();
		hideAllTeamSelectorContainer();
		hideTeamRadioButtons();
	}
	else {
		hideMultiselectTeamContainer();
		showAllTeamSelectorContainer();
		showTeamRadioButtons();
		hideTeamCheckboxes();
	}
}

function setMultiselectTeamValue(value){
	if (value){
		$('#multiselectTeam').prop('checked', true);
	}
	else {
		$('#multiselectTeam').prop('checked', false);
	}
}

function showAllTeamSelectorContainer(){
	$('#team-selector-container-all').show();
}

function hideAllTeamSelectorContainer(){
	$('#team-selector-container-all').hide();
}

function showMultiselectTeamContainer(){
	$('#multiselectTeamContainer').show();
}

function hideMultiselectTeamContainer(){
	$('#multiselectTeamContainer').hide();
}

function showTeamSelectorFooterContainer(){
	$('#team-selector-footer-container').show();
}

function hideTeamSelectorFooterContainer(){
	$('#team-selector-footer-container').hide();
}

//team-checkbox-input-
function showTeamCheckboxes(){
	var teamValues = getAllTeamValues();
	
	for (var index = 0; index < teamValues.length; index++){
		var teamValue = teamValues[index];
		showTeamCheckbox(teamValue);
	}
}

function showTeamCheckbox(teamValue){
	var normalizedValue = normalizeTeamValue(teamValue);
	$('#team-checkbox-input-' + normalizedValue).show();
}

function hideTeamCheckboxes(){
	
	var teamValues = getAllTeamValues();
	
	for (var index = 0; index < teamValues.length; index++){
		var teamValue = teamValues[index];
		hideTeamCheckbox(teamValue);
	}
}

function hideTeamCheckbox(teamValue){
	var normalizedValue = normalizeTeamValue(teamValue);
	$('#team-checkbox-input-' + normalizedValue).hide();
}

//team-radio-input-
function showTeamRadioButtons(){
	var teamValues = getAllTeamValues();
	
	for (var index = 0; index < teamValues.length; index++){
		var teamValue = teamValues[index];
		showTeamRadioButton(teamValue);
	}
}

function showTeamRadioButton(teamValue){
	var normalizedValue = normalizeTeamValue(teamValue);
	$('#team-radio-input-' + normalizedValue).show();
}

function hideTeamRadioButtons(){
	
	var teamValues = getAllTeamValues();
	
	for (var index = 0; index < teamValues.length; index++){
		var teamValue = teamValues[index];
		hideTeamRadioButton(teamValue);
	}
}

function hideTeamRadioButton(teamValue){
	var normalizedValue = normalizeTeamValue(teamValue);
	$('#team-radio-input-' + normalizedValue).hide();
}


function setMultiselectTeam(value){
	NFL_PICKS_GLOBAL.multiselectTeam = value;
}

function getMultiselectTeam(){
	return NFL_PICKS_GLOBAL.multiselectTeam;
}

function onClickTeamSelectionOk(event){
	event.stopPropagation();
	//If it's multi select here, unselect the all option.
	var multiselectTeam = getMultiselectTeam();
	if (multiselectTeam){
		removeTeamFromCurrentSelection('all');
	}
	hideTeamSelector();
	var currentTeams = getCurrentTeamSelections();
	setSelectedTeams(currentTeams);
	updateTeamsLink();
	updateView();
}

function onClickTeamSelectionCancel(event){
	event.stopPropagation();
	resetAndHideTeamSelections();
}

function resetTeamSelections(){
	unselectAllTeamsByValue();
	var selectedTeamValues = getSelectedTeamValues();
	setCurrentTeamSelections(selectedTeamValues);
	var currentTeams = getCurrentTeamSelections();
	selectTeamsByValue(currentTeams);
}

function resetAndHideTeamSelections(){
	resetTeamSelections();
	hideTeamSelector();
}

function showTeamSelector(){
	$('#teamSelectorContainer').show();
}

function hideTeamSelector(){
	$('#teamSelectorContainer').hide();
}

var currentTeamSelections = [];

function getCurrentTeamSelections(){
	return currentTeamSelections;
}

function setCurrentTeamSelections(updatedSelections){
	currentTeamSelections = updatedSelections;
}

function clearCurrentTeamSelections(){
	currentTeamSelections = [];
}


function onClickSelectAllTeams(event){
	
	event.stopPropagation();
	
	var teams = getAllTeams();
	
	clearCurrentTeamSelections();
	
	for (var index = 0; index < teams.length; index++){
		var team = teams[index];
		selectTeam(team.value);
		addTeamToCurrentSelection(team.value);
	}
}

function onClickClearTeams(event){
	
	event.stopPropagation();
	
	var realTeams = getRealTeams();
	
	clearCurrentTeamSelections();
	
	for (var index = 0; index < realTeams.length; index++){
		var team = realTeams[index];
		unselectTeam(team.value);
		removeTeamFromCurrentSelection(team.value);
	}
}

function onClickTeam(event, value){
	event.stopPropagation();
	
	var multiselectTeam = getMultiselectTeam();
	
	if (multiselectTeam){
		var indexOfValue = currentTeamSelections.indexOf(value);
		if (indexOfValue >= 0){
			unselectTeam(value);
			removeTeamFromCurrentSelection(value);
		}
		else {
			selectTeam(value);
			addTeamToCurrentSelection(value);
		}
	}
	else {
		clearCurrentTeamSelections();
		selectTeam(value);
		addTeamToCurrentSelection(value);
		onClickTeamSelectionOk(event);
	}
}

function updateTeamsLink(){
	
	var selectedTeams = getSelectedTeams();
	
	//If there aren't any selected teams, it should be "none"
	if (isEmpty(selectedTeams)){
		$('#teamsLink').text('No teams');
		return;
	}
	
	if (selectedTeams.length == 1 && 'all' == selectedTeams[0].value){
		$('#teamsLink').text('All teams');
		return;
	}
	
	sortOptionsByLabel(selectedTeams);
	
	var linkText = '';
	
	for (var index = 0; index < selectedTeams.length; index++){
		var team = selectedTeams[index];
		
		if (index > 0){
			linkText = linkText + ', ';
		}
		
		linkText = linkText + team.label;
	}
	
	$('#teamsLink').prop('title', linkText);
	
	if (linkText.length >= 25){
		linkText = linkText.substring(0, 25) + '...';
	}
	
	$('#teamsLink').text(linkText);
}

function showTeamsLink(){
	$('#teamsLink').show();
}

function hideTeamsLink(){
	$('#teamsLink').hide();
}

function selectAllTeamsByValue(){
	var teamValues = getAllTeamValues();
	selectTeamsByValue(teamValues);
}

function unselectAllTeamsByValue(){
	var teamValues = getAllTeamValues();
	unselectTeamsByValue(teamValues);
}

function selectTeamsByValue(values){
	for (var index = 0; index < values.length; index++){
		var value = values[index];
		selectTeam(value);
	}
}


function normalizeTeamValue(value){
	var normalizedValue = normalizeString(value);
	return normalizedValue;
}

function selectTeam(value){
	var normalizedValue = normalizeTeamValue(value);
	$('#team-checkbox-input-' + normalizedValue).prop('checked', true);
	$('#team-radio-input-' + normalizedValue).prop('checked', true);
}

function addTeamToCurrentSelection(value){
	currentTeamSelections.push(value);
}

function unselectTeamsByValue(teams){
	for (var index = 0; index < teams.length; index++){
		var team = teams[index];
		unselectTeam(team);
	}
}

function unselectTeam(team){
	var normalizedValue = normalizeTeamValue(team);
	$('#team-checkbox-input-' + normalizedValue).prop('checked', false);
	$('#team-radio-input-' + normalizedValue).prop('checked', false);
}

function removeTeamFromCurrentSelection(value){
	var indexOfValue = currentTeamSelections.indexOf(value);
	if (indexOfValue >= 0){
		currentTeamSelections.splice(indexOfValue, 1);
	}
}

/**
 * 
 * This function will cause the given team (the value should be the abbreviation)
 * to be the only team selected.
 * 
 * @param value
 * @returns
 */
function selectSingleTeamFull(value){
	clearCurrentTeamSelections();
	//just does the ui
	selectTeam(value);
	//just does the current selections
	addTeamToCurrentSelection(value);
	var currentTeams = getCurrentTeamSelections();
	setSelectedTeams(currentTeams);
}

/**
 * 
 * This function will completely unselect the team by its value.
 * It will make the team unselected on the ui, remove it from
 * the "current selections" array, and update the selected teams
 * in the NFL_PICKS_GLOBAL variable.
 * 
 * @param value
 * @returns
 */
function unselectTeamFull(value){
	unselectTeam(value);
	removeTeamFromCurrentSelection(value);
	var currentTeams = getCurrentTeamSelections();
	setSelectedTeams(currentTeams);
}

function getAllTeams(){
	return NFL_PICKS_GLOBAL.teams;
}

function getRealTeams(){
	return NFL_PICKS_GLOBAL.teams;
}

function getAllTeamValues(){
	var teamValues = [];
	
	for (var index = 0; index < NFL_PICKS_GLOBAL.teams.length; index++){
		var team = NFL_PICKS_GLOBAL.teams[index];
		teamValues.push(team.value);
	}
	
	return teamValues;
}

function getTeams(values){
	
	var teams = [];
	
	for (var index = 0; index < values.length; index++){
		var value = values[index];
		var team = getTeam(value);
		if (team != null){
			teams.push(team);
		}
	}
	
	return teams;
}

function getTeam(value){
	
	for (var index = 0; index < NFL_PICKS_GLOBAL.teams.length; index++){
		var team = NFL_PICKS_GLOBAL.teams[index];
		if (value == team.value){
			return team;
		}
	}
	
	return null;
}

function getSelectedTeamValues(){
	
	var teamValues = [];
	
	var selectedTeams = getSelectedTeams();
	
	for (var index = 0; index < selectedTeams.length; index++){
		var selectedTeam = selectedTeams[index];
		teamValues.push(selectedTeam.value);
	}
	
	return teamValues;
}

function getTeamValuesForRequest(){
	
	var selectedValues = getSelectedTeamValues();
	
	var valuesToSend = [];
	
	var realTeams = getRealTeams();
	
	for (var index = 0; index < selectedValues.length; index++){
		var selectedValue = selectedValues[index];
		
		if ('all' == selectedValue){
			for (var index2 = 0; index2 < realTeams.length; index2++){
				var realTeam = realTeams[index2];
				valuesToSend.push(realTeam.value);
			}
		}
		else {
			valuesToSend.push(selectedValue);
		}
	}
	
	var uniqueValues = getUniqueValuesFromArray(valuesToSend);
	
	var teamValuesForRequest = arrayToDelimitedValue(uniqueValues);
	
	return teamValuesForRequest;
}

function getSelectedTeams(){
	return NFL_PICKS_GLOBAL.selections.teams;
}

/*
 var normalizedValue = normalizeTeamValue(team.value);
		
		var divHtmlCssClass = 'selection-item-container';
		if (index + 1 == teams.length){
			divHtmlCssClass = 'selection-item-container-last';
		}

		var teamHtml = '<div id="team-selector-container-' + normalizedValue + '" class="' + divHtmlCssClass + '" onClick="onClickTeam(event, \'' + team.value + '\');">' +
 */

function getTeamSelectorContainerId(team){
	
	var normalizedTeamAbbreviation = normalizeTeamValue(team);
	
	var teamSelectorContainerId = 'team-selector-container-' + normalizedTeamAbbreviation;
	
	return teamSelectorContainerId;
}

function showTeamItem(team){
	var teamSelectorContainerId = getTeamSelectorContainerId(team);
	$('#' + teamSelectorContainerId).show();
}

function hideTeamItem(team){
	var teamSelectorContainerId = getTeamSelectorContainerId(team);
	$('#' + teamSelectorContainerId).hide();
}

function showTeamItems(teams){
	for (var index = 0; index < teams.length; index++){
		var team = teams[index];
		showTeamItem(team);
	}
}

function hideTeamItems(teams){
	for (var index = 0; index < teams.length; index++){
		var team = teams[index];
		hideTeamItem(team);
	}
}

function updateCurrentTeamSelections(){
	var selectedTeamValues = getSelectedTeamValues();
	setCurrentTeamSelections(selectedTeamValues);
}





/**
 * 
 * This function will say whether a "specific" team was selected
 * (basically if the team isn't "all").
 * 
 * @returns
 */
function isSpecificTeamSelected(){
	
	var selectedTeams = getSelectedTeams();
	
	if (selectedTeams.length > 1){
		return false;
	}
	
	var selectedTeam = selectedTeams[0].value;
	
	if ('all' == selectedTeam){
		return false;
	}
	
	return true;
}



function showTeamContainer(){
	$('#teamContainer').show();
}

function hideTeamContainer(){
	$('#teamContainer').hide();
}



/**
 * 
 * This function will update the available teams that can be selected.  It will just go through
 * and check whether each team was "active" during the selected years.  If they were, then it'll
 * show them and if they weren't, it'll hide them.
 * 
 * @returns
 */
function updateAvailableTeamOptions(){
	
	//Steps to do:
	//	1. Get the year values as integers.
	//	2. Go through every team and get when it started and ended.
	//	3. If the year it started is after any of the selected years and it doesn't have an
	//	   end or its end is before one of the selected years, that means it played games during
	//	   the selected years so it should be shown.
	//	4. Otherwise, it didn't and so it should be hidden.
	
	var currentSelectedYearValues = getYearValuesForSelectedYears();
	var integerYearValues = getValuesAsIntegers(currentSelectedYearValues);
	
	var teamsToShow = [];
	var teamsToHide = [];

	//All the teams are stored in the global variable.  Just have to go through them.
	var teams = NFL_PICKS_GLOBAL.data.teams;
	
	for (var index = 0; index < teams.length; index++){
		var team = teams[index];
		
		//Flipping this switch off and I'll flip it on if the team's start and end years show
		//it played games in the selected years.
		var showTeam = false;
		
		//Make sure to turn their years into numbers.
		var teamStartYearInteger = parseInt(team.startYear);
		var teamEndYearInteger = -1;
		if (isDefined(team.endYear)){
			teamEndYearInteger = parseInt(team.endYear);
		}

		//Go through each selected year.
		for (var yearIndex = 0; yearIndex < integerYearValues.length; yearIndex++){
			var currentYearValue = integerYearValues[yearIndex];
			
			//If the team started before the current year and either is still active (end year = -1) or was active after the
			//current year, that means it played games in the selected year, so it should be shown.
			if (teamStartYearInteger <= currentYearValue && (teamEndYearInteger == -1 || teamEndYearInteger >= currentYearValue)){
				showTeam = true;
			}
		}
		
		//Just put it in the list based on whether it should be shown or not.
		if (showTeam){
			teamsToShow.push(team.abbreviation);
		}
		else {
			teamsToHide.push(team.abbreviation);
		}
	}
	
	//Show the teams that should be shown in the selector dropdown.
	showTeamItems(teamsToShow);
	
	//Hide the teams that should be hidden in the selector.
	hideTeamItems(teamsToHide);
	
	//And, we have to go through and unselect the ones we should hide in case they 
	//were selected.  If we just hide them and they're still selected, they'll still
	//show up on the ui, just not in the selection dropdown.
	for (var index = 0; index < teamsToHide.length; index++){
		var teamToHide = teamsToHide[index];
		unselectTeamFull(teamToHide);
	}
	
}


/**
 * 
 * This function will set the given teams as being selected in the UI and in the
 * NFL_PICKS_GLOBAL variable (NFL_PICKS_GLOBAL.selections.teams).  
 * 
 * It expects the given teams variable to either be...
 * 	1. An array of team abbreviations.
 * 	2. A comma separated string of team abbreviations.
 * 	3. A single team abbreviation.
 * 
 * It will put the actual team objects into the NFL_PICKS_GLOBAL variable for
 * each team abbreviation that's given.
 * 
 * @param teams
 * @returns
 */
function setSelectedTeams(teams){
	
	//Steps to do:
	//	1. Check whether the teams variable is an array.
	//	2. If it is, just keep it.
	//	3. Otherwise, it's a string so check to see if it has multiple values.
	//	4. If it does, then turn it into an array.
	//	5. Otherwise, just put it in there as a single value.
	//	6. Go through each team in the array, get the actual object for the team
	//	   and put it in the global variable.  And, "select" it in the ui.
	
	var teamValuesArray = [];
	
	var isArray = Array.isArray(teams);
	
	if (isArray){
		teamValuesArray = teams;
	}
	else {
		var hasMultipleValues = doesValueHaveMultipleValues(teams);
		
		if (hasMultipleValues){
			teamValuesArray = delimitedValueToArray(teams);
		}
		else {
			teamValuesArray.push(teams);
		}
	}
	
	var teamsArray = [];
	
	for (var index = 0; index < teamValuesArray.length; index++){
		var value = teamValuesArray[index];
		selectTeam(value);

		var team = getTeam(value);
		teamsArray.push(team);
	}
	
	//THIS was the key ... update the current team selections... geez this is too complicated
	setCurrentTeamSelections(teamValuesArray);
	
	NFL_PICKS_GLOBAL.selections.teams = teamsArray;
}