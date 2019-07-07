function onClickTypeSelector(event){
	event.stopPropagation();
	var wasSelectorVisible = isVisible('typeSelectorContainer'); 
	hideSelectorContainers();
	if (!wasSelectorVisible){
		showTypeSelector();
	}
}

function showTypeSelector(){
	$('#typeSelectorContainer').show();
}

function hideTypeSelector(){
	$('#typeSelectorContainer').hide();
}

function onClickType(event, value){
	event.stopPropagation();
	setSelectedType(value);
	hideTypeSelector();
	NFL_PICKS_GLOBAL.selections.type = getSelectedType();
	updateTypeLink();
	updateView();
}

function getType(value){
	for (var index = 0; index < NFL_PICKS_GLOBAL.types.length; index++){
		var type = NFL_PICKS_GLOBAL.types[index];
		if (type.value == value){
			return type;
		}
	}
	return null;
}

function updateTypeLink(){
	var selectedType = getSelectedType();
	var type = getType(selectedType);
	if (type != null){
		$('#typesLink').text(type.label);
	}
}

function showTypeLink(){
	$('#typesLink').show();
}

function hideTypeLink(){
	$('#typesLink').hide();
}





function onClickPlayerSelector(event){
	event.stopPropagation();
	
	var wasSelectorVisible = isVisible('playerSelectorContainer'); 
	
	hideSelectorContainers();

	if (!wasSelectorVisible){
		resetPlayerSelections();
		showPlayerSelector();
	}
}

function onClickMultiselectPlayerContainer(event){
	event.stopPropagation();
	
	var multiselectPlayer = getMultiselectPlayer();
	
	if (multiselectPlayer){
		setMultiselectPlayerValue(false);
	}
	else {
		setMultiselectPlayerValue(true);
	}
	
	onClickMultiselectPlayer(event);
}

function onClickMultiselectPlayer(event){
	event.stopPropagation();
	
	var multiselectPlayerChecked = $('#multiselectPlayer').prop('checked');
	
	setMultiselectPlayer(multiselectPlayerChecked);
	
	if (multiselectPlayerChecked){
		showMultiselectPlayerContainer();
		showPlayerCheckboxes();
		hideAllPlayerSelectorContainer();
		hidePlayerRadioButtons();
		showPlayerSelectorFooterContainer();
	}
	else {
		hideMultiselectPlayerContainer();
		showAllPlayerSelectorContainer();
		showPlayerRadioButtons();
		hidePlayerCheckboxes();
		hidePlayerSelectorFooterContainer();
	}
}

function setMultiselectPlayerValue(value){
	if (value){
		$('#multiselectPlayer').prop('checked', true);
	}
	else {
		$('#multiselectPlayer').prop('checked', false);
	}
}

function showAllPlayerSelectorContainer(){
	$('#player-selector-container-all').show();
}

function hideAllPlayerSelectorContainer(){
	$('#player-selector-container-all').hide();
}

function showMultiselectPlayerContainer(){
	$('#multiselectPlayerContainer').show();
}

function hideMultiselectPlayerContainer(){
	$('#multiselectPlayerContainer').hide();
}

function showPlayerSelectorFooterContainer(){
	$('#player-selector-footer-container').show();
}

function hidePlayerSelectorFooterContainer(){
	$('#player-selector-footer-container').hide();
}

//player-checkbox-input-
function showPlayerCheckboxes(){
	var playerValues = getAllPlayerValues();
	
	for (var index = 0; index < playerValues.length; index++){
		var playerValue = playerValues[index];
		showPlayerCheckbox(playerValue);
	}
}

function showPlayerCheckbox(playerValue){
	var normalizedValue = normalizePlayerValue(playerValue);
	$('#player-checkbox-input-' + normalizedValue).show();
}

function hidePlayerCheckboxes(){
	
	var playerValues = getAllPlayerValues();
	
	for (var index = 0; index < playerValues.length; index++){
		var playerValue = playerValues[index];
		hidePlayerCheckbox(playerValue);
	}
}

