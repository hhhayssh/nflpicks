function setTeamSelector(teamSelector){
	setCurrentActiveTeamSelector(teamSelector);
	NFL_PICKS_GLOBAL.selections.teamSelector = teamSelector;
}

function onClickTeam1Selector(event){
	event.stopPropagation();
	
	selectTeam1Container();
}

function onClickTeam2Selector(event){
	event.stopPropagation();
	
	selectTeam2Container();
}

//do this when initializing
function selectTeam1Container(){
	hideTeam2Container();
	showTeam1Container();
	
	showMultiSelectTeam1RowContainer();
	hideMultiSelectTeam2RowContainer();
	hideMultiSelectTeam2Container();
	
	//var multiSelectTeam1 = getMultiSelectTeam1();
	var multiSelectTeam1 = getCurrentMultiSelectTeam1();
	if (multiSelectTeam1){
		showMultiSelectTeam1Container();
	}
	else {
		hideMultiSelectTeam1Container();
	}
	
	//selected-team-selector
	//$('#team2SelectorContainer').removeClass('selected-team-selector');
	//$('#team1SelectorContainer').addClass('selected-team-selector');
	
	$('#team2SelectorRadioButton').prop('checked', false);
	$('#team1SelectorRadioButton').prop('checked', true);
	
	setCurrentActiveTeamSelector('team1');
}

function showTeam1Container(){
	$('#team-1-list-items-container').show();
}

function hideTeam1Container(){
	$('#team-1-list-items-container').hide();
}

function selectTeam2Container(){
	hideTeam1Container();
	showTeam2Container();
	
	hideMultiSelectTeam1RowContainer();
	hideMultiSelectTeam1Container();
	showMultiSelectTeam2RowContainer();
	
	var multiSelectTeam2 = getCurrentMultiSelectTeam2();
	if (multiSelectTeam2){
		showMultiSelectTeam2Container();
	}
	else {
		hideMultiSelectTeam2Container();
	}
	
	//$('#team1SelectorContainer').removeClass('selected-team-selector');
	//$('#team2SelectorContainer').addClass('selected-team-selector');
	
	$('#team1SelectorRadioButton').prop('checked', false);
	$('#team2SelectorRadioButton').prop('checked', true);
	
	setCurrentActiveTeamSelector('team2');
}

function showTeam2Container(){
	$('#team-2-list-items-container').show();
}

function hideTeam2Container(){
	$('#team-2-list-items-container').hide();
}

function onClickTeam1AtTeam2SelectorContainer(event){
	event.stopPropagation();
	
	var team1AtTeam2 = getTeam1AtTeam2();
	
	if (team1AtTeam2){
		setTeam1AtTeam2Value(false);
	}
	else {
		setTeam1AtTeam2Value(true);
	}
	
	onClickTeam1AtTeam2Selector(event);
}

function onClickTeam1AtTeam2Selector(event){
	event.stopPropagation();
	
	//setCurrentTeam1AtTeam2Selection
	var currentTeam1AtTeam2Selection = getCurrentTeam1AtTeam2Selection();
	
	var newTeam1AtTeam2Selection = null;
	if (currentTeam1AtTeam2Selection){
		newTeam1AtTeam2Selection = false;
	}
	else {
		newTeam1AtTeam2Selection = true;
	}
	setCurrentTeam1AtTeam2Selection(newTeam1AtTeam2Selection);
	
	var team1AtTeam2Checked = $('#team1AtTeam2').prop('checked');
	
	//setTeam1AtTeam2(team1AtTeam2Checked);
	
	updateTeam1Team2Labels();
}

function updateTeam1Team2Labels(){
	
	var team1AtTeam2 = getTeam1AtTeam2();
	
	if (team1AtTeam2){
		$('#team1SelectorLink').text('Team 1 (Away)');
		$('#team2SelectorLink').text('Team 2 (Home)');
	}
	else {
		$('#team1SelectorLink').text('Team 1');
		$('#team2SelectorLink').text('Team 2');
	}
}

function setTeam1AtTeam2Value(value){
	if (value){
		$('#team1AtTeam2').prop('checked', true);
	}
	else {
		$('#team1AtTeam2').prop('checked', false);
	}
}


function onClickTeamVsOrAtContainerSelector(event){
	event.stopPropagation();
	
	if (!isVisible('vsOrAtContainer')){
		showVsOrAtContainer();
	}
	else {
		hideVsOrAtContainer();
	}
}

function showVsOrAtContainer(){
	$('#vsOrAtContainer').show();
}

function hideVsOrAtContainer(){
	$('#vsOrAtContainer').hide();
}

function onClickTeamVsOrAtSelector(event){
	event.stopPropagation();
	
	toggleTeamVsOrAt();

	updateTeamVsOrAtLinkLabel();
	
	hideVsOrAtContainer();
}

function toggleTeamVsOrAt(){
	
	//var team1AtTeam2 = getTeam1AtTeam2();
	var team1AtTeam2 = getCurrentTeam1AtTeam2Selection();
	
	if (team1AtTeam2){
		setCurrentTeam1AtTeam2Selection(false);
		//setTeam1AtTeam2(false);
	}
	else {
		setCurrentTeam1AtTeam2Selection(true);
		//setTeam1AtTeam2(true);
	}
}

function updateTeamVsOrAtLinkLabel(){
	
	//var team1AtTeam2 = getTeam1AtTeam2();
	var team1AtTeam2 = getCurrentTeam1AtTeam2Selection();
	
	if (team1AtTeam2){
		setTeamVsOrAtLinkCurrentLabel('@');
		setTeamVsOrAtLinkNotCurrentLabel('vs');
	}
	else {
		setTeamVsOrAtLinkCurrentLabel('vs');
		setTeamVsOrAtLinkNotCurrentLabel('@');
	}
}

function setTeamVsOrAtLinkCurrentLabel(label){
	$('#vsOrAtSelectorLinkCurrent').text(label);
}

function setTeamVsOrAtLinkNotCurrentLabel(label){
	$('#vsOrAtSelectorLinkNotCurrent').text(label);
}

