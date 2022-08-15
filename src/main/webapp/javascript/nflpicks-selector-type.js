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


/**
 * 
 * Updates the selectors so that they're good to go for when the type is picks.
 * 
 * Shows:
 * 		year, player, team, week
 * Hides:
 * 		stat name
 * 
 * Only shows or hides something if the given type isn't the previous selected type.
 * 
 * @param type
 * @returns
 */
function updatePicksSelectors(type){

	//Steps to do:
	//	1. If the previous type is the same as the given one, we don't need
	//	   to do anything to the selectors.
	//	2. Show and hide what we need to.
	//	3. Store the type we were given for next time.
	
	var previousSelectedType = getPreviousType();
	
	if (previousSelectedType == type){
		return;
	}

	showPlayersLink();
	showWeeksLink();
	showYearsLink();
	showTeamsLink();
	hideStatNameLink();
	
	setPreviousType(type);
}


/**
 * 
 * Updates the selectors so that they're right for browsing the "standings".
 * 
 * Shows:
 * 		player, year, week, team
 * Hides:
 * 		stat name
 * 
 * Only shows or hides something if the given type isn't the previous selected type.
 * 
 * @param type
 * @returns
 */
function updateStandingsSelectors(type){
	
	//Steps to do:
	//	1. If the previous type is the same as the given one, we don't need
	//	   to do anything to the selectors.
	//	2. Show and hide what we need to.
	//	3. Store the type we were given for next time.
	
	var previousSelectedType = getPreviousType();
	if (previousSelectedType == type){
		return;
	}
	
	showPlayersLink();
	showWeeksLink();
	showYearsLink();
	showTeamsLink();
	hideStatNameLink();
	
	setPreviousType(type);
}

/**
 * 
 * Updates the selectors so that they're right for browsing the "standings".
 * 
 * Shows:
 * 		player, year, week, team
 * Hides:
 * 		stat name
 * 
 * Only shows or hides something if the given type isn't the previous selected type.
 * 
 * @param type
 * @returns
 */
function updateDivisionStandingsSelectors(type){
	
	//Steps to do:
	//	1. If the previous type is the same as the given one, we don't need
	//	   to do anything to the selectors.
	//	2. Show and hide what we need to.
	//	3. Store the type we were given for next time.
	
	var previousSelectedType = getPreviousType();
	if (previousSelectedType == type){
		return;
	}
	
	showPlayersLink();
	showWeeksLink();
	showYearsLink();
	showTeamsLink();
	hideStatNameLink();
	
	setPreviousType(type);
}

/**
 * 
 * Updates the selectors so that they're good to go for browsing the
 * "stats"
 * 
 * Shows:
 * 		stat name, others depending on the stat name
 * Hides:
 * 		depends on the stat name
 * 
 * Stat name:
 * 		champions
 * 			shows: Nothing
 * 			hides: player, year, week, team
 * 		championship standings
 * 			shows: Nothing
 * 			hides: player, year, week, team
 * 		division titles
 * 			shows: Nothing
 * 			hides: player, year, week, team
 * 		division title standings
 * 			shows: Nothing
 * 			hides: player, year, week, team
 * 		week standings
 * 			shows: player, year, week
 * 			hides: team
 * 		weeks won standings
 * 			shows: year
 * 			hides: player, team, week
 * 		weeks won by week
 * 			shows: year, week
 * 			hides: team
 * 		week records by player
 * 			shows: year, week, player
 * 			hides: team
 * 		pick accuracy
 * 			shows: year, player, team
 * 			hides: week
 * 		pick splits:
 * 			shows: year, week, team
 * 			hides: player
 * 		collective record summary:
 * 			shows: player, year, week, team
 * 			hides: nothing
 * 		collective pick accuracy:
 * 			shows: player, year, week, team
 * 			hides: nothing
 * 
 * @param type
 * @returns
 */
function updateStatsSelectors(type){
	
	//Steps to do:
	//	1. We always want to show the stat name container.
	//	2. Get the name of the stat we want to show.
	//	3. Show and hide what we need to based on the kind of stat we want to show.
	//	4. Store the type we were given for next time.
	
	showStatNameLink();
	
	var statName = getSelectedStatName();
	
	if ('champions' == statName){
		showPlayersLink();
		showYearsLink();
		hideWeeksLink();
		hideTeamsLink();
	}
	else if ('championshipStandings' == statName){
		showPlayersLink();
		showYearsLink();
		hideWeeksLink();
		hideTeamsLink();
	}
	else if ('divisionTitles' == statName){
		showPlayersLink();
		showYearsLink();
		hideWeeksLink();
		hideTeamsLink();
	}
	else if ('divisionTitleStandings' == statName){
		showPlayersLink();
		showYearsLink();
		hideWeeksLink();
		hideTeamsLink();
	}
	else if ('seasonStandings' == statName){
		showPlayersLink();
		showYearsLink();
		showWeeksLink();
		hideTeamsLink();
	}
	else if ('weekStandings' == statName){
		showPlayersLink();
		showYearsLink();
		showWeeksLink();
		hideTeamsLink();
	}
	else if ('weeksWonStandings' == statName){
		showPlayersLink();
		showYearsLink();
		showWeeksLink();
		hideTeamsLink();
	}
	else if ('weeksWonByWeek' == statName){
		showPlayersLink();
		showYearsLink();
		showWeeksLink();
		hideTeamsLink();
	}
	else if ('weekRecordsByPlayer' == statName){
		showPlayersLink();
		showYearsLink();
		showWeeksLink();
		hideTeamsLink();
	}
	else if ('pickAccuracy' == statName){
		showPlayersLink();
		showYearsLink();
		showWeeksLink();
		showTeamsLink();
	}
	else if ('pickSplits' == statName){
		showPlayersLink();
		showYearsLink();
		showWeeksLink();
		showTeamsLink();
	}
	else if ('weekComparison' == statName){
		showPlayersLink();
		showYearsLink();
		showWeeksLink();
		hideTeamsLink();
	}
	else if ('seasonProgression' == statName){
		showPlayersLink();
		showYearsLink();
		showWeeksLink();
		hideTeamsLink();
	}
	else if ('collectiveRecordSummary' == statName){
		showPlayersLink();
		showYearsLink();
		showWeeksLink();
		showTeamsLink();
	}
	else if ('collectivePickAccuracy' == statName){
		showPlayersLink();
		showYearsLink();
		showWeeksLink();
		showTeamsLink();
	}
	setPreviousType(type);
}

/**
 * 
 * Gets the selected value for the type.
 * 
 * @returns
 */
function getSelectedType(){
	return $('input[name=type]:checked').val();
}

/**
 * 
 * Sets the selected value for the type to the given type.  Only does it
 * if the type select input has the given type as an option.
 * 
 * @param type
 * @returns
 */
function setSelectedType(type){
	$('input[name=type]').val([type]);
	NFL_PICKS_GLOBAL.selections.type = type;
}

/**
 * 
 * Gets the previous type that was selected.  This is so we can decide
 * whether to update stuff or not when the type changes.
 * 
 * @returns
 */
function getPreviousType(){
	return NFL_PICKS_GLOBAL.previousType;
}

/**
 * 
 * Sets the previous type in the NFL_PICKS_GLOBAL variable.  This is so we can decide
 * whether to update stuff or not when the type changes.
 * 
 * @param newPreviousType
 * @returns
 */
function setPreviousType(newPreviousType){
	NFL_PICKS_GLOBAL.previousType = newPreviousType;
}