function hidePlayerCheckbox(playerValue){
	var normalizedValue = normalizePlayerValue(playerValue);
	$('#player-checkbox-input-' + normalizedValue).hide();
}

//player-radio-input-
function showPlayerRadioButtons(){
	var playerValues = getAllPlayerValues();
	
	for (var index = 0; index < playerValues.length; index++){
		var playerValue = playerValues[index];
		showPlayerRadioButton(playerValue);
	}
}

function showPlayerRadioButton(playerValue){
	var normalizedValue = normalizePlayerValue(playerValue);
	$('#player-radio-input-' + normalizedValue).show();
}

function hidePlayerRadioButtons(){
	
	var playerValues = getAllPlayerValues();
	
	for (var index = 0; index < playerValues.length; index++){
		var playerValue = playerValues[index];
		hidePlayerRadioButton(playerValue);
	}
}

function hidePlayerRadioButton(playerValue){
	var normalizedValue = normalizePlayerValue(playerValue);
	$('#player-radio-input-' + normalizedValue).hide();
}


function setMultiselectPlayer(value){
	NFL_PICKS_GLOBAL.multiselectPlayer = value;
}

function getMultiselectPlayer(){
	return NFL_PICKS_GLOBAL.multiselectPlayer;
}

function onClickPlayerSelectionOk(event){
	event.stopPropagation();
	//If it's multi select here, unselect the all option.
	var multiselectPlayer = getMultiselectPlayer();
	if (multiselectPlayer){
		removePlayerFromCurrentSelection('all');
	}
	hidePlayerSelector();
	setSelectedPlayers(currentPlayerSelections);
	updatePlayersLink();
	updateView();
}

function onClickPlayerSelectionCancel(event){
	event.stopPropagation();
	resetAndHidePlayerSelections();
}

function resetPlayerSelections(){
	unselectAllPlayersByValue();
	currentPlayerSelections = getSelectedPlayerValues();
	selectPlayersByValue(currentPlayerSelections);
}

function resetAndHidePlayerSelections(){
	resetPlayerSelections();
	hidePlayerSelector();
}

function showPlayerSelector(){
	$('#playerSelectorContainer').show();
}

function hidePlayerSelector(){
	$('#playerSelectorContainer').hide();
}

var currentPlayerSelections = [];


/*
 '<div style="display: inline-block; width: 48%; text-align: left;"><a href="javascript:void(0);" onClick="onClickClearPlayers(event);">Clear</a></div>' +
						   					'<div style="display: inline-block; width: 48%; text-align: right;"><a href="javascript:void(0);" onClick="onClickSelectAllPlayers(event);>Select all</a></div>' +
 */

function onClickSelectAllPlayers(event){
	
	event.stopPropagation();
	
	var players = getAllPlayers();
	
	currentPlayerSelections = [];
	
	for (var index = 0; index < players.length; index++){
		var player = players[index];
		selectPlayer(player.value)
		addPlayerToCurrentSelection(player.value);
	}
}

function onClickClearPlayers(event){
	
	event.stopPropagation();
	
	var realPlayers = getRealPlayers();
	
	currentPlayerSelections = [];
	
	for (var index = 0; index < realPlayers.length; index++){
		var player = realPlayers[index];
		unselectPlayer(player.value);
		removePlayerFromCurrentSelection(player.value);
	}
}

function onClickPlayer(event, value){
	event.stopPropagation();
	
	var multiselectPlayer = getMultiselectPlayer();
	
	if (multiselectPlayer){
		var indexOfValue = currentPlayerSelections.indexOf(value);
		if (indexOfValue >= 0){
			unselectPlayer(value);
			removePlayerFromCurrentSelection(value);
		}
		else {
			selectPlayer(value);
			addPlayerToCurrentSelection(value);
		}
	}
	else {
		currentPlayerSelections = [];
		selectPlayer(value);
		addPlayerToCurrentSelection(value);
		onClickPlayerSelectionOk(event);
	}
}