function onClickTeam1(event, value){
	event.stopPropagation();
	
	setCurrentActiveTeamSelector('team1');
	
	//var multiSelectTeam1 = getMultiSelectTeam1();
	var multiSelectTeam1 = getCurrentMultiSelectTeam1();
	
	if (multiSelectTeam1){
		var currentTeam1Selections = getCurrentTeam1Selections();
		var indexOfValue = currentTeam1Selections.indexOf(value);
		if (indexOfValue >= 0){
			unselectTeam1(value);
			removeTeam1FromCurrentSelection(value);
		}
		else {
			selectTeam1(value);
			addTeam1ToCurrentSelection(value);
		}
	}
	else {
		clearCurrentTeam1Selections();
		selectTeam1(value);
		addTeam1ToCurrentSelection(value);
		//onClickTeam1SelectionOk(event);
		onClickTeamSelectionOk(event);
	}
}


function onClickTeam2(event, value){
	event.stopPropagation();
	
	setCurrentActiveTeamSelector('team2');
	
	var multiSelectTeam2 = getCurrentMultiSelectTeam2();
	
	if (multiSelectTeam2){
		var currentTeam2Selections = getCurrentTeam2Selections();
		var indexOfValue = currentTeam2Selections.indexOf(value);
		if (indexOfValue >= 0){
			unselectTeam2(value);
			removeTeam2FromCurrentSelection(value);
		}
		else {
			selectTeam2(value);
			addTeam2ToCurrentSelection(value);
		}
	}
	else {
		clearCurrentTeam2Selections();
		selectTeam2(value);
		addTeam2ToCurrentSelection(value);
		//onClickTeam2SelectionOk(event);
		onClickTeamSelectionOk(event);
	}
}

//The team selector that they picked (team1 or team2).
//Here so we can remember which one they were on when we open the menu because
//that makes it a little easier and better.
var currentActiveTeamSelector = null;

function setCurrentActiveTeamSelector(activeTeamSelector){
	currentActiveTeamSelector = activeTeamSelector;
}

function getCurrentActiveTeamSelector(){
	return currentActiveTeamSelector;
}

var currentTeam1AtTeam2Selection = null;

function getCurrentTeam1AtTeam2Selection(){
	return currentTeam1AtTeam2Selection;
}

function setCurrentTeam1AtTeam2Selection(updatedSelection){
	currentTeam1AtTeam2Selection = updatedSelection;
}

function getTeam1AtTeam2(){
	return NFL_PICKS_GLOBAL.team1AtTeam2;
}

function setTeam1AtTeam2(team1AtTeam2){
	NFL_PICKS_GLOBAL.team1AtTeam2 = team1AtTeam2;
}


var currentMultiSelectTeam1Selections = [];

var currentSingleSelectTeam1Selections = [];

//split this into multi and single team selections
var currentTeam1Selections = [];

var currentMultiSelectTeam1 = false;

function getCurrentTeam1Selections(){
	
	//var multiSelectTeam1 = getMultiSelectTeam1();
	var multiSelectTeam1 = getCurrentMultiSelectTeam1();
	
	var selections = currentSingleSelectTeam1Selections;
	
	if (multiSelectTeam1){
		selections = getCurrentMultiSelectTeam1Selections();
	}
	else {
		selections = getCurrentSingleSelectTeam1Selections();
	}
	
	return selections;
}

function setCurrentTeam1Selections(updatedSelections){
	
	//var multiSelectTeam1 = getMultiSelectTeam1();
	var multiSelectTeam1 = getCurrentMultiSelectTeam1();
	
	if (multiSelectTeam1){
		setCurrentMultiSelectTeam1Selections(updatedSelections);
	}
	else {
		setCurrentSingleSelectTeam1Selections(updatedSelections);
	}
	
}

//not sure if this should be both or not ...
function clearCurrentTeam1Selections(){
	//currentTeam1Selections = [];
	
	//var multiSelectTeam1 = getMultiSelectTeam1();
	var multiSelectTeam1 = getCurrentMultiSelectTeam1();
	
	if (multiSelectTeam1){
		clearCurrentMultiSelectTeam1Selections();
	}
	else {
		clearCurrentSingleSelectTeam1Selections();
	}
}



function getCurrentMultiSelectTeam1Selections(){
	return currentMultiSelectTeam1Selections;
}

function setCurrentMultiSelectTeam1Selections(updatedSelections){
	currentMultiSelectTeam1Selections = updatedSelections;
}

function clearCurrentMultiSelectTeam1Selections(){
	currentMultiSelectTeam1Selections = [];
}

function getCurrentSingleSelectTeam1Selections(){
	return currentSingleSelectTeam1Selections;
}

function setCurrentSingleSelectTeam1Selections(updatedSelections){
	currentSingleSelectTeam1Selections = updatedSelections;
}

function clearCurrentSingleSelectTeam1Selections(){
	currentSingleSelectTeam1Selections = [];
}

function getCurrentMultiSelectTeam1(){
	return currentMultiSelectTeam1;
}

function setCurrentMultiSelectTeam1(updatedMultiSelect){
	currentMultiSelectTeam1 = updatedMultiSelect;
}

function isTeamInTeam1CurrentSelections(team){
	
	var normalizedValue = normalizeTeamValue(team);
	
	var selections = getCurrentTeam1Selections();
	
	for (var index = 0; index < selections.length; index++){
		var selection = selections[index];
		
		if (normalizedValue == selection){
			return true;
		}
	}
	
	return false;
}

function isSpecificTeam1InCurrentSelections(){
	
	var currentTeam1Selections = getCurrentTeam1Selections();
	
	if (isEmpty(currentTeam1Selections)){
		return false;
	}
	
	var allItem = normalizeTeamValue('all');
	var firstItem = currentTeam1Selections[0];
	
	if (currentTeam1Selections.length == 1 && allItem == firstItem){
		return false;
	}
	
	return true;
}

