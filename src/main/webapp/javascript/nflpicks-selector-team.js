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
	
	//selected-team-selector
	$('#team2SelectorContainer').removeClass('selected-team-selector');
	$('#team1SelectorContainer').addClass('selected-team-selector');
	
	$('#team2SelectorRadioButton').prop('checked', false);
	$('#team1SelectorRadioButton').prop('checked', true);
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
	
	$('#team1SelectorContainer').removeClass('selected-team-selector');
	$('#team2SelectorContainer').addClass('selected-team-selector');
	
	$('#team1SelectorRadioButton').prop('checked', false);
	$('#team2SelectorRadioButton').prop('checked', true);
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
	
	var team1AtTeam2Checked = $('#team1AtTeam2').prop('checked');
	
	setTeam1AtTeam2(team1AtTeam2Checked);
	
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

function setTeam1AtTeam2(value){
	NFL_PICKS_GLOBAL.team1AtTeam2 = value;
}

function getTeam1AtTeam2(){
	return NFL_PICKS_GLOBAL.team1AtTeam2;
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
	
	var team1AtTeam2 = getTeam1AtTeam2();
	
	if (team1AtTeam2){
		setTeam1AtTeam2(false);
	}
	else {
		setTeam1AtTeam2(true);
	}
}

function updateTeamVsOrAtLinkLabel(){
	
	var team1AtTeam2 = getTeam1AtTeam2();
	
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
	
	var multiselectTeam = getMultiselectTeam();
	
	if (multiselectTeam){
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
		onClickTeam1SelectionOk(event);
	}
}


function onClickTeam2(event, value){
	event.stopPropagation();
	
	var multiselectTeam = getMultiselectTeam();
	
	if (multiselectTeam){
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
		onClickTeam2SelectionOk(event);
	}
}


var currentTeam1Selections = [];

function getCurrentTeam1Selections(){
	return currentTeam1Selections;
}

function setCurrentTeam1Selections(updatedSelections){
	currentTeam1Selections = updatedSelections;
}

function clearCurrentTeam1Selections(){
	currentTeam1Selections = [];
}

function selectTeam1(value){
	var normalizedValue = normalizeTeamValue(value);
	$('#team-1-checkbox-input-' + normalizedValue).prop('checked', true);
	$('#team-1-radio-input-' + normalizedValue).prop('checked', true);
}

function unselectTeam1(team){
	var normalizedValue = normalizeTeamValue(team);
	$('#team-1-checkbox-input-' + normalizedValue).prop('checked', false);
	$('#team-1-radio-input-' + normalizedValue).prop('checked', false);
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
	currentTeam1Selections.push(value);
}

function onClickTeam1SelectionOk(event){
	event.stopPropagation();
	//If it's multi select here, unselect the all option.
	var multiselectTeam = getMultiselectTeam();
	if (multiselectTeam){
		removeTeam1FromCurrentSelection('all');
		removeTeam2FromCurrentSelection('all');
	}
	
	hideTeamSelector();
	var currentTeams1 = getCurrentTeam1Selections();
	setSelectedTeams1(currentTeams1);
	
	var currentTeams2 = getCurrentTeam2Selections();
	setSelectedTeams2(currentTeams2);
	
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

var currentTeam2Selections = [];

function getCurrentTeam2Selections(){
	return currentTeam2Selections;
}

function setCurrentTeam2Selections(updatedSelections){
	currentTeam2Selections = updatedSelections;
}

function clearCurrentTeam2Selections(){
	currentTeam2Selections = [];
}

function selectTeam2(value){
	var normalizedValue = normalizeTeamValue(value);
	$('#team-2-checkbox-input-' + normalizedValue).prop('checked', true);
	$('#team-2-radio-input-' + normalizedValue).prop('checked', true);
}

function unselectTeam2(team){
	var normalizedValue = normalizeTeamValue(team);
	$('#team-2-checkbox-input-' + normalizedValue).prop('checked', false);
	$('#team-2-radio-input-' + normalizedValue).prop('checked', false);
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
	currentTeam2Selections.push(value);
}

function onClickTeam2SelectionOk(event){
	event.stopPropagation();
	//If it's multi select here, unselect the all option.
	var multiselectTeam = getMultiselectTeam();
	if (multiselectTeam){
		removeTeam2FromCurrentSelection('all');
	}
	hideTeamSelector();
	var currentTeams = getCurrentTeam2Selections();
	setSelectedTeams2(currentTeams);
	updateTeamsLink();
	updateView();
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


//this needs to reset team 1 and 2 selections
function resetTeam1Selections(){
	unselectAllTeams1ByValue();
	var selectedTeamValues = getSelectedTeam1Values();
	setCurrentTeam1Selections(selectedTeamValues);
	var currentTeams = getCurrentTeam1Selections();
	selectTeams1ByValue(currentTeams);
}

function unselectAllTeams1ByValue(){
	var teamValues = getAllTeamValues();
	unselectTeams1ByValue(teamValues);
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

function onClickTeamSelector(event){
	event.stopPropagation();
	
	var wasSelectorVisible = isVisible('teamSelectorContainer'); 
	
	hideSelectorContainers();

	if (!wasSelectorVisible){
		resetTeam1Selections();
		resetTeam2Selections();
		showTeamSelector();
		selectTeam1Container();
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
		hideAllTeam1SelectorContainer();
		hideAllTeam2SelectorContainer();
		hideTeamRadioButtons();
	}
	else {
		hideMultiselectTeamContainer();
		showAllTeamSelectorContainer();
		showAllTeam1SelectorContainer();
		showAllTeam2SelectorContainer();
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
		removeTeam1FromCurrentSelection('all');
		removeTeam2FromCurrentSelection('all');
	}
	
	hideTeamSelector();
	var currentTeams1 = getCurrentTeam1Selections();
	setSelectedTeams1(currentTeams1);
	
	var currentTeams2 = getCurrentTeam2Selections();
	setSelectedTeams2(currentTeams2);
	
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
	resetTeam1Selections();
	resetTeam2Selections();
	hideTeamSelector();
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