function updatePlayersLink(){
	
	var selectedPlayers = getSelectedPlayers();
	
	//If there aren't any selected players, it should be "none"
	if (isEmpty(selectedPlayers)){
		$('#playersLink').text('Nobody');
		return;
	}
	
	sortOptionsByLabel(selectedPlayers);
	
	var linkText = '';
	
	for (var index = 0; index < selectedPlayers.length; index++){
		var player = selectedPlayers[index];
		
		if (index > 0){
			linkText = linkText + ', ';
		}
		
		linkText = linkText + player.label;
	}
	
	$('#playersLink').prop('title', linkText);
	
	if (linkText.length >= 25){
		linkText = linkText.substring(0, 25) + '...';
	}
	
	$('#playersLink').text(linkText);
}

function showPlayersLink(){
	$('#playersLink').show();
}

function hidePlayersLink(){
	$('#playersLink').hide();
}

function selectAllPlayersByValue(){
	var playerValues = getAllPlayerValues();
	selectPlayersByValue(playerValues);
}

function unselectAllPlayersByValue(){
	var playerValues = getAllPlayerValues();
	unselectPlayersByValue(playerValues);
}

function selectPlayersByValue(values){
	for (var index = 0; index < values.length; index++){
		var value = values[index];
		selectPlayer(value);
	}
}

function normalizePlayerValue(value){
	var normalizedValue = normalizeString(value);
	return normalizedValue;
}

function selectPlayer(value){
	var normalizedValue = normalizePlayerValue(value);
	$('#player-checkbox-input-' + normalizedValue).prop('checked', true);
	$('#player-radio-input-' + normalizedValue).prop('checked', true);
}

function addPlayerToCurrentSelection(value){
	currentPlayerSelections.push(value);
}

function unselectPlayersByValue(players){
	for (var index = 0; index < players.length; index++){
		var player = players[index];
		unselectPlayer(player);
	}
}

function unselectPlayer(player){
	var normalizedValue = normalizePlayerValue(player);
	$('#player-checkbox-input-' + normalizedValue).prop('checked', false);
	$('#player-radio-input-' + normalizedValue).prop('checked', false);
}

function removePlayerFromCurrentSelection(value){
	var indexOfValue = currentPlayerSelections.indexOf(value);
	if (indexOfValue >= 0){
		currentPlayerSelections.splice(indexOfValue, 1);
	}
}

function getAllPlayers(){
	return NFL_PICKS_GLOBAL.players;
}

function getRealPlayers(){
	return NFL_PICKS_GLOBAL.realPlayers;
}

function getAllPlayerValues(){
	var playerValues = [];
	
	for (var index = 0; index < NFL_PICKS_GLOBAL.players.length; index++){
		var player = NFL_PICKS_GLOBAL.players[index];
		playerValues.push(player.value);
	}
	
	return playerValues;
}

function getPlayers(values){
	
	var players = [];
	
	for (var index = 0; index < values.length; index++){
		var value = values[index];
		var player = getPlayer(value);
		if (player != null){
			players.push(player);
		}
	}
	
	return players;
}

function getPlayer(value){
	
	for (var index = 0; index < NFL_PICKS_GLOBAL.players.length; index++){
		var player = NFL_PICKS_GLOBAL.players[index];
		if (value == player.value){
			return player;
		}
	}
	
	return null;
}

function getSelectedPlayerValues(){
	
	var playerValues = [];
	
	var selectedPlayers = getSelectedPlayers();
	
	for (var index = 0; index < selectedPlayers.length; index++){
		var selectedPlayer = selectedPlayers[index];
		playerValues.push(selectedPlayer.value);
	}
	
	return playerValues;
}