function selectTeam1(value){
	var normalizedValue = normalizeTeamValue(value);
	
	//var multiSelectTeam1 = getMultiSelectTeam1();
	var currentMultiSelectTeam1 = getCurrentMultiSelectTeam1();
	
	if (currentMultiSelectTeam1){
		$('#team-1-checkbox-input-' + normalizedValue).prop('checked', true);	
	}
	else {
		$('#team-1-radio-input-' + normalizedValue).prop('checked', true);
	}
}

function unselectTeam1(team){
	var normalizedValue = normalizeTeamValue(team);
	
	//var multiSelectTeam1 = getMultiSelectTeam1();
	var currentMultiSelectTeam1 = getCurrentMultiSelectTeam1();
	
	if (currentMultiSelectTeam1){
		$('#team-1-checkbox-input-' + normalizedValue).prop('checked', false);	
	}
	else {
		$('#team-1-radio-input-' + normalizedValue).prop('checked', false);
	}
}

function removeTeam1FromCurrentSelection(value){
	var currentTeam1Selections = getCurrentTeam1Selections();
	var indexOfValue = currentTeam1Selections.indexOf(value);
	if (indexOfValue >= 0){
		currentTeam1Selections.splice(indexOfValue, 1);
		setCurrentTeam1Selections(currentTeam1Selections);
	}
}

function addTeam1ToCurrentSelection(value){
	
	//var multiSelectTeam1 = getMultiSelectTeam1();
	var multiSelectTeam1 = getCurrentMultiSelectTeam1();
	
	if (multiSelectTeam1){
		var selections = getCurrentMultiSelectTeam1Selections();
		selections.push(value);
		setCurrentMultiSelectTeam1Selections(selections);
	}
	else {
		var selections = getCurrentSingleSelectTeam1Selections();
		selections.push(value);
		setCurrentSingleSelectTeam1Selections(selections);
	}
}

var currentMultiSelectTeam2Selections = [];

var currentSingleSelectTeam2Selections = [];

var currentTeam2Selections = [];

var currentMultiSelectTeam2 = false;

function getCurrentTeam2Selections(){
	
	var multiSelectTeam2 = getCurrentMultiSelectTeam2();
	
	var selections = currentSingleSelectTeam2Selections;
	
	if (multiSelectTeam2){
		selections = getCurrentMultiSelectTeam2Selections();
	}
	else {
		selections = getCurrentSingleTeam2Selections();
	}
	
	return selections;
}

function setCurrentTeam2Selections(updatedSelections){
	
	var multiSelectTeam2 = getCurrentMultiSelectTeam2();
	
	if (multiSelectTeam2){
		setCurrentMultiTeam2Selections(updatedSelections);
	}
	else {
		setCurrentSingleTeam2Selections(updatedSelections);
	}
	
}

//not sure if this should be both or not ...
function clearCurrentTeam2Selections(){
	//currentTeam1Selections = [];
	
	var multiSelectTeam2 = getCurrentMultiSelectTeam2();
	
	if (multiSelectTeam2){
		clearCurrentMultiTeam2Selections();
	}
	else {
		clearCurrentSingleTeam2Selections();
	}
}

function getCurrentMultiSelectTeam2Selections(){
	return currentMultiSelectTeam2Selections;
}

function setCurrentMultiTeam2Selections(updatedSelections){
	currentMultiSelectTeam2Selections = updatedSelections;
}

function clearCurrentMultiTeam2Selections(){
	currentMultiSelectTeam2Selections = [];
}

function getCurrentSingleTeam2Selections(){
	return currentSingleSelectTeam2Selections;
}

function setCurrentSingleTeam2Selections(updatedSelections){
	currentSingleSelectTeam2Selections = updatedSelections;
}

function clearCurrentSingleTeam2Selections(){
	currentSingleSelectTeam2Selections = [];
}

function getCurrentMultiSelectTeam2(){
	return currentMultiSelectTeam2;
}

function setCurrentMultiSelectTeam2(updatedMultiSelect){
	currentMultiSelectTeam2 = updatedMultiSelect;
}



function selectTeam2(value){
	var normalizedValue = normalizeTeamValue(value);
	
	var multiSelectTeam2 = getCurrentMultiSelectTeam2();
	
	if (multiSelectTeam2){
		$('#team-2-checkbox-input-' + normalizedValue).prop('checked', true);	
	}
	else {
		$('#team-2-radio-input-' + normalizedValue).prop('checked', true);
	}
}

function unselectTeam2(team){
	var normalizedValue = normalizeTeamValue(team);
	
	var currentMultiSelectTeam2 = getCurrentMultiSelectTeam2();
	
	if (currentMultiSelectTeam2){
		$('#team-2-checkbox-input-' + normalizedValue).prop('checked', false);	
	}
	else {
		$('#team-2-radio-input-' + normalizedValue).prop('checked', false);
	}
}

function removeTeam2FromCurrentSelection(value){
	var currentTeam2Selections = getCurrentTeam2Selections();
	var indexOfValue = currentTeam2Selections.indexOf(value);
	if (indexOfValue >= 0){
		currentTeam2Selections.splice(indexOfValue, 1);
		setCurrentTeam2Selections(currentTeam2Selections);
	}
}

function addTeam2ToCurrentSelection(value){
	
	var multiSelectTeam2 = getCurrentMultiSelectTeam2();
	
	if (multiSelectTeam2){
		var selections = getCurrentMultiSelectTeam2Selections();
		selections.push(value);
		setCurrentMultiTeam2Selections(selections);
	}
	else {
		var selections = getCurrentSingleTeam2Selections();
		selections.push(value);
		setCurrentSingleTeam2Selections(selections);
	}
}


function isTeamInTeam2CurrentSelections(team){
	
	var normalizedValue = normalizeTeamValue(team);
	
	var selections = getCurrentTeam2Selections();
	
	for (var index = 0; index < selections.length; index++){
		var selection = selections[index];
		
		if (normalizedValue == selection){
			return true;
		}
	}
	
	return false;
}

