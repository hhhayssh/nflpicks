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
	
	sortOptionsByLabel(selectedPlayers);
	
	var linkText = '';
	
	if (selectedPlayers.length > 0){
		for (var index = 0; index < selectedPlayers.length; index++){
			var player = selectedPlayers[index];
			
			if (index > 0){
				linkText = linkText + ', ';
			}
			
			linkText = linkText + player.label;
		}
	}
	else {
		linkText = 'Everybody';
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



/**
 * 
 * A convenience function for checking whether a single player is selected or not.
 * It'll return false if the current selected player is "all" or if it has multiple
 * values.  Otherwise, it'll return true.
 * 
 * @returns
 */
function isASinglePlayerSelected(){
	
	var selectedPlayer = getSelectedPlayer();
	
	if ('all' == selectedPlayer || doesValueHaveMultipleValues(selectedPlayer)){
		return false;
	}
	
	return true;
}

/**
 * 
 * This function will say whether a specific player is selected.  If the
 * current selected player is "all", it'll say there isn't.  Otherwise, it'll
 * say there is.
 * 
 * @returns
 */
function isSpecificPlayerSelected(){
	
	var selectedPlayers = getSelectedPlayers();
	
	if (selectedPlayers.length > 1){
		return false;
	}
	
	var selectedPlayer = selectedPlayers[0].value;
	
	if ('all' == selectedPlayer){
		return false;
	}
	
	return true;
}


function showPlayerContainer(){
	$('#playerContainer').show();
}

function hidePlayerContainer(){
	$('#playerContainer').hide();
}


/**
 * 
 * This function will set the given players as being selected in the NFL_PICKS_GLOBAL
 * variable (NFL_PICKS_GLOBAL.selections.players) and it'll call the "selectPlayer"
 * function in the selectors file for each player so they get "selected" on the UI too.
 * 
 * It expects the given players variable to either be...
 * 	1. An array of player names.
 * 	2. A comma separated string of player names.
 * 	3. A single player name.
 * 
 * It will put the actual player objects into the NFL_PICKS_GLOBAL variable for
 * each player name that's given.
 * 
 * @param players
 * @returns
 */
function setSelectedPlayers(players){
	
	//Steps to do:
	//	1. Check whether the players variable is an array.
	//	2. If it is, just keep it.
	//	3. Otherwise, it's a string so check to see if it has multiple values.
	//	4. If it does, then turn it into an array.
	//	5. Otherwise, just put it in there as a single value.
	//	6. Go through each player in the array, get the actual object for the player name
	//	   and put it in the global variable.  And, "select" them in the ui.
	
	var playerValuesArray = [];
	
	var isArray = Array.isArray(players);
	
	if (isArray){
		playerValuesArray = players;
	}
	else {
		var hasMultipleValues = doesValueHaveMultipleValues(players);
		
		if (hasMultipleValues){
			playerValuesArray = delimitedValueToArray(players);
		}
		else {
			playerValuesArray.push(players);
		}
	}
	
	var playersArray = [];
	
	for (var index = 0; index < playerValuesArray.length; index++){
		var value = playerValuesArray[index];
		selectPlayer(value);

		var player = getPlayer(value);
		playersArray.push(player);
	}
	
	NFL_PICKS_GLOBAL.selections.players = playersArray;
}