function getPlayerValuesForRequest(){
	
	var selectedValues = getSelectedPlayerValues();
	
	var valuesToSend = [];
	
	var realPlayers = getRealPlayers();
	
	for (var index = 0; index < selectedValues.length; index++){
		var selectedValue = selectedValues[index];
		
		if ('all' == selectedValue){
			for (var index2 = 0; index2 < realPlayers.length; index2++){
				var realPlayer = realPlayers[index2];
				valuesToSend.push(realPlayer.value);
			}
		}
		else {
			valuesToSend.push(selectedValue);
		}
	}
	
	var uniqueValues = getUniqueValuesFromArray(valuesToSend);
	
	var playerValuesForRequest = arrayToDelimitedValue(uniqueValues);
	
	return playerValuesForRequest;
}

function getSelectedPlayers(){
	return NFL_PICKS_GLOBAL.selections.players;
}











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
	setSelectedTeams(currentTeamSelections);
	updateTeamsLink();
	updateView();
}

function onClickTeamSelectionCancel(event){
	event.stopPropagation();
	resetAndHideTeamSelections();
}

function resetTeamSelections(){
	unselectAllTeamsByValue();
	currentTeamSelections = getSelectedTeamValues();
	selectTeamsByValue(currentTeamSelections);
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


/*
 '<div style="display: inline-block; width: 48%; text-align: left;"><a href="javascript:void(0);" onClick="onClickClearTeams(event);">Clear</a></div>' +
						   					'<div style="display: inline-block; width: 48%; text-align: right;"><a href="javascript:void(0);" onClick="onClickSelectAllTeams(event);>Select all</a></div>' +
 */

function onClickSelectAllTeams(event){
	
	event.stopPropagation();
	
	var teams = getAllTeams();
	
	currentTeamSelections = [];
	
	for (var index = 0; index < teams.length; index++){
		var team = teams[index];
		selectTeam(team.value);
		addTeamToCurrentSelection(team.value);
	}
}

function onClickClearTeams(event){
	
	event.stopPropagation();
	
	var realTeams = getRealTeams();
	
	currentTeamSelections = [];
	
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
		currentTeamSelections = [];
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









function onClickStatNameSelector(event){
	event.stopPropagation();
	var wasSelectorVisible = isVisible('statNameSelectorContainer'); 
	hideSelectorContainers();
	if (!wasSelectorVisible){
		showStatNameSelector();
	}
}

function showStatNameSelector(){
	$('#statNameSelectorContainer').show();
}

function hideStatNameSelector(){
	$('#statNameSelectorContainer').hide();
}

function onClickStatName(event, value){
	event.stopPropagation();
	setSelectedStatName(value);
	hideStatNameSelector();
	NFL_PICKS_GLOBAL.selections.statName = getSelectedStatName();
	updateStatNameLink();
	updateView();
}

function getStatName(value){
	for (var index = 0; index < NFL_PICKS_GLOBAL.statNames.length; index++){
		var statName = NFL_PICKS_GLOBAL.statNames[index];
		if (statName.value == value){
			return statName;
		}
	}
	return null;
}

function updateStatNameLink(){
	var selectedStatName = getSelectedStatName();
	var statName = getStatName(selectedStatName);
	if (statName != null){
		$('#statNamesLink').text(statName.label);
	}
}

function showStatNameLink(){
	$('#statNamesLink').show();
}

function hideStatNameLink(){
	$('#statNamesLink').hide();
}









function setMultiselectWeek(value){
	NFL_PICKS_GLOBAL.multiselectWeek = value;
}

function getMultiselectWeek(){
	return NFL_PICKS_GLOBAL.multiselectWeek;
}

function setMultiselectYear(value){
	NFL_PICKS_GLOBAL.multiselectYear = value;
}

function getMultiselectYear(){
	return NFL_PICKS_GLOBAL.multiselectYear;
}





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
	
	//If there aren't any selected years, it should be "none"
	if (isEmpty(selectedYears)){
		$('#yearsLink').text('No years');
		return;
	}
	
	if (selectedYears.length == 1 && 'all' == selectedYears[0].value){
		$('#yearsLink').text('All years');
		return;
	}
	
	sortOptionsByNumericValue(selectedYears);
	
	var linkText = '';
	
	for (var index = 0; index < selectedYears.length; index++){
		var year = selectedYears[index];
		
		if (index > 0){
			linkText = linkText + ', ';
		}
		
		linkText = linkText + year.label;
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
	
	var realYears = getRealYears();
	
	/*
	 var yearOptions = [{label: 'All', value: 'all'},
		                   {label: 'Jurassic Period (2010-2015)', value: 'old'},
		                   {label: 'First year (2016)', value: 'half-modern'},
		                   {label: 'Modern Era (2017 - now)', value: 'modern'}];
	 */
	
	for (var index = 0; index < selectedValues.length; index++){
		var selectedValue = selectedValues[index];
		
		if ('all' == selectedValue){
			for (var index2 = 0; index2 < realYears.length; index2++){
				var realYear = realYears[index2];
				valuesToSend.push(realYear.value);
			}
		}
		else if ('old' == selectedValue){
			valuesToSend = valuesToSend.concat(['2010', '2011', '2012', '2013', '2014', '2015']);
		}
		else if ('half-modern' == selectedValue){
			valuesToSend.push('2016');
		}
		else if ('modern' == selectedValue){
			for (var index2 = 0; index2 < realYears.length; index2++){
				var realYear = realYears[index2];
				
				var realYearValue = parseInt(realYear.value);
				
				if (realYearValue >= 2017){
					valuesToSend.push(realYear.value);
				}
			}
		}
		else {
			valuesToSend.push(selectedValue);
		}
	}
	
	var uniqueValues = getUniqueValuesFromArray(valuesToSend);
	
	var yearValuesForRequest = arrayToDelimitedValue(uniqueValues);
	
	return yearValuesForRequest;
}

function getSelectedYears(){
	return NFL_PICKS_GLOBAL.selections.years;
}






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
	setSelectedWeeks(currentWeekSelections);
	updateWeeksLink();
	updateView();
}

function onClickWeekSelectionCancel(event){
	event.stopPropagation();
	resetAndHideWeekSelections();
}

function resetWeekSelections(){
	unselectAllWeeksByValue();
	currentWeekSelections = getSelectedWeekValues();
	selectWeeksByValue(currentWeekSelections);
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


/*
 '<div style="display: inline-block; width: 48%; text-align: left;"><a href="javascript:void(0);" onClick="onClickClearWeeks(event);">Clear</a></div>' +
						   					'<div style="display: inline-block; width: 48%; text-align: right;"><a href="javascript:void(0);" onClick="onClickSelectAllWeeks(event);>Select all</a></div>' +
 */

function onClickSelectAllWeeks(event){
	
	event.stopPropagation();
	
	var weeks = getAllWeeks();
	
	currentWeekSelections = [];
	
	for (var index = 0; index < weeks.length; index++){
		var week = weeks[index];
		selectWeek(week.value);
		addWeekToCurrentSelection(week.value);
	}
}

function onClickClearWeeks(event){
	
	event.stopPropagation();
	
	var weeks = getAllWeeks();
	
	currentWeekSelections = [];
	
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
		currentWeekSelections = [];
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

function getWeekValuesForRequest(){
	
	var selectedValues = getSelectedWeekValues();
	
	var valuesToSend = [];
	
	var realWeeks = getRealWeeks();
	
	for (var index = 0; index < selectedValues.length; index++){
		var selectedValue = selectedValues[index];
		
		if ('all' == selectedValue){
			for (var index2 = 0; index2 < realWeeks.length; index2++){
				var realWeek = realWeeks[index2];
				valuesToSend.push(realWeek.value);
			}
		}
		else if ('regular-season' == selectedValue){
			valuesToSend = valuesToSend.concat(['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17']);
		}
		else if ('playoffs' == selectedValue){
			valuesToSend = valuesToSend.concat(['18', '19', '20', '21']);
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