function isSpecificTeam2InCurrentSelections(){
	
	var currentTeam2Selections = getCurrentTeam2Selections();
	
	if (isEmpty(currentTeam2Selections)){
		return false;
	}
	
	var allItem = normalizeTeamValue('all');
	var firstItem = currentTeam2Selections[0];
	
	if (currentTeam2Selections.length == 1 && allItem == firstItem){
		return false;
	}
	
	return true;
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
function setSelectedTeams1(teams){
	
	//Steps to do:
	//	1. Check whether the teams variable is an array.resetTeam1Selections
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
		selectTeam1(value);

		var team = getTeam(value);
		teamsArray.push(team);
	}
	
	//THIS was the key ... update the current team selections... geez this is too complicated
	setCurrentTeam1Selections(teamValuesArray);
	
	NFL_PICKS_GLOBAL.selections.teams1 = teamsArray;
} 

function resetTeamSelector(){
	
	var teamSelector = NFL_PICKS_GLOBAL.selections.teamSelector;
	
	if ('team1' == teamSelector){
		selectTeam1Container();
	}
	else if ('team2' == teamSelector){
		selectTeam2Container();
	}
}


//this needs to reset team 1 and 2 selections
function resetTeam1Selections(){
	unselectAllTeams1ByValue();
	var selectedTeamValues = getSelectedTeam1Values();
	setCurrentTeam1Selections(selectedTeamValues);
	var currentTeams = getCurrentTeam1Selections();
	selectTeams1ByValue(currentTeams);
	
	var multiSelectTeam1 = getMultiSelectTeam1();
	setCurrentMultiSelectTeam1(multiSelectTeam1);
	setMultiSelectTeam1Value(multiSelectTeam1);
	
	updateTeam1MultiSelectDisplay(multiSelectTeam1);
}

function updateTeam1MultiSelectDisplay(multiSelectTeam1){
	
	if (multiSelectTeam1){
		showMultiSelectTeam1Container();
		showTeam1Checkboxes();
		hideAllTeam1SelectorContainer();
		hideTeam1RadioButtons();
	}
	else {
		hideMultiSelectTeam1Container();
		hideTeam1Checkboxes();
		showAllTeam1SelectorContainer();
		showTeam1RadioButtons();
	}
}

function unselectAllTeams1ByValue(){
	var teamValues = getAllTeamValues();
	unselectTeams1ByValue(teamValues);
}

function unselectTeams1AllItem(){
	
	var allItem = getAllTeamsItem();
	
	var teams = [];
	teams.push('all');
	
	unselectTeams1ByValue(teams);
}

function unselectTeams1ByValue(teams){
	for (var index = 0; index < teams.length; index++){
		var team = teams[index];
		unselectTeam1(team);
	}
}

function getSelectedTeam1Values(){
	
	var teamValues = [];
	
	var selectedTeams = getSelectedTeams1();
	
	for (var index = 0; index < selectedTeams.length; index++){
		var selectedTeam = selectedTeams[index];
		teamValues.push(selectedTeam.value);
	}
	
	return teamValues;
}

function selectTeams1AllItem(){
	
	var teams = [];
	teams.push('all');
	
	selectTeams1ByValue(teams);
}

function selectTeams1ByValue(values){
	for (var index = 0; index < values.length; index++){
		var value = values[index];
		selectTeam1(value);
	}
}

function getTeam1ValuesForRequest(){
	
	var selectedValues = getSelectedTeam1Values();
	
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
	
	var team1ValuesForRequest = arrayToDelimitedValue(uniqueValues);
	
	return team1ValuesForRequest;
}

function setSelectedTeams2(teams){
	
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
		selectTeam2(value);

		var team = getTeam(value);
		teamsArray.push(team);
	}
	
	//THIS was the key ... update the current team selections... geez this is too complicated
	setCurrentTeam2Selections(teamValuesArray);
	
	NFL_PICKS_GLOBAL.selections.teams2 = teamsArray;
}

function resetTeam2Selections(){
	unselectAllTeams2ByValue();
	var selectedTeamValues = getSelectedTeam2Values();
	setCurrentTeam2Selections(selectedTeamValues);
	var currentTeams = getCurrentTeam2Selections();
	selectTeams2ByValue(currentTeams);
	
	var multiSelectTeam2 = getMultiSelectTeam2();
	setCurrentMultiSelectTeam2(multiSelectTeam2);
	setMultiSelectTeam2Value(multiSelectTeam2);
	updateTeam2MultiSelectDisplay(multiSelectTeam2);
	
}

function updateTeam2MultiSelectDisplay(multiSelectTeam2){
	
	if (multiSelectTeam2){
		showMultiSelectTeam2Container();
		showTeam2Checkboxes();
		hideAllTeam2SelectorContainer();
		hideTeam2RadioButtons();
	}
	else {
		hideMultiSelectTeam2Container();
		hideTeam2Checkboxes();
		showAllTeam2SelectorContainer();
		showTeam2RadioButtons();
	}
}

function unselectAllTeams2ByValue(){
	var teamValues = getAllTeamValues();
	unselectTeams2ByValue(teamValues);
}

function unselectTeams2ByValue(teams){
	for (var index = 0; index < teams.length; index++){
		var team = teams[index];
		unselectTeam2(team);
	}
}

function getSelectedTeam2Values(){
	
	var teamValues = [];
	
	var selectedTeams = getSelectedTeams2();
	
	for (var index = 0; index < selectedTeams.length; index++){
		var selectedTeam = selectedTeams[index];
		teamValues.push(selectedTeam.value);
	}
	
	return teamValues;
}

function selectTeams2ByValue(values){
	for (var index = 0; index < values.length; index++){
		var value = values[index];
		selectTeam2(value);
	}
}

function getTeam2ValuesForRequest(){
	
	var selectedValues = getSelectedTeam2Values();
	
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
	
	var team2ValuesForRequest = arrayToDelimitedValue(uniqueValues);
	
	return team2ValuesForRequest;
}

function getTeam1AtTeam2ValueForRequest(){
	
	var team1AtTeam2 = getTeam1AtTeam2();
	
	var team1AtTeam2Value = 'false';
	
	if (team1AtTeam2){
		team1AtTeam2Value = 'true';
	}
	
	return team1AtTeam2Value;
}
/*
function hideAllTeam1SelectorContainer(){
	$('#team-1-selector-container-all').hide();
}

function showAllTeam1SelectorContainer(){
	$('#team-1-selector-container-all').show();
}

function hideAllTeam2SelectorContainer(){
	$('#team-2-selector-container-all').hide();
}

function showAllTeam2SelectorContainer(){
	$('#team-2-selector-container-all').show();
}
*/

function onClickTeamSelector(event){
	event.stopPropagation();
	
	var wasSelectorVisible = isVisible('teamSelectorContainer'); 
	
	hideSelectorContainers();

	if (!wasSelectorVisible){
		resetTeam1AtTeam2Selection();
		resetTeam1Selections();
		resetTeam2Selections();
		resetTeamSelector();
		showTeamSelector();
	}
}

function onClickMultiSelectTeamContainer(event){
	event.stopPropagation();
	
	var multiSelectTeam = getMultiSelectTeam();
	
	if (multiSelectTeam){
		setMultiSelectTeamValue(false);
	}
	else {
		setMultiSelectTeamValue(true);
	}
	
	onClickMultiSelectTeam(event);
}

function onClickMultiSelectTeam(event){
	event.stopPropagation();
	
	var multiSelectTeamChecked = $('#multiSelectTeam').prop('checked');
	
	setMultiSelectTeam(multiSelectTeamChecked);
	
	if (multiSelectTeamChecked){
		showMultiSelectTeamContainer();
		showTeamCheckboxes();
		hideAllTeamSelectorContainer();
		hideAllTeam1SelectorContainer();
		hideAllTeam2SelectorContainer();
		hideTeamRadioButtons();
	}
	else {
		hideMultiSelectTeamContainer();
		showAllTeamSelectorContainer();
		showAllTeam1SelectorContainer();
		showAllTeam2SelectorContainer();
		showTeamRadioButtons();
		hideTeamCheckboxes();
	}
}

function setMultiSelectTeamValue(value){
	if (value){
		$('#multiSelectTeam').prop('checked', true);
	}
	else {
		$('#multiSelectTeam').prop('checked', false);
	}
}

function showAllTeamSelectorContainer(){
	$('#team-selector-container-all').show();
}

function hideAllTeamSelectorContainer(){
	$('#team-selector-container-all').hide();
}

function showMultiSelectTeamContainer(){
	$('#multiSelectTeamContainer').show();
}

function hideMultiSelectTeamContainer(){
	$('#multiSelectTeamContainer').hide();
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
	$('#team-1-checkbox-input-' + normalizedValue).show();
	$('#team-2-checkbox-input-' + normalizedValue).show();
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
	$('#team-1-checkbox-input-' + normalizedValue).hide();
	$('#team-2-checkbox-input-' + normalizedValue).hide();
}

//team-radio-input-
function showTeamRadioButtons(){
	var teamValues = getAllTeamValues();
	
	for (var index = 0; index < teamValues.length; index++){
		var teamValue = teamValues[index];
		showTeamRadioButton(teamValue);
	}
}

//this needs to change so it's per team now.
function showTeamRadioButton(teamValue){
	var normalizedValue = normalizeTeamValue(teamValue);
	$('#team-1-radio-input-' + normalizedValue).show();
	$('#team-2-radio-input-' + normalizedValue).show();
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
	$('#team-1-radio-input-' + normalizedValue).hide();
	$('#team-2-radio-input-' + normalizedValue).hide();
}


function setMultiSelectTeam(value){
	NFL_PICKS_GLOBAL.multiSelectTeam = value;
}

function getMultiSelectTeam(){
	return NFL_PICKS_GLOBAL.multiSelectTeam;
}



function setMultiSelectTeam1(value){
	NFL_PICKS_GLOBAL.multiSelectTeam1 = value;
}

function getMultiSelectTeam1(){
	return NFL_PICKS_GLOBAL.multiSelectTeam1;
}

function setMultiSelectTeam2(value){
	NFL_PICKS_GLOBAL.multiSelectTeam2 = value;
}

function getMultiSelectTeam2(){
	return NFL_PICKS_GLOBAL.multiSelectTeam2;
}




















function onClickMultiSelectTeam1Container(event){
	event.stopPropagation();
	
	onClickMultiSelectTeam1(event);
}

function onClickMultiSelectTeam1(event){
	event.stopPropagation();
	
	var currentMultiSelectTeam1Checked = getCurrentMultiSelectTeam1();
	
	var newMultiSelectTeam1 = null;
	if (currentMultiSelectTeam1Checked){
		newMultiSelectTeam1 = false;
	}
	else {
		newMultiSelectTeam1 = true;
	}
	
	setCurrentMultiSelectTeam1(newMultiSelectTeam1);
	setMultiSelectTeam1Value(newMultiSelectTeam1);
	
	updateTeam1MultiSelectDisplay(newMultiSelectTeam1);
}






function setMultiSelectTeam1Value(value){
	if (value){
		$('#multiSelectTeam1').prop('checked', true);
	}
	else {
		$('#multiSelectTeam1').prop('checked', false);
	}
}


//multiSelectTeam1RowContainer

function showMultiSelectTeam1RowContainer(){
	$('#multiSelectTeam1RowContainer').show();
}

function hideMultiSelectTeam1RowContainer(){
	$('#multiSelectTeam1RowContainer').hide();
}

function showAllTeam1SelectorContainer(){
	$('#team-1-selector-container-all').show();
}

function hideAllTeam1SelectorContainer(){
	$('#team-1-selector-container-all').hide();
}

function showMultiSelectTeam1Container(){
	$('#multiSelectTeam1Container').show();
}

function hideMultiSelectTeam1Container(){
	$('#multiSelectTeam1Container').hide();
}

function showTeam1SelectorFooterContainer(){
	$('#team1-selector-footer-container').show();
}

function hideTeam1SelectorFooterContainer(){
	$('#team1-selector-footer-container').hide();
}

//team-checkbox-input-
function showTeam1Checkboxes(){
	var teamValues = getAllTeamValues();
	
	for (var index = 0; index < teamValues.length; index++){
		var teamValue = teamValues[index];
		showTeam1Checkbox(teamValue);
	}
}

function showTeam1Checkbox(teamValue){
	var normalizedValue = normalizeTeamValue(teamValue);
	$('#team-1-checkbox-input-' + normalizedValue).show();
}

function hideTeam1Checkboxes(){
	
	var teamValues = getAllTeamValues();
	
	for (var index = 0; index < teamValues.length; index++){
		var teamValue = teamValues[index];
		hideTeam1Checkbox(teamValue);
	}
}

function hideTeam1Checkbox(teamValue){
	var normalizedValue = normalizeTeamValue(teamValue);
	$('#team-1-checkbox-input-' + normalizedValue).hide();
}

//team-radio-input-
function showTeam1RadioButtons(){
	var teamValues = getAllTeamValues();
	
	for (var index = 0; index < teamValues.length; index++){
		var teamValue = teamValues[index];
		showTeam1RadioButton(teamValue);
	}
}

//this needs to change so it's per team now.
function showTeam1RadioButton(teamValue){
	var normalizedValue = normalizeTeamValue(teamValue);
	$('#team-1-radio-input-' + normalizedValue).show();
}

function hideTeam1RadioButtons(){
	
	var teamValues = getAllTeamValues();
	
	for (var index = 0; index < teamValues.length; index++){
		var teamValue = teamValues[index];
		hideTeam1RadioButton(teamValue);
	}
}

function hideTeam1RadioButton(teamValue){
	var normalizedValue = normalizeTeamValue(teamValue);
	$('#team-1-radio-input-' + normalizedValue).hide();
}




function onClickMultiSelectTeam2Container(event){
	event.stopPropagation();
	
	onClickMultiSelectTeam2(event);
}

function onClickMultiSelectTeam2(event){
	event.stopPropagation();
	
	var currentMultiSelectTeam2Checked = getCurrentMultiSelectTeam2();
	
	var newMultiSelectTeam2 = null;
	if (currentMultiSelectTeam2Checked){
		newMultiSelectTeam2 = false;
	}
	else {
		newMultiSelectTeam2 = true;
	}
	
	setCurrentMultiSelectTeam2(newMultiSelectTeam2);
	setMultiSelectTeam2Value(newMultiSelectTeam2);
	
	updateTeam2MultiSelectDisplay(newMultiSelectTeam2);
}

function setMultiSelectTeam2Value(value){
	if (value){
		$('#multiSelectTeam2').prop('checked', true);
	}
	else {
		$('#multiSelectTeam2').prop('checked', false);
	}
}

function showMultiSelectTeam2RowContainer(){
	$('#multiSelectTeam2RowContainer').show();
}

function hideMultiSelectTeam2RowContainer(){
	$('#multiSelectTeam2RowContainer').hide();
}


function showAllTeam2SelectorContainer(){
	$('#team-2-selector-container-all').show();
}

function hideAllTeam2SelectorContainer(){
	$('#team-2-selector-container-all').hide();
}

function showMultiSelectTeam2Container(){
	$('#multiSelectTeam2Container').show();
}

function hideMultiSelectTeam2Container(){
	$('#multiSelectTeam2Container').hide();
}

function showTeam2SelectorFooterContainer(){
	$('#team2-selector-footer-container').show();
}

function hideTeam2SelectorFooterContainer(){
	$('#team2-selector-footer-container').hide();
}

//team-checkbox-input-
function showTeam2Checkboxes(){
	var teamValues = getAllTeamValues();
	
	for (var index = 0; index < teamValues.length; index++){
		var teamValue = teamValues[index];
		showTeam2Checkbox(teamValue);
	}
}

function showTeam2Checkbox(teamValue){
	var normalizedValue = normalizeTeamValue(teamValue);
	$('#team-2-checkbox-input-' + normalizedValue).show();
}

function hideTeam2Checkboxes(){
	
	var teamValues = getAllTeamValues();
	
	for (var index = 0; index < teamValues.length; index++){
		var teamValue = teamValues[index];
		hideTeam2Checkbox(teamValue);
	}
}

function hideTeam2Checkbox(teamValue){
	var normalizedValue = normalizeTeamValue(teamValue);
	$('#team-2-checkbox-input-' + normalizedValue).hide();
}

//team-radio-input-
function showTeam2RadioButtons(){
	var teamValues = getAllTeamValues();
	
	for (var index = 0; index < teamValues.length; index++){
		var teamValue = teamValues[index];
		showTeam2RadioButton(teamValue);
	}
}

//this needs to change so it's per team now.
function showTeam2RadioButton(teamValue){
	var normalizedValue = normalizeTeamValue(teamValue);
	$('#team-2-radio-input-' + normalizedValue).show();
}

function hideTeam2RadioButtons(){
	
	var teamValues = getAllTeamValues();
	
	for (var index = 0; index < teamValues.length; index++){
		var teamValue = teamValues[index];
		hideTeam2RadioButton(teamValue);
	}
}

function hideTeam2RadioButton(teamValue){
	var normalizedValue = normalizeTeamValue(teamValue);
	$('#team-2-radio-input-' + normalizedValue).hide();
}































function onClickTeamSelectionOk(event){
	event.stopPropagation();
	
	//Make the selection official at this point.
	var multiSelectTeam1 = getCurrentMultiSelectTeam1();
	setMultiSelectTeam1(multiSelectTeam1);

	//If it's multi select here, unselect the all option.
	if (multiSelectTeam1){
		removeTeam1FromCurrentSelection('all');
	}
	
	var multiSelectTeam2 = getCurrentMultiSelectTeam2();
	setMultiSelectTeam2(multiSelectTeam2);
	if (multiSelectTeam2){
		removeTeam2FromCurrentSelection('all');
	}
	
	var team1AtTeam2 = getCurrentTeam1AtTeam2Selection();
	setTeam1AtTeam2(team1AtTeam2);
	
	hideTeamSelector();
	var currentTeams1 = getCurrentTeam1Selections();
	setSelectedTeams1(currentTeams1);
	
	var currentTeams2 = getCurrentTeam2Selections();
	setSelectedTeams2(currentTeams2);
	
	var teamSelector = getCurrentActiveTeamSelector();
	setTeamSelector(teamSelector);
	
	//this one is next
	//	ARZ, BAL @ ATL, CLE, ...
	// make elipses for each team
	// and that'll make it for the full thing
	// if only one team is selected, it should be
	//	ATL vs All
	//	All @ ATL
	updateTeamsLink();
	//and then we add them for the request
	//then, update the java side
	updateView();
}

function onClickTeamSelectionCancel(event){
	event.stopPropagation();
	resetAndHideTeamSelections();
}

function resetAndHideTeamSelections(){
	resetTeam1AtTeam2Selection();
	resetTeam1Selections();
	resetTeam2Selections();
	hideTeamSelector();
}

function resetTeam1AtTeam2Selection(){
	var team1AtTeam2 = getTeam1AtTeam2();
	setCurrentTeam1AtTeam2Selection(team1AtTeam2);
	updateTeamVsOrAtLinkLabel();
}

function showTeamSelector(){
	$('#teamSelectorContainer').show();
}

function hideTeamSelector(){
	$('#teamSelectorContainer').hide();
}

function isTeam1Selected(){
	var isTeam1Selected = $('#team1SelectorRadioButton').prop('checked');
	return isTeam1Selected;
}

function isTeam2Selected(){
	var isTeam2Selected = $('#team2SelectorRadioButton').prop('checked');
	return isTeam2Selected;
}

function onClickSelectAllTeams(event){
	
	//get the current active team (team 1 or 2)
	//and select them all
	
	event.stopPropagation();
	
	var teams = getAllTeams();
	
	var team1Selected = isTeam1Selected();
	var team2Selected = isTeam2Selected();
	
	if (team1Selected){
		clearCurrentTeam1Selections();
		
		for (var index = 0; index < teams.length; index++){
			var team = teams[index];
			selectTeam1(team.value);
			addTeam1ToCurrentSelection(team.value);
		}
	}
	else if (team2Selected){
		clearCurrentTeam2Selections();
		
		for (var index = 0; index < teams.length; index++){
			var team = teams[index];
			selectTeam2(team.value);
			addTeam2ToCurrentSelection(team.value);
		}
	}
}

function onClickClearTeams(event){
	
	event.stopPropagation();
	
	var realTeams = getRealTeams();
	
	var team1Selected = isTeam1Selected();
	var team2Selected = isTeam2Selected();
	
	if (team1Selected){
		clearCurrentTeam1Selections();
		
		for (var index = 0; index < realTeams.length; index++){
			var team = realTeams[index];
			unselectTeam1(team.value);
			removeTeam1FromCurrentSelection(team.value);
		}
	}
	else if (team2Selected){
		clearCurrentTeam2Selections();
		
		for (var index = 0; index < realTeams.length; index++){
			var team = realTeams[index];
			unselectTeam2(team.value);
			removeTeam2FromCurrentSelection(team.value);
		}
	}
}















function onClickSelectAllTeams1(event){
	
	//get the current active team (team 1 or 2)
	//and select them all
	
	event.stopPropagation();
	
	var teams = getAllTeams();
	
	var team1Selected = isTeam1Selected();
	
	if (team1Selected){
		clearCurrentTeam1Selections();
		
		for (var index = 0; index < teams.length; index++){
			var team = teams[index];
			selectTeam1(team.value);
			addTeam1ToCurrentSelection(team.value);
		}
	}
}

function onClickClearTeams1(event){
	
	event.stopPropagation();
	
	var realTeams = getRealTeams();
	
	var team1Selected = isTeam1Selected();
	
	if (team1Selected){
		clearCurrentTeam1Selections();
		
		for (var index = 0; index < realTeams.length; index++){
			var team = realTeams[index];
			unselectTeam1(team.value);
			removeTeam1FromCurrentSelection(team.value);
		}
	}
}






function onClickSelectAllTeams2(event){
	
	//get the current active team (team 1 or 2)
	//and select them all
	
	event.stopPropagation();
	
	var teams = getAllTeams();
	
	var team1Selected = isTeam2Selected();
	
	if (team1Selected){
		clearCurrentTeam2Selections();
		
		for (var index = 0; index < teams.length; index++){
			var team = teams[index];
			selectTeam2(team.value);
			addTeam2ToCurrentSelection(team.value);
		}
	}
}

function onClickClearTeams2(event){
	
	event.stopPropagation();
	
	var realTeams = getRealTeams();
	
	var team2Selected = isTeam2Selected();
	
	if (team2Selected){
		clearCurrentTeam2Selections();
		
		for (var index = 0; index < realTeams.length; index++){
			var team = realTeams[index];
			unselectTeam2(team.value);
			removeTeam2FromCurrentSelection(team.value);
		}
	}
}





























function isSpecificTeamSelected(){
	
	var specificTeam1Selected = isSpecificTeam1Selected();
	
	var specificTeam2Selected = isSpecificTeam2Selected();
	
	if (specificTeam1Selected || specificTeam2Selected){
		return true;
	}
	
	return false;
}

function isSpecificTeam1Selected() {
	
	var selectedTeams1 = getSelectedTeams1();
	
	if (selectedTeams1.length > 1 || selectedTeams1.length == 0){
		return false;
	}
	
	var selectedTeam = selectedTeams1[0].value;
	
	if ('all' == selectedTeam){
		return false;
	}
	
	return true;
}

function isTeam1AllTeams(){
	
	var selectedTeams1 = getSelectedTeams1();
	
	if (isEmpty(selectedTeams1)){
		return true;
	}
	
	if (selectedTeams1.length > 1){
		return false;
	}
	
	var selectedTeam = selectedTeams1[0].value;
	
	if ('all' == selectedTeam){
		return true;
	}
	
	return false;
}

function isSpecificTeam2Selected() {
	
	var selectedTeams2 = getSelectedTeams2();
	
	if (selectedTeams2.length > 1 || selectedTeams2.length == 0){
		return false;
	}
	
	var selectedTeam = selectedTeams2[0].value;
	
	if ('all' == selectedTeam){
		return false;
	}
	
	return true;
}

function isTeam2AllTeams(){
	
	var selectedTeams2 = getSelectedTeams2();
	
	if (isEmpty(selectedTeams2)){
		return true;
	}
	
	if (selectedTeams2.length > 1){
		return false;
	}
	
	var selectedTeam = selectedTeams2[0].value;
	
	if ('all' == selectedTeam){
		return true;
	}
	
	return false;
}

function updateTeamsLink(){
	
	//this one is next
	//	ARZ, BAL @ ATL, CLE, ...
	// make elipses for each team
	// and that'll make it for the full thing
	// if only one team is selected, it should be
	//	ATL vs All
	//	All @ ATL
	
	//ATL, BUF vs ALL
	
	var selectedTeams1 = getSelectedTeams1();
	var selectedTeams2 = getSelectedTeams2();
	
	//If there aren't any selected teams, it should be "none"
//	if (isEmpty(selectedTeams1) && isEmpty(selectedTeams2)){
//		$('#teamsLink').text('No teams');
//		return;
//	}
	
	var team1AllTeams = isTeam1AllTeams();
	
	var team2AllTeams = isTeam2AllTeams();
	
	if (team1AllTeams && team2AllTeams){
		$('#teamsLink').text('All teams');
		$('#teamsLink').prop('title', 'All teams');
		return;
	}
	
	if (selectedTeams1.length == 0 && selectedTeams2.length == 0){
		$('#teamsLink').text('All teams');
		$('#teamsLink').prop('title', 'All teams');
		return;
	}
	
	sortOptionsByLabel(selectedTeams1);
	sortOptionsByLabel(selectedTeams2);
	
	var teams1LinkText = '';
	
	if (selectedTeams1.length > 0){
		for (var index = 0; index < selectedTeams1.length; index++){
			var team = selectedTeams1[index];
			
			if (index > 0){
				teams1LinkText = teams1LinkText + ', ';
			}
			
			teams1LinkText = teams1LinkText + team.label;
		}
	}
	else {
		teams1LinkText = 'All teams';
	}
	
	var fullTeams1LinkText = teams1LinkText;
	
	//we want 3 teams at most
	//most teams have 3 letters, so that would be
	//	ATL, ARZ, BUF
	//13
	if (teams1LinkText.length > 13){
		teams1LinkText = teams1LinkText.substring(0, 13) + '...';
	}
	
	var teams2LinkText = '';
	
	if (selectedTeams2.length > 0){
		for (var index = 0; index < selectedTeams2.length; index++){
			var team = selectedTeams2[index];
			
			if (index > 0){
				teams2LinkText = teams2LinkText + ', ';
			}
			
			teams2LinkText = teams2LinkText + team.label;
		}
	}
	else {
		teams2LinkText = 'All teams';
	}
	
	var fullTeams2LinkText = teams2LinkText;
	
	if (teams2LinkText.length > 13){
		teams2LinkText = teams2LinkText.substring(0, 13) + '...';
	}
	
	var conjunction = 'vs';
	
	var team1AtTeam2 = getTeam1AtTeam2();
	if (team1AtTeam2){
		conjunction = '@';
	}
	
	var linkText = teams1LinkText + ' ' + conjunction + ' ' + teams2LinkText;
	
	var fullLinkText = fullTeams1LinkText + ' ' + conjunction + ' ' + fullTeams2LinkText;
	
	$('#teamsLink').prop('title', fullLinkText);
	
	$('#teamsLink').text(linkText);
}

function showTeamsLink(){
	$('#teamsLink').show();
}

function hideTeamsLink(){
	$('#teamsLink').hide();
}

function normalizeTeamValue(value){
	var normalizedValue = normalizeString(value);
	return normalizedValue;
}

function selectTeam(value){
	var normalizedValue = normalizeTeamValue(value);
	$('#team-1-checkbox-input-' + normalizedValue).prop('checked', true);
	$('#team-2-checkbox-input-' + normalizedValue).prop('checked', true);
	$('#team-1-radio-input-' + normalizedValue).prop('checked', true);
	$('#team-2-radio-input-' + normalizedValue).prop('checked', true);
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

function getSelectedTeams1(){
	return NFL_PICKS_GLOBAL.selections.teams1;
}

function getSelectedTeams2(){
	return NFL_PICKS_GLOBAL.selections.teams2;
}

function getTeamSelectorContainerId(team){
	
	var normalizedTeamAbbreviation = normalizeTeamValue(team);
	
	var teamSelectorContainerId = 'team-selector-container-' + normalizedTeamAbbreviation;
	
	return teamSelectorContainerId;
}

function showTeamItem(team){
	
	var normalizedTeamAbbreviation = normalizeTeamValue(team);
	
	var team1SelectorContainerId = 'team-1-selector-container-' + normalizedTeamAbbreviation;
	//var teamSelectorContainerId = getTeamSelectorContainerId(team);
	$('#' + team1SelectorContainerId).show();
	
	var team2SelectorContainerId = 'team-2-selector-container-' + normalizedTeamAbbreviation;
	//var teamSelectorContainerId = getTeamSelectorContainerId(team);
	$('#' + team2SelectorContainerId).show();
}

function hideTeamItem(team){
	
	var normalizedTeamAbbreviation = normalizeTeamValue(team);
	
	var team1SelectorContainerId = 'team-1-selector-container-' + normalizedTeamAbbreviation;
	$('#' + team1SelectorContainerId).hide();
	
	var team2SelectorContainerId = 'team-2-selector-container-' + normalizedTeamAbbreviation;
	$('#' + team2SelectorContainerId).hide();
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
//	for (var index = 0; index < teamsToHide.length; index++){
//		var teamToHide = teamsToHide[index];
//		unselectTeamFull(teamToHide);
//	}
	
}



function getAllTeamsItem(){
	
	var allTeamsTeam = {label: 'All teams', value: 'all'};
	
	return allTeamsTeam;
}
