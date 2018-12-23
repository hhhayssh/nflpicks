
//next up...

//	1. refactoring the grid rendering
//	2. commenting this file and organizing it...

/**
 * 
 * This is the "container" for global variables.  I made it so we won't have to worry
 * about "conflicts" with local variable names.
 * 
 */
var NFL_PICKS_GLOBAL = {
	
	/**
	 * The previous type they picked.  This is so we can decide how much of the view we need
	 * to "refresh" when we update it.
	 */
	previousType: null,
	
	/**
	 * Switches that say whether these pick grids have been shown.  If they haven't, we want
	 * to make sure we don't show the picks for all years and weeks (unless they specifically asked
	 * for that).
	 * We don't want to do that because that's a lot of info to show.  So, these are here basically
	 * so we can "smartly" default the year and week selections for the picks and pick splits grids.
	 */
	havePicksBeenShown: false,
	havePickSplitsBeenShown: false,
	
	/**
	 * Whether we should push the previous parameters onto the backward navigation stack.
	 */
	pushPreviousParameters: true,
	/**
	 * The previous parameters that were used to show the view.  This is so they can go back
	 * and forth pretty easily.
	 */
	previousParameters: null,
	
	/**
	 * The stacks for navigating forward and backward.  They hold the parameters that were shown for the "view".
	 * When they change the view, we put the previous parameters on the backward stack and when they navigate backward,
	 * we pop those parameters off to change the view and put the previous ones on the forward stack.
	 */
	navigationForwardStack: [],
	navigationBackwardStack: [],
	
	/**
	 * So we can get the current year and week number which come in handy.
	 */
	currentYear: null,
	currentWeekNumber: null,
	
	/**
	 * So we can show the games for the current week.
	 */
	gamesForCurrentWeek: null,
	
	/**
	 * For holding the initial selections for when the page first shows up.  We set some of these
	 * variables with values from the server (like the year) and others (like the type) to constants.
	 */
	initialType: null,
	initialYear: null,
	initialWeek: null,
	initialPlayer: null,
	initialTeam: null,
	initialStatName: null
};

//these all need to be moved into the global variable...
var previousType = null;

var havePicksBeenShown = false;

var pickSplitsBeenShown = false;

var navigationForwardStack = [];
var navigationBackwardStack = [];

/**
 * When the document's been loaded on the browser, we want to:
 * 
 * 		1. Go to the server and get the selection criteria (teams, players, initial values).
 * 		2. Initialize the UI based on those values. 
 */
$(document).ready(
	function(){
		getSelectionCriteriaAndInitialize();
});

/**
 * 
 * This function will initialize the view.  It assumes all the stuff from the server
 * that's needed to initialize is setup.
 * 
 * @returns
 */
function initializeView(){
	
	//Steps to do:
	//	1. Set the initial selections for the type, year, week, ...
	//	2. Update the view based on those selections.
	
	initializeSelections();
	updateView();
}

/**
 * 
 * Sets the initial selections for the type, year, week...
 * They're all set like this:
 * 
 * 		1. The initial value comes from NFL_PICKS_GLOBAL.
 * 		2. If there's a url parameter for the selection, it's used instead.
 * 
 * This way, we...
 * 
 * 		1. Set the initial values when loading the data from the server (for stuff like
 * 		   week and year).
 * 		2. Allow the overriding of the values by url parameters.
 * 
 * Number 2 makes it so people can send direct urls and get the view they want the first
 * time the page shows up.
 * 
 * @returns
 */
function initializeSelections(){

	//Steps to do:
	//	1. Get the parameters that were sent in the url.
	//	2. Initialize each selection with its global initial value.
	//	3. If there's a value for it in the url, use that instead so the url
	//	   overrides what we assume initially.
	
	var parameters = getUrlParameters();
	
	var type = NFL_PICKS_GLOBAL.initialType;
	if (isDefined(parameters) && isDefined(parameters.type)){
		type = parameters.type;
	}
	setSelectedType(type);
	
	var year = NFL_PICKS_GLOBAL.initialYear;
	if (isDefined(parameters) && isDefined(parameters.year)){
		year = parameters.year;
	}
	setSelectedYear(year);
	
	var week = NFL_PICKS_GLOBAL.initialWeek;
	if (isDefined(parameters) && isDefined(parameters.week)){
		week = parameters.week;
	}
	setSelectedWeek(week);
	
	var player = NFL_PICKS_GLOBAL.initialPlayer;
	if (isDefined(parameters) && isDefined(parameters.player)){
		player = parameters.player;
		//if the player has a comma in it, show the options and select multiplayer
		if (player.indexOf(',') != -1){
			showOptions();
		}
	}
	setSelectedPlayer(player);
	
	var statName = NFL_PICKS_GLOBAL.initialStatName;
	if (isDefined(parameters) && isDefined(parameters.statName)){
		statName = parameters.statName;
	}
	setSelectedStatName(statName);
	
	var team = NFL_PICKS_GLOBAL.initialTeam;
	if (isDefined(parameters) && isDefined(parameters.team)){
		team = parameters.team;
	}
	setSelectedTeam(team);
}

/**
 * 
 * This function will set all the selections from the given parameters.  It's here
 * so that we can do the "navigate forward and backward" thing.  We keep those parameters
 * in maps and then, to go forward and backward, we just have to feed the map we want to
 * this function.
 * 
 * This function <i>WON'T</i> update the view.  You'll have to do that yourself after calling it.
 * 
 * @param parameters
 * @returns
 */
function setSelectionsFromParameters(parameters){
	
	//Steps to do:
	//	1. If the parameters don't have anything, there's nothing to do.
	//	2. Otherwise, just go through and set each individual value.
	
	if (!isDefined(parameters)){
		return;
	}
	
	//need to clear all parameters here...
	//what does "clear" mean? ... it could mean different things
	
	if (isDefined(parameters.type)){
		setSelectedType(parameters.type);
	}
	
	if (isDefined(parameters.player)){
		setSelectedPlayer(parameters.player);
	}
	
	if (isDefined(parameters.year)){
		setSelectedYear(parameters.year);
	}
	
	if (isDefined(parameters.week)){
		setSelectedWeek(parameters.week);
	}
	
	if (isDefined(parameters.team)){
		setSelectedTeam(parameters.team);
	}
	
	if (isDefined(parameters.statName)){
		setSelectedStatName(parameters.statName);
	}
	
	if (isDefined(parameters.multiSelectPlayer)){
		setMultiSelectPlayer(parameters.multiSelectPlayer);
		updateMultiSelectVisibilityPlayer();
	}
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
 * Gets all the values for each kind of parameter (type, year, week, ...)
 * and returns them in a map with the key being the parameter.
 * 
 * Here so we can easily get what's selected (for the navigate forward and backward
 * stuff).
 * 
 * @returns
 */
function getSelectedParameters(){
	
	var parameters = {};
	
	parameters.type = getSelectedType();
	parameters.player = getSelectedPlayer();
	parameters.year = getSelectedYear();
	parameters.week = getSelectedWeek();
	parameters.statName = getSelectedStatName();
	parameters.team = getSelectedTeam();
	parameters.showOptions = areOptionsShown();
	parameters.multiSelectPlayer = isMultiSelectPlayerEnabled();
	
	return parameters;
}

/**
 * 
 * This function will get the initial selection criteria (teams, players, ...)
 * from the server and create the selection criteria for those options.
 * 
 * It will also initialize the NFL_PICKS_GLOBAL values (some are pulled from the server,
 * so that's why we do it in this function) and call the function that initializes the view 
 * once it's ready.
 * 
 * Those initial values will be:
 * 
 * 		1. type - standings
 * 		2. year - current
 * 		3. week - all
 * 		4. player - all
 * 		5. team - all
 * 		6. statName - champions
 * 
 * @returns
 */
function getSelectionCriteriaAndInitialize(){
	
	//Steps to do:
	//	1. Send the request to the server to get the selection criteria.
	//	2. When it comes back, pull out the years, players, and teams
	//	   and set the options for them in each select.
	//	3. Set the initial values in the NFL_PICKS_GLOBAL variable.
	//	4. Now that we have all the criteria and initial values, we can initialize the view.
	
	$.ajax({url: 'nflpicks?target=selectionCriteria',
			contentType: 'application/json; charset=UTF-8'}
	)
	.done(function(data) {
		var selectionCriteriaContainer = $.parseJSON(data);
		
		var years = selectionCriteriaContainer.years;
		//We want the "all" year option to be first.
		var yearOptions = [{label: 'All', value: 'all'}];
		for (var index = 0; index < years.length; index++){
			var year = years[index];
			yearOptions.push({label: year, value: year});
		}
		setOptionsInSelect('year', yearOptions);
		
		var players = selectionCriteriaContainer.players;
		//We want the "all" player option to be the first one.
		var playerOptions = [{id: 'selected-players', label: '', value: ''},
		                     {label: 'Everybody', value: 'all'}];
		for (var index = 0; index < players.length; index++){
			var player = players[index];
			playerOptions.push({id: normalizeString('player-' + player), label: player, value: player});
		}
		setOptionsInSelect('player', playerOptions);
		hideSelectedPlayersOption();
		
		var teams = selectionCriteriaContainer.teams;
		//Sort the teams in alphabetical order to make sure we show them in a consistent order.
		teams.sort(function (teamA, teamB){
			
			if (teamA.abbreviation < teamB.abbreviation){
				return -1;
			}
			else if (teamA.abbreviation > teamB.abbreviation){
				return 1;
			}
			
			return 0;
		});
		//We also want the "all" option to be first.
		var teamOptions = [{label: 'All', value: 'all'}];
		for (var index = 0; index < teams.length; index++){
			var team = teams[index];
			teamOptions.push({label: team.abbreviation, value: team.abbreviation});
		}
		setOptionsInSelect('team', teamOptions);

		//The current year and week come from the server.
		NFL_PICKS_GLOBAL.currentYear = selectionCriteriaContainer.currentYear;
		NFL_PICKS_GLOBAL.currentWeekNumber = selectionCriteriaContainer.currentWeekNumber;
		//Initially, we want to see the standings for the current year for everybody, so set those
		//as the initial types.
		NFL_PICKS_GLOBAL.initialType = 'standings';
		NFL_PICKS_GLOBAL.initialYear = NFL_PICKS_GLOBAL.currentYear;
		NFL_PICKS_GLOBAL.initialWeek = 'all';
		NFL_PICKS_GLOBAL.initialPlayer = 'all';
		NFL_PICKS_GLOBAL.initialTeam = 'all';
		NFL_PICKS_GLOBAL.initialStatName = 'champions';
		
		initializeView();
	})
	.fail(function() {
	})
	.always(function() {
	});
}

/**
 * 
 * This function will cause the view to "navigate forward".  We can only do that if
 * we've gone back.  So, this function will check the stack that holds the "forward parameters",
 * pop the top of it off (if there's something in it), and then cause the view to have those
 * parameters.
 * 
 * Before navigating, it will take the current parameters and put them on the previous stack
 * so they can go back if they hit "back".
 * 
 * @returns
 */
function navigateForward(){
	
	//Steps to do:
	//	1. If there aren't any forward parameters, there's no way to go forward.
	//	2. The current parameters should go on the previous stack.
	//	3. Get the forward parameters off the forward stack.
	//	4. Set them as the selections.
	//	5. Update the view and make sure it doesn't push any parameters on any
	//	   stack
	//	6. Flip the switch back so any other navigation will push parameters
	//	   on the previous stack.
	
	if (NFL_PICKS_GLOBAL.navigationForwardStack.length == 0){
		return;
	}
	
	var currentParameters = getSelectedParameters();
	NFL_PICKS_GLOBAL.navigationBackwardStack.push(currentParameters);
	
	var parameters = NFL_PICKS_GLOBAL.navigationForwardStack.pop();
	
	setSelectionsFromParameters(parameters);
	
	//Before updating the view, flip the switch that the updateView function uses to 
	//decide whether to push the parameters for the current view on the stack or not.
	//Since we're navigating forward, we take care of that in this function instead.
	//A little bootleg, so it probably means I designed it wrong...
	NFL_PICKS_GLOBAL.pushPreviousParameters = false;
	updateView();
	NFL_PICKS_GLOBAL.pushPreviousParameters = true;
}

/**
 * 
 * This function will cause the view to show the previous view.  It's the same thing
 * as going backward except will pull from the navigate backward stack.  The previous parameters
 * for the view are stored on a stack, so to go backward, we just have to pop those parameters
 * off, set them as the selections, and the update the view.  
 * 
 * It'll also take the current parameters (before going backward) and push them on the forward stack
 * so navigating forward, after going backward, brings them back to where they were.
 * 
 * @returns
 */
function navigateBackward(){

	//Steps to do:
	//	1. If there isn't anything to backward to, there's nothing to do.
	//	2. The current parameters should go on the forward stack since they're
	//	   what we want to show if people navigate forward
	//	3. The parameters we want to use come off the backward stack.
	//	4. Flip the switch that says to not push any parameters on in the view function.
	//	5. Update based on the parameters we got.
	//	6. Flip the switch back so that any other navigation causes the parameters
	//	   to go on the previous stack.
	
	if (NFL_PICKS_GLOBAL.navigationBackwardStack.length == 0){
		return;
	}
	
	var currentParameters = getSelectedParameters();
	NFL_PICKS_GLOBAL.navigationForwardStack.push(currentParameters);
	
	var parameters = NFL_PICKS_GLOBAL.navigationBackwardStack.pop();
	
	//stuff is updated here...
	setSelectionsFromParameters(parameters);
	
	//Just like when navigating forward, we don't want the updateView function to fiddle
	//with the navigation stacks since we're doing it here.  After the view has been updated, though,
	//flip the switch back so that any other navigation cause the updateView function save the
	//current view before changing.
	NFL_PICKS_GLOBAL.pushPreviousParameters = false;
	updateView();
	NFL_PICKS_GLOBAL.pushPreviousParameters = true;
}

/**
 * 
 * This function will make it so we only show the forward and backward
 * links if they can actually navigate forward and backward.  It just checks
 * the length of the stacks and uses that to decide whether to show
 * or hide each link.
 * 
 * @returns
 */
function updateNavigationLinksVisibility(){
	
	//Steps to do:
	//	1. If the stack doesn't have anything in it, we shouldn't show
	//	   the link.
	//	2. Otherwise, we should.
	
	if (NFL_PICKS_GLOBAL.navigationForwardStack.length == 0){
		$('#navigationFowardContainer').hide();
	}
	else {
		$('#navigationFowardContainer').show();
	}
	
	if (NFL_PICKS_GLOBAL.navigationBackwardStack.length == 0){
		$('#navigationBackwardContainer').hide();
	}
	else {
		$('#navigationBackwardContainer').show();
	}
}

/**
 * 
 * The "main" function for the UI.  Makes it so we show what they picked on the screen.
 * It bases its decision on the "type" variable and then just calls the right function
 * based on what that is.
 * 
 * If the NFL_PICKS_GLOBAL.pushPreviousParameters switch is flipped, it'll also update
 * the navigation stacks.  That switch is there so that:
 * 
 * 		1. When they do any non-forward or backward navigation action, we update the stacks.
 * 		2. When they push forward or backward, we can handle the stacks other places.
 * 
 * @returns
 */
function updateView(){

	//Steps to do:
	//	1. Before doing anything, if the switch is flipped, we should save the parameters
	//	   from the last navigation on the backward stack so they can go backward to what
	//	   we're currently on, if they want.
	//	2. Get the type of view they want.
	//	3. Update the selector view based on the type.
	//	4. Decide which function to call based on that.
	//	5. After the view is updated, keep the current selected parameters around so we can push
	//	   them on the "back" stack the next time they make a change.
	//	6. Make sure we're showing the right "navigation" links.
	
	//If there are previous parameters, and we should push them, push them on the backward
	//navigation stack so they can go back to that view with the back button.
	//If we shouldn't push them, that means the caller is handling the stack stuff themselves.
	//And, if we should push them, that means they did some "action" that takes them on a
	//different "branch", so we should clear out the forward stack since they can't go
	//forward anymore.
	if (NFL_PICKS_GLOBAL.previousParameters != null && NFL_PICKS_GLOBAL.pushPreviousParameters){
		NFL_PICKS_GLOBAL.navigationBackwardStack.push(NFL_PICKS_GLOBAL.previousParameters);
		NFL_PICKS_GLOBAL.navigationForwardStack = [];
	}
	
	var type = getSelectedType();
	
	//Update the selectors that get shown.  We want to show different things depending
	//on the type.
	updateSelectors(type);
	
	if ('picks' == type){
		updatePicks();
	}
	else if ('standings' == type) {
		updateStandings();
	}
	else if ('stats' == type){
		updateStats();
	}
	
	//At this point, the selected parameters are the current parameters.  We want to
	//keep them around in case we need to push them on the stack the next time through.
	NFL_PICKS_GLOBAL.previousParameters = getSelectedParameters();
	
	//And we need to make sure we're showing the right "forward" and "back" links.
	updateNavigationLinksVisibility();
}

/**
 * 
 * This function will update the selectors for the given type.  It just calls
 * the specific type's update function.
 * 
 * @param type
 * @returns
 */
function updateSelectors(type){
	
	//Steps to do:
	//	1. Call the function based on the type.
	
	if ('picks' == type){
		updatePicksSelectors(type);
	}
	else if ('standings' == type){
		updateStandingsSelectors(type);
	}
	else if ('stats' == type){
		updateStatsSelectors(type);
	}
	
	updateMultiSelects(type);
}

function updateMultiSelects(type){
	
	updateMultiSelectPlayer();
	
		//I wonder if this should be called inside each selector, instead of in multi selects?
		
		//this should be the newly selected player
		//they should be appended to the current selection
		//we're taking advantage of the fact that, on the change,
		//the value of the select will be the single value, and the 
		//previous values will be stored in the
		//selected-players value
		//
		// .... is this the way it should be done?
		//
		//either this, or store it on the NFL_PICKS_GLOBAL variable
		//... we still have to have an extra one to store multiple
		//selected players because we have to show it.
		//var selectedPlayer = getSelectedPlayer();
}

//refreshes the player selection ... if there was a newly selected player, they'll
//be added to the selected players.  if the same players are selected, there will be
//a kind of refresh of the list.
//
//
//This function should combine the selected player with the previous selections
//and set that as the selected player
function updateMultiSelectPlayer(){
	
	//if this is a newly selected player
	var selectedPlayerValue = getSelectedPlayer();
	
	var multiSelectPlayerEnabled = isMultiSelectPlayerEnabled();
	
	if (!multiSelectPlayerEnabled){
		setSelectedPlayer(selectedPlayerValue);
		updateMultiSelectVisibilityPlayer();
		return;
	}
	
	//these are the one from the container
	var selectedPlayersValue = getSelectedPlayers();
	
	var selectedPlayersValueArray = selectedPlayersValue.split(',');
	for (var index = 0; index < selectedPlayersValueArray.length; index++){
		var selectedPlayer = selectedPlayersValueArray[index].trim();
		selectedPlayersValueArray[index] = selectedPlayer;
	}
	
	var newPlayersToSelect = selectedPlayerValue.split(',');
	
	for (var index = 0; index < newPlayersToSelect.length; index++){
		var newPlayerToSelect = newPlayersToSelect[index].trim();
		
		if (!selectedPlayersValueArray.includes(newPlayerToSelect) &&
				doesSelectHaveOptionWithValue('player', newPlayerToSelect)){
		
			if (selectedPlayersValue.length > 0){
				selectedPlayersValue = selectedPlayersValue + ', ';
			}
			
			selectedPlayersValue = selectedPlayersValue + newPlayerToSelect;
		}
	}
	
	setSelectedPlayer(selectedPlayersValue);
	
	updateMultiSelectVisibilityPlayer();
}

/*
 /*
 //this should be done outside of here
		var currentSelectedPlayer = getSelectedPlayer();
		if (currentSelectedPlayer != 'all'){
			updateSelectedPlayersOption(currentSelectedPlayer);
		}
		else {
			updateSelectedPlayersOption('');
		}
 */
function multiSelectOptionChangePlayer(){
	
	//if there's a single player selected, they shold be put in the
	//selectedPlayers
	var currentSelectedPlayer = getSelectedPlayer();
	
	var currentSelectedPlayers = getSelectedPlayers();
	
	//what are the possibilities
	//	there's a single player selected and they enabled it
	//		if it's not all, make that player the selected players
	//	there's multiple players selected and they disabled it
	//		keep them shown i think....
	
	var multiSelectPlayerEnabled = isMultiSelectPlayerEnabled();
	if (multiSelectPlayerEnabled){
		//move the selection up to the top... the update visibility function
		//will show it.
		if (currentSelectedPlayer != 'all'){
			updateSelectedPlayersOption(currentSelectedPlayer);
		}
		//if it is all, what happens?
		//the visibility function will hide it, so it won't be selectable, but it'll still be shown because
		//it's currently selected.
	}
	else {
		//if they switched it off, we don't need to do anything
		//the visibility function will show all the player names and hide the multiple players option, but
		//it'll still be shown because it's currently selected
	}
	
	updateMultiSelectVisibilityPlayer();
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
	
	hideStatNameContainer();

	showPlayerContainer();
	
	showAllPlayerOption();
	showYearContainer();
	showTeamContainer();
	showWeekContainer();
	
	setPreviousType(type);
}

/**
 * 
 * Updates the selectors so that they're right for browsing the "standings".
 * 
 * Shows:
 * 		player, year, week
 * Hides:
 * 		team, stat name
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
	
	hideStatNameContainer();
	hideTeamContainer();
	
	showPlayerContainer();
	showAllPlayerOption();
	showYearContainer();
	showWeekContainer();
	
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
	
	showStatNameContainer();
	
	var statName = getSelectedStatName();
	
	if ('champions' == statName){
		hidePlayerContainer();
		hideYearContainer();
		hideWeekContainer();
		hideTeamContainer();
	}
	else if ('championshipStandings' == statName){
		hidePlayerContainer();
		hideYearContainer();
		hideWeekContainer();
		hideTeamContainer();
	}
	else if ('weekStandings' == statName){
		showYearContainer();
		showPlayerContainer();
		showAllPlayerOption();
		showWeekContainer();
		hideTeamContainer();
	}
	else if ('weeksWonStandings' == statName){
		showYearContainer();
		hideWeekContainer();
		hidePlayerContainer();
		hideTeamContainer();
	}
	else if ('weeksWonByWeek' == statName){
		showYearContainer();
		showWeekContainer();
		hidePlayerContainer();
		hideTeamContainer();
	}
	else if ('weekRecordsByPlayer' == statName){
		showYearContainer();
		showPlayerContainer();
		showWeekContainer();
		hideTeamContainer();
		hideAllPlayerOption();
	}
	else if ('pickAccuracy' == statName){
		showYearContainer();
		showPlayerContainer();
		showAllPlayerOption();
		hideWeekContainer();
		showTeamContainer();
		hideAllPlayerOption();
	}
	else if ('pickSplits' == statName){
		showYearContainer();
		showWeekContainer();
		showTeamContainer();
		hidePlayerContainer();
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
	return $('#type option:selected').val();
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
	if (doesSelectHaveOptionWithValue('type', type)){
		$('#type').val(type);
	}
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

function showSelectMultiplePlayersOption(){
	$('#select-multiple-players').show();
}

function hideSelectMultiplePlayersOption(){
	$('#select-multiple-players').hide();
}

function showSelectSinglePlayersOption(){
	$('#select-single-player').show();
}

function hideSelectSinglePlayersOption(){
	$('#select-single-player').hide();
}

function getSelectedPlayers(){
	return $('#selected-players').val();
}

function updateSelectedPlayersOption(selectedPlayers){
	$('#selected-players').val(selectedPlayers);
	$('#selected-players').text(selectedPlayers);
}

function makeSelectedPlayersOptionSelected(){
}

function showSelectedPlayersOption(){
	$('#selected-players').show();
}

function hideSelectedPlayersOption(){
	$('#selected-players').hide();
}

function hidePlayerOptions(players){
	
	var playersArray = players.split(',');
	
	for (var index = 0; index < playersArray.length; index++){
		var player = playersArray[index].trim();
		hidePlayerOption(player);
	}
}

function hidePlayerOption(player){
	$('#' + normalizeString('player-' + player)).hide();
}

function showAllPlayerOptions(){
	var allOptions = $('#player option');
	
	for (var index = 0; index < allOptions.length; index++){
		var option = allOptions[index];
		
		if (isDefined(option.id) && option.id.startsWith('player-')){
			$('#' + option.id).show();
		}
		
	}
}

/**
 * 
 * Gets the player that's selected.
 * 
 * @returns
 */
function getSelectedPlayer(){
	return $('#player option:selected').val();
}

/**
 * 
 * Sets the selected player to the given one if that player is
 * one of the player input's options.
 * 
 * @param player
 * @returns
 */
function setSelectedPlayer(player){
	
	if (!isDefined(player)){
		return;
	}
	
	//if the player has a comma in it, we need to enable multi select for the player
	//the forward and backward navigation need this...
	if (player.indexOf(',') != -1){
		setMultiSelectPlayer(true);
	}
	
	var multiSelectPlayerEnabled = isMultiSelectPlayerEnabled();
	
	if (!multiSelectPlayerEnabled){
		//I think this should go away ... there should be a function that updates
		//the visibility of the options
		updateSelectedPlayersOption('');
		setSelectedPlayerValue(player);
		return;
	}

	//update its value to what was given
	updateSelectedPlayersOption(player);
	//make it selected in the select
	setSelectedPlayerValue(player);
}

/**
 * 
 * Sets the value on the player input to the given value.  Doesn't do any
 * funny business of trying to figure out whether it's mutliple select or not.
 * 
 * @param value
 * @returns
 */
function setSelectedPlayerValue(value){
	$('#player').val(value);
}

/**
 * 
 * Gets the selected year.
 * 
 * @returns
 */
function getSelectedYear(){
	return $('#year option:selected').val();
}

/**
 * 
 * Sets the selected year if the year input has the given
 * year as an option.
 * 
 * @param year
 * @returns
 */
function setSelectedYear(year){
	if (doesSelectHaveOptionWithValue('year', year)){
		$('#year').val(year);
	}
}

/**
 * 
 * Gets the selected week.
 * 
 * @returns
 */
function getSelectedWeek(){
	return $('#week option:selected').val();
}

/**
 * 
 * Sets the selected week to the given week if it's one of
 * the week input's options.
 * 
 * @param week
 * @returns
 */
function setSelectedWeek(week){
	if (doesSelectHaveOptionWithValue('week', week)){
		$('#week').val(week);
	}
}

/**
 * 
 * Gets the selected stat name.
 * 
 * @returns
 */
function getSelectedStatName(){
	return $('#statName option:selected').val();
}

/**
 * 
 * Sets the selected stat name if it's one of the options
 * on the stat name input.
 * 
 * @param statName
 * @returns
 */
function setSelectedStatName(statName){
	if (doesSelectHaveOptionWithValue('statName', statName)){
		$('#statName').val(statName);
	}
}

/**
 * 
 * Gets the selected team.
 * 
 * @returns
 */
function getSelectedTeam(){
	return $('#team option:selected').val();
}

/**
 * 
 * Sets the selected team if the given team is one of the
 * options in the team input.
 * 
 * @param team
 * @returns
 */
function setSelectedTeam(team){
	if (doesSelectHaveOptionWithValue('team', team)){
		$('#team').val(team);
	}
}


function updateMultiSelectVisibilityPlayer(){
	
	var multiSelectPlayerEnabled = isMultiSelectPlayerEnabled();
	
	if (multiSelectPlayerEnabled){
		
		var selectedPlayers = getSelectedPlayers();
		
		if ('' != selectedPlayers){
			showSelectedPlayersOption();
		}

		showAllPlayerOptions();
		
		var currentSelectedPlayer = getSelectedPlayer();
		hidePlayerOptions(currentSelectedPlayer);
		hideAllPlayerOption();
	}
	else {
		hideSelectedPlayersOption();
		showAllPlayerOption();
		showAllPlayerOptions();
	}
}

function enableMultiSelectPlayer(){
	setMultiSelectPlayer(true);
	showOptions();
	updateMultiSelectVisibilityPlayer();
}

function setMultiSelectPlayer(value){
	return $('#multiSelectPlayer').prop('checked', value);
}

function isMultiSelectPlayerEnabled(){
	return $('#multiSelectPlayer').prop('checked');
}

/**
 * 
 * This function will set the given html as the content we show.  It'll clear out what's
 * in there now.
 * 
 * @param contentHtml
 * @returns
 */
function setContent(contentHtml){
	$('#contentContainer').empty();
	$('#contentContainer').append(contentHtml);
}

/**
 * 
 * This function will go get the standings from the server and show them on the UI.
 * 
 * What standings it gets depends on the player, year, and week that are selected.
 * 
 * @returns
 */
function updateStandings(){

	//Steps to do:
	//	1. Get the parameters to send (player, year, and week).
	//	2. Send them to the server.
	//	3. Update the UI with the results.
	
	var player = getSelectedPlayer();
	var year = getSelectedYear();
	var week = getSelectedWeek();
	
	//If they picked "regular season", that's weeks 1-17.
	//Otherwise, if they picked the playoffs, that's weeks 18-21.
	var weekToUse = week;
	if ('regular-season' == week){
		weekToUse = '1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17';
	}
	else if ('playoffs' == week){
		weekToUse = '18,19,20,21';
	}
	
	$.ajax({url: 'nflpicks?target=standings&player=' + player + '&year=' + year + '&week=' + weekToUse,
		contentType: 'application/json; charset=UTF-8'}
	)
	.done(function(data) {
		var standingsContainer = $.parseJSON(data);
		//We want to show the records that came back, but we're going to have to sort them
		//to make sure they're in the order we want.
		var records = standingsContainer.records;

		//We want the record with the most wins coming first.  If they have the same number
		//of wins, we want the one with fewer losses coming first.
		records.sort(function (record1, record2){
			if (record1.wins > record2.wins){
				return -1;
			}
			else if (record1.wins < record2.wins){
				return 1;
			}
			else {
				if (record1.losses < record2.losses){
					return -1;
				}
				else if (record1.losses > record2.losses){
					return 1;
				}
			}
			return 0;
		});
		
		//Now that we have them sorted, we can create the html for the standings.
		var standingsHtml = createStandingsHtml(standingsContainer.records);
		
		//And set it as the content.
		setContent(standingsHtml);
	})
	.fail(function() {
	})
	.always(function() {
	});
}

function hideAllPlayerOption(){
	console.log('hide all player');
	$('#player option[value=all]').hide();
}

function showAllPlayerOption(){
	console.log('show all player');
	$('#player option[value=all]').show();
}

function hideAllWeekOption(){
	$('#week option[value=all]').hide();
}

function showAllWeekOption(){
	$('#week option[value=all]').show();
}

function hideAllYearOption(){
	$('#year option[value=all]').hide();
}

function showAllYearOption(){
	$('#year option[value=all]').show();
}

function showYearContainer(){
	$('#yearContainer').show();
}

function hideYearContainer(){
	$('#yearContainer').hide();
}

function showPlayerContainer(){
	$('#playerContainer').show();
}

function hidePlayerContainer(){
	$('#playerContainer').hide();
}

function showWeekContainer(){
	$('#weekContainer').show();
}

function hideWeekContainer(){
	$('#weekContainer').hide();
}

function showTeamContainer(){
	$('#teamContainer').show();
}

function hideTeamContainer(){
	$('#teamContainer').hide();
}

function showStatNameContainer(){
	$('#statNameContainer').show();
}

function hideStatNameContainer(){
	$('#statNameContainer').hide();
}

function areOptionsShown(){
	
	if ($('#options').is(':visible')){
		return true;
	}
	
	return false;
}

function showOptions(){
	$('#optionsLink').text('Hide options');
	$('#options').show();
}

function hideOptions(){
	$('#optionsLink').text('Options');
	$('#options').hide();
}

function toggleOptions(){
	
	if (areOptionsShown()){
		hideOptions();
	}
	else {
		showOptions();
	}
}

/**
 * 
 * This function will update the picks grid with the current selectors they ... picked.
 * It'll get the parameters, go to the server to get the picks, and then update the UI
 * with the grid.
 * 
 * @returns
 */
function updatePicks(){
	
	//Steps to do:
	//	1. Get the parameters they picked.
	//	2. Default the year and week to the current year and week if we should.
	//	3. Go to the server and get the picks.
	//	4. Update the UI with the picks grid.
	
	var year = getSelectedYear();
	var player = getSelectedPlayer();
	var week = getSelectedWeek();
	var team = getSelectedTeam();

	//We need to make sure we only use "all" for the year if they explicitly set it.
	//
	//That should only happen if:
	//	1. It's "all" in the url.
	//	2. Or, they have seen the picks and have set it to "all" themselves.
	//
	//I'm doing it like this because using "all" for the year might bring back a lot
	//of picks, so we should only do it if that's what they want to do.
	var parameters = getUrlParameters();

	var hasYearInUrl = false;
	if (isDefined(parameters) && isDefined(parameters.year)){
		hasYearInUrl = true;
	}

	//We want to default it to the current year if:
	//
	//	1. It's "all"
	//	2. We haven't shown the picks before
	//	3. The "all" isn't from the url.
	//
	//In that situation, they didn't "explicitly" set it to "all", so we want to show
	//only picks for the current year to start off with.
	if ('all' == year && !NFL_PICKS_GLOBAL.havePicksBeenShown && !hasYearInUrl){
		year = NFL_PICKS_GLOBAL.currentYear;
		setSelectedYear(year);
	}

	//Do the same thing with the week.  We only want to show picks for all the weeks if
	//they went out of their way to say that's what they wanted to do.
	var hasWeekInUrl = false;
	if (isDefined(parameters) && isDefined(parameters.week)){
		hasWeekInUrl = true;
	}
	
	//If it's "all" and the picks haven't been shown and the "all" didn't come from the url,
	//it's their first time seeing the picks, so we should show the ones for the current week.
	if ('all' == week && !NFL_PICKS_GLOBAL.havePicksBeenShown && !hasWeekInUrl){
		week = NFL_PICKS_GLOBAL.currentWeekNumber + '';
		setSelectedWeek(week);
	}
	
	//At this point, we're going to show them the picks, so we should flip that switch.
	NFL_PICKS_GLOBAL.havePicksBeenShown = true;

	//If the week is a "special" one, put in the actual numbers instead.
	var weekToUse = week;
	if ('regular-season' == week){
		weekToUse = '1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17';
	}
	else if ('playoffs' == week){
		weekToUse = '18,19,20,21';
	}

	//Go to the server and get the grid.
	$.ajax({url: 'nflpicks?target=compactPicksGrid&player=' + player + '&year=' + year + '&week=' + weekToUse + '&team=' + team,
		contentType: 'application/json; charset=UTF-8'}
	)
	.done(function(data) {
		//Update the UI with what the server sent back.
		var picksGrid = $.parseJSON(data);
		var picksGridHtml = createPicksGridHtml(picksGrid);
		setContent(picksGridHtml);
	})
	.fail(function() {
	})
	.always(function() {
	});
}

/**
 * 
 * This function will get the stats from the server and update them on the ui.  The stat that
 * it shows depends on the statName they picked.
 * 
 * @returns
 */
function updateStats(){
	
	//Steps to do:
	//	1. Get the selected parameters.
	//	2. Make sure they're ok based on the stat name.
	//	3. Go to the server and get the stats.
	//	4. Update the UI with what came back.
	
	var statName = getSelectedStatName();
	var player = getSelectedPlayer();
	var year = getSelectedYear();
	var week = getSelectedWeek();
	var team = getSelectedTeam();
	
	//If the stat is "week records by player" or "pick accuracy", then they have to pick
	//a player, so set it to the first player if there isn't one or it's "all".
	if (statName == 'weekRecordsByPlayer' || statName == 'pickAccuracy'){
		if (!isDefined(player) || 'all' == player){
			var firstRealPlayer = $('#player option')[1].value;
			setSelectedPlayer(firstRealPlayer);
			player = getSelectedPlayer();
		}
	}
	//If we're showing the champions or championship standings, we want to show them
	//for all players and all years.
	else if (statName == 'champions' || statName == 'championshipStandings'){
		//With multi select, we don't want to do this
		if (!isMultiSelectPlayerEnabled()){
			setSelectedPlayer('all');
		}
		setSelectedYear('all');
		player = getSelectedPlayer();
		year = getSelectedYear();
	}
	//If the stat name is the "pick splits", we want to do the same thing we do with the picks grid.
	//Only show "all" for the year or the week if they actually set it to "all".
	//If it's the first time we're showing the pick splits, we only want to show all of them if that
	//was in the url.
	else if (statName == 'pickSplits'){
		//Since we're showing how players are split up, we want to show all players.
		setSelectedPlayer('all');
		player = getSelectedPlayer();
		var urlParameters = getUrlParameters();
		
		//Same deal as with the picks grid...
		var hasYearInUrl = false;
		if (isDefined(urlParameters) && isDefined(urlParameters.year)){
			hasYearInUrl = true;
		}
		
		//If the year is "all", we haven't shown the picks, and "all" didn't come from the url, then we
		//want the year we show the pick splits for to be the current year.
		if ('all' == year && !NFL_PICKS_GLOBAL.havePickSplitsBeenShown && !hasYearInUrl){
			year = NFL_PICKS_GLOBAL.currentYear;
			setSelectedYear(year);
			year = getSelectedYear();
		}
		
		//Same deal as with the year and with the picks grid...
		var hasWeekInUrl = false;
		if (isDefined(urlParameters) && isDefined(urlParameters.week)){
			hasWeekInUrl = true;
		}
		
		//If the week is "all", we haven't shown the picks, and "all" didn't come from the url, then we
		//want the week we show the pick splits for to be the current week.
		if ('all' == week && !NFL_PICKS_GLOBAL.havePickSplitsBeenShown && !hasWeekInUrl){
			week = NFL_PICKS_GLOBAL.currentWeekNumber + '';
			setSelectedWeek(week);
			week = getSelectedWeek();
		}
		
		//And, since we're here, that means we've shown the pick splits to the user, so the next time, we won't
		//do the funny business with the week and year.
		NFL_PICKS_GLOBAL.havePickSplitsBeenShown = true;
	}

	//If the week was one of the "special" ones, use the actual weeks instead.
	var weekToUse = week;
	if ('regular-season' == week){
		weekToUse = '1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17';
	}
	else if ('playoffs' == week){
		weekToUse = '18,19,20,21';
	}
	
	//Send the request to the server.
	$.ajax({url: 'nflpicks?target=stats&statName=' + statName + '&year=' + year + '&player=' + player + '&week=' + weekToUse + '&team=' + team,
			contentType: 'application/json; charset=UTF-8'}
	)
	.done(function(data) {
		var statsHtml = '';

		//Make the html for the kind of stat they wanted to see.
		if ('champions' == statName){
			var championships = $.parseJSON(data);
			statsHtml = createChampionsHtml(championships);
			
		}
		else if ('championshipStandings' == statName){
			var championships = $.parseJSON(data);
			statsHtml = createChampionshipStandingsHtml(championships);
		}
		else if ('weeksWonStandings' == statName){
			var weekRecords = $.parseJSON(data);
			//We want to sort the records before we show them so we can show the rank.
			sortWeekRecords(weekRecords);
			statsHtml = createWeeksWonHtml(weekRecords);
		}
		else if ('weeksWonByWeek' == statName){
			var weeksWonByWeek = $.parseJSON(data);
			statsHtml = createWeeksWonByWeek(weeksWonByWeek);
		}
		else if ('weekRecordsByPlayer' == statName){
			var weekRecords = $.parseJSON(data);
			//Like with the other records, we want to sort them before we show them.
			sortWeekRecordsBySeasonAndWeek(weekRecords);
			statsHtml = createWeekRecordsByPlayerHtml(weekRecords);
		}
		else if ('weekStandings' == statName){
			var playerWeekRecords = $.parseJSON(data);
			statsHtml = createWeekStandingsHtml(playerWeekRecords);
		}
		else if ('pickAccuracy' == statName){
			var pickAccuracySummaries = $.parseJSON(data);
			statsHtml = createPickAccuracySummariesHtml(pickAccuracySummaries);
		}
		else if ('pickSplits' == statName){
			var pickSplits = $.parseJSON(data);
			statsHtml = createPickSplitsGridHtml(pickSplits);
		}
		
		setContent(statsHtml);
	})
	.fail(function() {
	})
	.always(function() {
	});
	
}

/**
 * 
 * A "convenience" function that says whether any record in the given
 * array has any ties.
 * 
 * @param records
 * @returns
 */
function hasTies(records){
	
	//Steps to do:
	//	1. Go through all the records and return true if one has a tie.
	
	if (!isDefined(records)){
		return false;
	}
	
	for (var index = 0; index < records.length; index++){
		var record = records[index];
		
		if (record.ties > 0){
			return true;
		}
	}
	
	return false;
}

/**
 * 
 * This function will compare the given records and return -1 if the first record
 * has more wins than the second, 1 if it has more, and 0 if it's the same.
 * 
 * If the records have the same number of wins, then it bases the comparison on the
 * losses.  If record1 has the same wins but fewer losses, it should go first, so it 
 * returns -1.
 * 
 * It'll return 0 if they have the exact same number of wins and losses.
 * 
 * We do this in a few different places, so I decided to make a function.
 * 
 * @param record1
 * @param record2
 * @returns
 */
function recordWinComparisonFunction(record1, record2){
	
	//Steps to do:
	//	1. Compare based on the wins first.
	//	2. If they're the same, compare on the losses.
	
	//More wins should go first.
	if (record1.wins > record2.wins){
		return -1;
	}
	//Fewer wins should go last.
	else if (record1.wins < record2.wins){
		return 1;
	}
	else {
		//With the same number of wins, fewer losses should go first.
		if (record1.losses < record2.losses){
			return -1;
		}
		//And more losses should go second.
		else if (record1.losses > record2.losses){
			return 1;
		}
	}
	
	//Same wins and losses = same record.
	return 0;
}

/**
 * 
 * This function will create the html for showing the standings for the given records.
 * It expects them to be sorted in the order they should be shown already, so it won't do any
 * sorting of them (it's up to the caller to do that).
 * 
 * @param records
 * @returns
 */
function createStandingsHtml(records){
	
	//Steps to do:
	//	1. Create the header for the standings.
	//	2. Figure out what the most wins and losses are for the records (so
	//	   we can get the games back when making them).
	//	3. Go through each record, get its rank, and add it to the table.
	//	4. Put all the parts of the table together.	
	
	var standingsHtml = '';
	
	//We only want to include the ties header if there's a record in there
	//with a tie.
	var areThereAnyTies = hasTies(records);
	var tiesHeader = '';
	if (areThereAnyTies){
		tiesHeader = '<th class="standings-table-header">T</th>';
	}
	
	var standingsHeaderHtml = '<thead class="standings-table-head">' +
						 	  	'<th class="standings-table-player-header"></th>' +
						 	  	'<th class="standings-table-header">W</th>' + 
						 	  	'<th class="standings-table-header">L</th>' +
						 	  	tiesHeader + 
						 	  	'<th class="standings-table-header">%</th>' + 
						 	  	'<th class="standings-table-header">GB</th>' + 
						 	  '</thead>';

	//For holding the table rows.
	var rowsHtml = '';
	
	//Here so we can figure out how many games back a record is from the top.
	var topWins = 0;
	var topLosses = 0;
	
	if (!isEmpty(records)){
		topWins = records[0].wins;
		topLosses = records[0].losses;
	}
	//If there aren't any records, then we should just show that there aren't any results.
	else {
		rowsHtml = '<tr><td colspan="5" style="text-align: center;">No results</td></tr>';
	}

	for (var index = 0; index < records.length; index++){
		var record = records[index];

		//Get the rank of this record within all the other records.  The "rank" function
		//takes care of doing that.  We just have to tell it how to compare two records and
		//when records are exactly the same.
		//The "recordWinComparisonFunction" compares records based on their wins and losses,
		//and the inline function says a record is exactly the same when it's for the same player.
		var recordRank = rank(record, records, recordWinComparisonFunction, function (record1, record2){
			
			if (record1.player.id == record2.player.id){
				return true;
			}
			
			return false;
		});
		
		//We want to show the percentage too.
		var percentage = record.wins / (record.wins + record.losses);
		var percentageString = '';
		//And we want it to 3 decimal places.
		if (!isNaN(percentage)){
			percentageString = percentage.toPrecision(3);
		}
		
		var gamesBack = '';
		if (record.losses == topLosses && record.wins == topWins){
			gamesBack = '-';
		}
		else {
			var calculatedGamesBack = topWins - record.wins;
			gamesBack = calculatedGamesBack + '';
		}
		
		var tiesCell = '';
		if (areThereAnyTies){
			tiesCell = '<td class="standings-table-cell">' + record.ties + '</td>';
		}
		
		var rankText = recordRank.rank + '';
		if (recordRank.tie){
			rankText = rankText + 't';
		}
		
		//Now we have everything we need for the row.
		rowsHtml = rowsHtml + 
				  '<tr class="standings-table-row">' +
				  	'<td class="standings-table-player-cell">' + rankText + '. ' + record.player.name + '</td>' +
				  	'<td class="standings-table-cell">' + record.wins + '</td>' +
				  	'<td class="standings-table-cell">' + record.losses + '</td>' +
				  	tiesCell + 
				  	'<td class="standings-table-cell">' + percentageString + '</td>' +
				  	'<td class="standings-table-cell">' + gamesBack + '</td>' + 
				  '</tr>';
	}

	//And now we just have to put them together.
	var standingsBodyHtml = '<tbody class="standings-table-body">' + rowsHtml + '</tbody>';
	
	standingsHtml = '<table class="standings-table">' + standingsHeaderHtml + standingsBodyHtml + '</table>';
	
	return standingsHtml;
}

function createStandingsGridHtml2(records){
	
	var standingsHtml = '';
	
	var areThereAnyTies = hasTies(records);
	var tiesHeader = '';
	if (areThereAnyTies){
		tiesHeader = '<th class="standings-table-header">T</th>';
	}
	
	var standingsHeaderHtml = '<thead class="standings-table-head">' +
						 	'<th class="standings-table-player-header"></th>' +
						 	'<th class="standings-table-header">W</th>' + 
						 	'<th class="standings-table-header">L</th>' +
						 	tiesHeader + 
						 	'<th class="standings-table-header">%</th>' + 
						 	'<th class="standings-table-header">GB</th>';
	
	
	standingsHeaderHtml = standingsHeaderHtml + '</thead>';
	
	var rowsHtml = '';
	
	var topWins = 0;
	var topLosses = 0;
	
	if (!isEmpty(records)){
		topWins = records[0].wins;
		topLosses = records[0].losses;
	}
	else {
		rowsHtml = '<tr><td colspan="5" style="text-align: center;">No results</td></tr>';
	}

	//The steps for calculating the rank:
	//	1. Have three variables: rank, nextRank, and tieIndependentRank.
	//	2. rank holds the rank of the current record we're on.  
	//	3. nextRank holds what the rank should be the next time we go through
	//	   the loop.
	//	4. tieIndependentRank holds the rank independent of ties.  Basically what it would be if
	//	   there were no ties (the position of the record in the array, starting at 1).
	//	5. Start the nextRank at 1 because that's what the rank of the next record we see will be.
	//	6. Start going through the records.
	//	7. Assign the nextRank that we calculated to the rank so that we use it for this record.
	//	8. Calculate the nextRank:
	//		1. If there's a next record and it has the same number of wins and losses as this one, then
	//		   the nextRank will be same as the current rank because there's a tie.
	//		2. Otherwise, it'll be whatever "tieIndepdentedRank" we have.  That's because we'll
	//		   want to basically pick up where we left off before the ties started.
	
	var rank = null;
	var nextRank = 1;
	var nextRecord = null;
	var previousRank = null;
	
	for (var index = 0; index < records.length; index++){
		var record = records[index];
		
		//This is the position of the record independent of whether there are ties.  Just the "raw" position if we
		//started counting at 1.  It will be the same as the rank if there aren't any ties.
		var tieIndependentRank = index + 1;
		//Set the rank to what we calculated it should be the previous time through the loop.
		rank = nextRank;
		
		//Now, need to calculate what it will be the next time.
		//If the next record has the same number of wins and losses, then it'll be the same as now because they're
		//tied.
		//Otherwise, if the next record doesn't, the next rank will be whatever this one's would have
		//been without ties + 1.  If there weren't any ties, then this record's rank would be the "tieIndependentRank".
		//So, that means the next rank would be that + 1.
		nextRecord = null;
		if (index + 1 < records.length){
			nextRecord = records[index + 1];
			
			if (record.wins == nextRecord.wins && record.losses == nextRecord.losses){
				//rank stays the same.
			}
			else {
				//current rank would be index + 1.  We want to be one beyond that.
				nextRank = tieIndependentRank + 1;
			}
		}
		
		//Now, we have the rank and next rank so we need to figure out if we need to put a little 't' to indicate
		//there was a tie.
		//There's a tie if:
		//	1. It's the same as the next rank and we're not at the end.
		//	2. The rank is the same as the previous rank.
		//
		//Number 1 should be pretty straight forward.  If this rank is the same as the next one, it's in a tie.
		//Number 2 is there for the last tie in a series of ties.  The last tie will have a "nextRank" that's different from
		//what it is, but we'll still want to show a tie for it.  So, in that case, we can just look to see if it's the same
		//as the previous rank and, if it is, we know there's a tie.
		var rankText = rank + '';
		if ((nextRank == rank && index + 1 < records.length) || (rank == previousRank)){
			rankText = rankText + 't';
		}
		
		var percentage = record.wins / (record.wins + record.losses);
		var percentageString = '';
		if (!isNaN(percentage)){
			percentageString = percentage.toPrecision(3);
		}
		var gamesBack = '';
		
		if (record.losses == topLosses && record.wins == topWins){
			gamesBack = '-';
		}
		else {
			var calculatedGamesBack = topWins - record.wins;
			gamesBack = calculatedGamesBack + '';
		}
		
		var tiesCell = '';
		if (areThereAnyTies){
			tiesCell = '<td class="standings-table-cell">' + record.ties + '</td>';
		}
		
		rowsHtml = rowsHtml + 
					   '<tr class="standings-table-row">' +
						'<td class="standings-table-player-cell">' + rankText + '. ' + record.player.name + '</td>' +
						'<td class="standings-table-cell">' + record.wins + '</td>' +
						'<td class="standings-table-cell">' + record.losses + '</td>' +
						tiesCell + 
						'<td class="standings-table-cell">' + percentageString + '</td>' +
						'<td class="standings-table-cell">' + gamesBack + '</td>';
		
		rowsHtml = rowsHtml + '</tr>';
		
		//Keep the current rank as the previous for the next time through.
		previousRank = rank;
		
	}
	
	var standingsBodyHtml = '<tbody class="standings-table-body">' + rowsHtml + '</tbody>';
	
	standingsHtml = '<table class="standings-table">' + standingsHeaderHtml + standingsBodyHtml + '</table>';
	
	return standingsHtml;
}

/**
 * 
 * This function will make the grid that holds the given picks in it.  It gets a little
 * hard to follow explaining how the css works, so hopefully I do an ok job explaining that.
 * 
 * @param picksGrid
 * @returns
 */
function createPicksGridHtml(picksGrid){

	//Steps to do:
	//	1. This would get too long, so I'll do it old school like how I did in college.
	
	//If they selected a year, we don't want to show it as a column.  But, if the year
	//is "all", then we're going to be showing multiple years, so we want a column for it.
	var yearHeader = '';
	var yearSelected = isSpecificYearSelected();
	if (!yearSelected){
		yearHeader = '<th align="left" class="table-header">Year</th>';
	}
	
	//Same deal with the week.  If they don't have a specific week selected, we want to show the week.  If they
	//do, then we don't.
	var weekHeader = '';
	var selectedWeek = getSelectedWeek();
	var weekSelected = isSpecificWeekSelected();
	if (!weekSelected){
		weekHeader = '<th align="left" class="table-header">Week</th>';
	}
	
	//The grid will have:
	//	1. The year (if we have it)
	//	2. The week (if we have it)
	//	3. The game
	//	4. Each player's pick.
	var gridHeaderHtml = '<thead>' +
							yearHeader + 
							weekHeader + 
						 	'<th align="left" class="table-header">Game</th>';
	
	for (var index = 0; index < picksGrid.players.length; index++){
		var player = picksGrid.players[index];
		
		//Each player's pick will have two columns: the pick and the result, so we want the player name
		//to span both of them.
		gridHeaderHtml = gridHeaderHtml + '<th align="left" colspan="2" class="table-header">' + player + '</th>';
	}
	
	gridHeaderHtml = gridHeaderHtml + '</thead>';

	//We want to show the records for the picks right below the player names.  This just initializes
	//them all to 0.  We'll fill them in as we're going through the picks and then add them at the end.
	var playerRecords = [];
	for (var index = 0; index < picksGrid.players.length; index++){
		var player = picksGrid.players[index];
		var playerRecord = {player: player,
							wins: 0,
							losses: 0,
							ties: 0};
		playerRecords[index] = playerRecord;
	}
	
	//Ok, now it's game time...
	var pickRowsHtml = '';
	
	for (var index = 0; index < picksGrid.picks.length; index++){
		var pick = picksGrid.picks[index];
		
		//We want the rows to have different backgrounds for even and odd so it's easy to see.
		var rowClassName = 'even-row';
		if (index % 2 == 1){
			rowClassName = 'odd-row';
		}
		
		//And we want to use different css to indicate winners and losers.
		var homeTeamClass = '';
		var awayTeamClass = '';
		if (isDefined(pick.winningTeamAbbreviation)){
			if (pick.winningTeamAbbreviation == pick.awayTeamAbbreviation){
				awayTeamClass = 'winner';
			}
			else if (pick.winningTeamAbbreviation == pick.homeTeamAbbreviation){
				homeTeamClass = 'winner';
			}
			else {
				awayTeamClass = 'tie';
				homeTeamClass = 'tie';
			}
		}
		
		//We need to do some different things when we're on the bottom row, so we should check
		//that switch.
		var isBottomRow = false;
		if (index + 1 == picksGrid.picks.length){
			isBottomRow = true;
		}

		//If they didn't pick a year, we'll want to add the cell for the year first.
		var yearCell = '';
		if (!yearSelected){
			//And, we know that the year should be the first column, so it should use that css.
			var cssClassToUse = 'first-pick-cell';
			//And the first column, bottom row css if it's the bottom row (so the border is right).
			if (isBottomRow){
				cssClassToUse = 'first-pick-cell-bottom';
			}
			
			yearCell = '<td class="' + cssClassToUse + '">' + pick.year + '</td>';
		}
		
		//Same deal with the week.  If they didn't pick one, add in a column for it.
		var weekCell = '';
		if (!weekSelected){
			
			var cssClassToUse = null;
			
			//A little more complicated than the year because it might or might not be the first column.
			
			//If the year isn't selected, and it's not the bottom, then we're showing the year, so the week
			//is just a middle column.
			if (!yearSelected && !isBottomRow){
				cssClassToUse = 'pick-cell';
			}
			//If it's a middle column, but it's on the bottom, we want the one with the bottom border.
			else if (!yearSelected && isBottomRow){
				cssClassToUse = 'pick-cell-bottom';
			}
			//If the year was picked, then this is going to be the first column.
			else if (yearSelected && !isBottomRow){
				cssClassToUse = 'first-pick-cell';
			}
			//And this is for if it's the first column and on the bottom (so it gets the bottom border).
			else if (yearSelected && isBottomRow){
				cssClassToUse = 'first-pick-cell-bottom';
			}
		
			weekCell = '<td class="' + cssClassToUse + '">' + pick.weekNumber + '</td>';
		}

		//Now, we have to do the same thing with the game that we did with the week.
		//It'll be the first column in the table if the week and year are selected (because
		//that means they won't be shown as columns).
		var isGameFirstColumn = weekSelected && yearSelected;

		var gameCssClassToUse = null;
		if (!isGameFirstColumn && !isBottomRow){
			gameCssClassToUse = 'pick-cell';
		}
		else if (!isGameFirstColumn && isBottomRow){
			gameCssClassToUse = 'pick-cell-bottom';
		}
		else if (isGameFirstColumn && !isBottomRow){
			gameCssClassToUse = 'first-pick-cell';
		}
		else if (isGameFirstColumn && isBottomRow){
			gameCssClassToUse = 'first-pick-cell-bottom';
		}

		//Now we can put the first part of the pick row together.
		//It's the year and week (if they're there), followed by the game.
		var gameRow = '<tr class="' + rowClassName + '">' + 
						yearCell +
						weekCell +
						'<td class="' + gameCssClassToUse + '">' + 
							'<span class="' + awayTeamClass + '">' + pick.awayTeamAbbreviation + '</span>' + 
							' @ ' + 
							'<span class="' + homeTeamClass + '">' + pick.homeTeamAbbreviation + '</span>' +  
						'</td>';
		
		//And now we have to add in the player picks.
		//There'll be two parts: the team they picked, and the result. 
		var pickTeamClass = '';
		var pickResultClass = 'pick-cell';
		
		//If we're on the bottom row, we want them to have a bottom border.
		if (isBottomRow){
			pickTeamClass = 'pick-game-bottom';
			pickResultClass = 'pick-cell-bottom';
		}
	
		//Go through each player and add a column for each one.
		for (var playerIndex = 0; playerIndex < picksGrid.players.length; playerIndex++){
			var playerName = picksGrid.players[playerIndex];

			//We have to get the team they picked from the "playerPicks" for the game.  It has a list
			//of playerPick structs, and we just have to get the one for the player we're on to get their pick.
			var pickedTeamForPlayer = null;
			for (var pickIndex = 0; pickIndex < pick.playerPicks.length; pickIndex++){
				var playerPick = pick.playerPicks[pickIndex];
				
				if (playerPick.player == playerName){
					pickedTeamForPlayer = playerPick.pick;
					break;
				}
			}
			
			
			//If the game doesn't have a result, we just want that part to be blank.
			var doesGameHaveResult = false;
			if (isDefined(pick.winningTeamAbbreviation)){
				doesGameHaveResult = true;
			}
			
			//Assume both the team and result are blank to start off with.
			var team = '&nbsp;';
			var result = '&nbsp;';
			var winnerOrLoserClass = '';
			
			//And use the actual pick for the team if they picked somebody.
			if (isDefined(pickedTeamForPlayer)){
				team = pickedTeamForPlayer;
			}

			//If the game has a result, figure out what that result should be.
			if (doesGameHaveResult){
				//If they picked a team, then use that to decide what the result is.
				//If they didn't pick a team, it's like we're going to ignore it.
				if (isDefined(pickedTeamForPlayer)){
					//If they picked the winnig team, that's a W.
					if (pick.winningTeamAbbreviation == pickedTeamForPlayer){
						result = 'W';
					}
					//If nobody won, that's a tie.
					else if (pick.winningTeamAbbreviation == 'TIE'){
						result = 'T';
					}
					//If we're here, they picked a team, it wasn't the winner, and the game wasn't
					//a tie ... So, in conclusion: L.
					else {
						result = 'L';
					}
				}
				
				//And, since there was a result, we can add that to the player's record.
				//
				//If they didn't make a pick, that doesn't qualify as a loss.  We don't count it as 
				//anything, so there's nothing for it here.  
				//When retrieving the records for the standings, we don't count missing picks as
				//losses anymore so we shouldn't do it here.
				
				if (result == 'W'){
					winnerOrLoserClass = 'winner';
					playerRecords[playerIndex].wins++;
				}
				else if (result == 'L'){
					winnerOrLoserClass = 'loser';
					playerRecords[playerIndex].losses++;
				}
				else if (result == 'T'){
					winnerOrLoserClass = 'tie';
					playerRecords[playerIndex].ties++;
				}
			}
			
			//And now, we can the player's pick to the row.
			gameRow = gameRow + '<td class="' + pickTeamClass + '">' + 
									'<span class="' + winnerOrLoserClass + '">' + team + '</span>' + 
								'</td>' 
									+ 
								'<td class="' + pickResultClass + '">' +
									'<span class="' + winnerOrLoserClass + '">' + result + '</span>' + 
								'</td>';
		}
		
		gameRow = gameRow + '</tr>';
		
		pickRowsHtml = pickRowsHtml + gameRow;
	}

	//Now that we're basically done with the table and the picks in it, we know that we have
	//all the player records filled in, so we can make the row for them.
	var weekRecordHtml = '';
	
	//Just go through them all (they should be in the same order that the players were in),
	//and add a column for each.
	for (var index = 0; index < playerRecords.length; index++){
		var playerRecord = playerRecords[index];
		var pickRecordRowCss = 'pick-record';
		
		//The last one needs a border on the right.
		if (index + 1 >= playerRecords.length){
			pickRecordRowCss = 'last-pick-record';
		}
		
		var tiesString = '';
		if (isDefined(playerRecord.ties) && playerRecord.ties > 0){
			tiesString = ' - ' + playerRecord.ties;
		}
		
		//We want it to span 2 columns because the picks have two columns (one for the pick, one for the result).
		var playerRecordHtml = '<td colspan="2" class="' + pickRecordRowCss + '">' + playerRecord.wins + ' - ' + playerRecord.losses + tiesString + '</td>';
		weekRecordHtml = weekRecordHtml + playerRecordHtml;
	}
	
	//If year and week weren't selected, we're going to need blank cells for them 
	//on the records row.
	var blankCells = '';
	if (!yearSelected){
		//It will always be the first cell and we'll always want it to have
		//a bottom.
		blankCells = blankCells + '<td class="first-pick-cell-bottom"></td>';
	}
	
	//Same thing with the week.
	if (!weekSelected){
		//It might or might not be the first cell, though.
		var cssClassToUse = 'pick-cell-bottom';
		if (yearSelected){
			cssClassToUse = 'first-pick-cell-bottom';
		}
		
		blankCells = blankCells + '<td class="' + cssClassToUse + '"></td>';
	}
	
	//The css for the game cell might change depending on whether it's the first column
	//or not (will be if both year and week are selected).
	var blankGameCssClassToUse = 'pick-cell-bottom';
	if (yearSelected && weekSelected){
		blankGameCssClassToUse = 'first-pick-cell-bottom';
	}
	blankCells = blankCells + '<td class="' + blankGameCssClassToUse + '"></td>';
	
	//We want the blank cells in there before the current record cells.
	weekRecordHtml = '<tr>' + blankCells + weekRecordHtml + '</tr>';
	
	//And we want the week record to be the first row, so add it in before the other rows.
	var gridBodyHtml = '<tbody>' + weekRecordHtml + pickRowsHtml + '</tbody>';
	
	//Finally, complete the grid.
	var picksGridHtml = '<table class="picks-table" align="center">' + gridHeaderHtml + gridBodyHtml + '</table>';
	
	return picksGridHtml;
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
	
	var isVisible = $('#' + id).is(':visible');
	
	if (isVisible){
		$('#' + id).hide();
	}
	else {
		$('#' + id).show();
	}
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

/**
 * 
 * Creates the html for showing who's won the most weeks.  It expects the records to be
 * in the order that you want to show them.
 * 
 * The html it makes will be a standings like table that shows how many weeks a person won
 * and then a link that shows the specific weeks. 
 * 
 * @param weekRecords
 * @returns
 */
function createWeeksWonHtml(weekRecords){

	//Steps to do:
	//	1. Make the header.
	//	2. Go through every "week record" and get its rank within all the records.
	//	3. Add an entry in the table for it.
	//	4. Add the link that'll show the weeks they won too.
	
	var weeksWonHtml = '<table class="standings-table">' + 
							'<thead class="standings-table-head">' + 
								'<tr class="standings-table-row">' + 
									'<th class="standings-table-player-header"></th>' +
									'<th class="standings-table-header">Weeks won</th>' +
								'</tr>' + 
							'</thead>';
	
	var weeksWonTableBody = '';
	
	for (var index = 0; index < weekRecords.length; index++){
		var weekRecord = weekRecords[index];
		
		//The records are already in the right order, we just need to find out what the 
		//"rank" for this particular one is within the whole list.
		//A little slower than if we did it in a non-generic way, but I think it's worth
		//it to have it more "clear" and (hopefully) easier to understand.
		//
		//The first function is used to figure out where the record ranks among all the records.
		//The second is used as part of that too, but says when a record is exactly the same
		//as another (so when we see the record we're on in the list, we don't say it's a tie).
		var recordRank = rank(weekRecord, weekRecords, function(record1, record2){
			
			if (record1.weekRecords.length > record2.weekRecords.length){
				return -1;
			}
			else if (record1.weekRecords.length < record2.weekRecords.length){
				return 1;
			}
			return 0;
		}, 
		function (record1, record2){
			
			if (record1.player.name == record2.player.name){
				return true;
			}
			
			return false;
		});

		//The number we want to show is the "rank" and we want to add a "t" if it's a tie.
		var rankText = recordRank.rank;
		if (recordRank.tie){
			rankText = rankText + 't';
		}
		
		weeksWonTableBody = weeksWonTableBody + 
							'<tr class="standings-table-row">' + 
								'<td class="standings-table-player-cell">' + rankText + '. ' + weekRecord.player.name + '</td>';

		var numberOfWeeksWon = weekRecord.weekRecords.length;
		//Add some padding if so everything lines up if some records have two digits.
		if (weekRecord.weekRecords.length < 10){
			numberOfWeeksWon = numberOfWeeksWon + '&nbsp;';
		}
		
		//This is for the link that'll let them see exactly what weeks the person won.
		var detailId = 'week-records-' + index;
		var weekRecordsHtml = '<div>' + numberOfWeeksWon + 
								' <a id="show-weeks-link-' + index + '" href="javascript:" onClick="toggleShowWeeks(' + index + ')" style="margin-left: 20px; float:right;">show weeks</a>' + 
							  '</div>' + 
							  //This is the container for list of weeks they won.  We want it hidden initially and they can click the link to show it.
							  '<div id="' + detailId + '" style="display: none;"><ul class="standings-table-cell-list">';

		//If they don't select a year, there might be weeks from multiple years in the list,
		//so we'll want to show the year alongside the week.
		var selectedYear = getSelectedYear();
		var showYear = false;
		if ('all' == selectedYear){
			showYear = true;
		}
		
		//We want to make sure the weeks are in ascending order by year and week (oldest week at the top, newest
		//at the bottom.
		sortWeekRecordsBySeasonAndWeek(weekRecord.weekRecords);
		
		for (var recordIndex = 0; recordIndex < weekRecord.weekRecords.length; recordIndex++){
			var record = weekRecord.weekRecords[recordIndex];

			//Show that there were ties if there were.
			var ties = '';
			if (record.record.ties > 0){
				ties = ' - ' + record.record.ties;
			}
			
			//And add in the year at the beginning if the year wasn't selected.
			var year = '';
			if (showYear){
				year = record.season.year + ', ';
			}
			
			//The line should be like: 2016, Week 5 (10 - 5 - 1)
			var recordText = year + record.week.label + ' (' + record.record.wins + ' - ' + record.record.losses +
							 ties + ')';
			
			//And we want the link that'll show the actual picks for the person, year, and week, so they can jump directly to
			//what they picked.
			var picksLink = createPicksLink(record.week.label, record.season.year, record.week.weekNumber, null, weekRecord.player.name);
			
			weekRecordsHtml = weekRecordsHtml + '<li>' + year + picksLink + ' (' + record.record.wins + ' - ' + record.record.losses +
			 ties + ')' + '</li>';
		}
		
		weekRecordsHtml = weekRecordsHtml + '</ul></div>';
		
		weeksWonTableBody = weeksWonTableBody + '<td class="standings-table-cell">' + weekRecordsHtml + '</td></tr>';
		
	}
	
	weeksWonHtml = weeksWonHtml + '<tbody class="standings-table-body">' + weeksWonTableBody + '</tbody></table>';
	
	return weeksWonHtml;
}

/**
 * 
 * This function will get the number rank of the given object in the given list.
 * It will use the given comparison function to compare the object with other objects
 * in the given list to decide where it fits, and it'll use the given "sameObjectFunction"
 * to make sure an object doesn't "tie with itself".
 * 
 * I made it because I wanted to do it in a few places (the original standings, weeks won standings,
 * and a few other places).
 * 
 * The given list <i>doesn't</i> need to be sorted in order for it to find the object's
 * rank.  
 * 
 * It'll return an object that's like: {rank: 12, tie: true}, so people can get the
 * numeric rank and whether it ties any other object in the list.
 * 
 * Doing it this way, you'll have to call it for every object in the list ... This function
 * is O(n), so that makes ranking every object in a list O(n^2).  That kind of sucks,
 * but it should be ok because we shouldn't be sorting hundreds or thousands of objects.
 * I felt like it was ok to give up some speed to have it so each of the "standings"
 * functions not have to duplicate the ranking code.
 * 
 * @param object
 * @param list
 * @param comparisonFunction
 * @param sameObjectFunction
 * @returns
 */
function rank(object, list, comparisonFunction, sameObjectFunction){

	//Steps to do:
	//	1. Create the initial "rank" for the object and assume it has the highest rank.
	//	2. Go through each object in the list and run the comparison object on it.
	//	3. If it returns a positive number, that means the object is after the current
	//	   one we're comparing it to, so we need to up the rank.
	//	4. If it says they're the same, and the object hasn't already tied another object, 
	//	   then use the "sameObjectFunction" to see whether they're the exact same object or not.
	//	5. If they aren't, then it's a real tie.  If they aren't, then it's not.
	
	var objectRank = {rank: 1, tie: false};
	
	var numberOfRecordsBetter = 0;
	var tie = false;
	
	for (var index = 0; index < list.length; index++){
		var currentObject = list[index];
		
		var comparisonResult = comparisonFunction(object, currentObject);
		
		if (comparisonResult > 0){
			objectRank.rank++;
		}
		else if (comparisonResult == 0){

			if (objectRank.tie == false){
				var isSameObject = sameObjectFunction(object, currentObject);

				if (!isSameObject){
					objectRank.tie = true;
				}
			}
		}
	}
	
	return objectRank;
}

/**
 * 
 * A convenience function that'll sort the given weekRecords array
 * by each weekRecord's length.  A "weekRecord" will have a list
 * of records for each week inside it.  This will sort it so that
 * the one with the most weeks comes first.  
 * 
 * This is for when we're ranking how many weeks a person has won and
 * we want the person who's won the most weeks (has the most records)
 * first.
 * 
 * @param weekRecords
 * @returns
 */
function sortWeekRecords(weekRecords){
	
	//Steps to do:
	//	1. Just run the sorting function on the records we were given.
	
	weekRecords.sort(function (weekRecord1, weekRecord2){
		if (weekRecord1.weekRecords.length > weekRecord2.weekRecords.length){
			return -1;
		}
		else if (weekRecord1.weekRecords.length < weekRecord2.weekRecords.length){
			return 1;
		}
		return 0;
	});
}

/**
 * 
 * This function will sort the given week records by season and week
 * so that the "oldest" records appear first.  It's here for when we're showing
 * how many weeks a person one and we want to show the weeks in chronological
 * order.  This function will make sure they're in chronological order.
 * 
 * @param weekRecords
 * @returns
 */
function sortWeekRecordsBySeasonAndWeek(weekRecords){
	
	//Steps to do:
	//	1. Just run the sorting function on the array 
	//	   we were given.
	
	weekRecords.sort(function (weekRecord1, weekRecord2){
		var year1 = parseInt(weekRecord1.season.year);
		var year2 = parseInt(weekRecord2.season.year);
		
		//If the year from one is before the other, we want the earlier one first.
		if (year1 < year2){
			return -1;
		}
		//And later one second.
		else if (year1 > year2){
			return 1;
		}
		else {
			//Otherwise, compare on the weeks.
			var week1 = weekRecord1.week.weekNumber;
			var week2 = weekRecord2.week.weekNumber;
			
			//With the earlier week first.
			if (week1 < week2){
				return -1;
			}
			else if (week1 > week2){
				return 1;
			}
		}
		
		return 0;
	});
}

/**
 * 
 * This function will say whether a "specific" year was selected
 * (basically if the year isn't "all").
 * 
 * @returns
 */
function isSpecificYearSelected(){

	var selectedYear = getSelectedYear();
	
	if ('all' == selectedYear){
		return false;
	}
	
	return true;
}

/**
 * 
 * This function will say whether a "specific" team was selected
 * (basically if the team isn't "all").
 * 
 * @returns
 */
function isSpecificTeamSelected(){
	
	var selectedTeam = getSelectedTeam();
	
	if ('all' == selectedTeam){
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

	var selectedWeek = getSelectedWeek();
	
	if ('all' == selectedWeek || 'regular-season' == selectedWeek || 'playoffs' == selectedWeek){
		return false;
	}
	
	return true;
}

/**
 * 
 * This function will create the html that shows the weekly records
 * for a player.  It expects the given records to be sorted in the order
 * they should be shown in, and it assumes they're all for the same player.
 * 
 * @param weekRecords
 * @returns
 */
function createWeekRecordsByPlayerHtml(weekRecords){
	
	//Steps to do:
	//	1. Make the header (add in one for ties and a year if they haven't picked one).
	//	2. Go through and output each record and that's pretty much it.
	
	var tiesHeader = '';
	
	var areThereAnyTies = false;
	for (var index = 0; index < weekRecords.length; index++){
		var weekRecord = weekRecords[index];
		
		if (weekRecord.record.ties > 0){
			areThereAnyTies = true;
			break;
		}
	}
	
	if (areThereAnyTies){
		tiesHeader = '<th class="standings-table-header">T</th>';
	}
	
	var yearHeader = '';
	var weekClass = 'standings-table-player-header';
	var aYearIsSelected = isSpecificYearSelected();
	if (!aYearIsSelected){
		yearHeader = '<th class="standings-table-player-header">Year</th>';
		weekClass = 'standings-table-header';
	}
	
	var tableHead = '<thead class="standings-table-head">' + 
						'<tr class="standings-table-row">' + 
							yearHeader +
							'<th class="' + weekClass + '">Week</th>' + 
							'<th class="standings-table-header">W</th>' + 
							'<th class="standings-table-header">L</th>' +
							tiesHeader +
							'<th class="standings-table-header">%</th>' +
						'</tr>' +
					'</thead>';
	
	var tableBody = '<tbody class="standings-table-body">';
	
	for (var index = 0; index < weekRecords.length; index++){
		var weekRecord = weekRecords[index];
		
		var yearCell = '';
		if (!aYearIsSelected){
			yearCell = '<td class="standings-table-player-cell">' + weekRecord.season.year + '</td>';
		}
		
		var weekLabel = shortenWeekLabel(weekRecord.week.label);
		
		var playerPicksLink = createPicksLink(weekLabel, weekRecord.season.year, weekRecord.week.weekNumber, null, weekRecord.player.name);

		var tiesCell = '';
		//Add a cell for ties if there are any.
		if (areThereAnyTies){
			tiesCell = '<td class="standings-table-cell">' + weekRecord.record.ties + '</td>';
		}
		
		//And we want to show the winning percentage too.
		var percentageString = getWinningPercentage(weekRecord.record.wins, weekRecord.record.losses);
		
		var row = '<tr class="standings-table-row">' +
					yearCell +
					'<td class="' + weekClass + '">' + playerPicksLink + '</td>' +
					'<td class="standings-table-cell">' + weekRecord.record.wins + '</td>' +
					'<td class="standings-table-cell">' + weekRecord.record.losses + '</td>' +
					tiesCell +
					'<td class="standings-table-cell">' + percentageString + '</td>'
				  '</tr>';
		
		tableBody = tableBody + row;
	}
	
	tableBody = tableBody + '</tbody>';

	var weekRecordsByPlayerHtml = '<table class="standings-table">' + tableHead + tableBody + '</table>';
	
	return weekRecordsByPlayerHtml;
}

/**
 * 
 * This function will get the winning percentage.  Here because we almost always want
 * it formatted the same way, so I figured it was better to do it in a function.
 * 
 * @param wins
 * @param losses
 * @returns
 */
function getWinningPercentage(wins, losses){
	
	//Steps to do:
	//	1. Get the actual percentage.
	//	2. Make it 3 decimal places if it's a real number and blank if it's not.
	
	var percentage = wins / (wins + losses);
	
	var percentageString = '';
	if (!isNaN(percentage)){
		percentageString = percentage.toPrecision(3);
	}
	
	return percentageString;
}

/**
 * 
 * Shows the best weekly records in a table.  Here so we can see the best
 * records over a year, or like over all time.  The given records should
 * be sorted in the order we want them shown in. 
 * 
 * @param playerWeekRecords
 * @returns
 */
function createWeekStandingsHtml(playerWeekRecords){

	//Steps to do:
	//	1. Make the header (add in the year and week if they aren't selected and
	//	   a header for ties if they are).
	//	2. Go through and add a row for each record with a link to the picks
	//	   for the week.
	//	3. That's pretty much it.
	
	var standingsHtml = '';
	
	var isYearSelected = isSpecificYearSelected();
	var yearHeader = '';
	if (!isYearSelected){
		yearHeader = '<th class="standings-table-header">Year</th>';
	}
	
	var isWeekSelected = isSpecificWeekSelected();
	var weekHeader = '';
	if (!isWeekSelected){
		isWeekSelected = false;
		weekHeader = '<th class="standings-table-header">Week</th>';
	}
	
	var areThereAnyTies = false;
	for (var index = 0; index < playerWeekRecords.length; index++){
		var playerWeekRecord = playerWeekRecords[index];
		
		if (playerWeekRecord.record.ties > 0){
			areThereAnyTies = true;
		}
	}
	
	var tiesHeader = '';
	if (areThereAnyTies){
		tiesHeader = '<th class="standings-table-header-small">T</th>';
	}
	
	var standingsHeaderHtml = '<thead class="standings-table-head">' +
						 	'<th class="standings-table-player-header"></th>' +
						 	yearHeader + 
						 	weekHeader +
						 	'<th class="standings-table-header-small">W</th>' + 
						 	'<th class="standings-table-header-small">L</th>' +
						 	tiesHeader + 
						 	'<th class="standings-table-header-small">%</th>';
	
	
	standingsHeaderHtml = standingsHeaderHtml + '</thead>';
	
	var rowsHtml = '';
	
	if (isEmpty(playerWeekRecords)){
		rowsHtml = '<tr><td colspan="5" style="text-align: center;">No results</td></tr>';
	}
	
	//Going to keep this "old school" since doing it "new school" would mean O(n^) for 
	//getting a record's rank ... and we could have hundreds of records (since there might be a
	//lot of players, with 17 * number of years worth of weeks for each player.

	//The steps for calculating the rank:
	//	1. Have three variables: rank, nextRank, and tieIndependentRank.
	//	2. rank holds the rank of the current record we're on.  
	//	3. nextRank holds what the rank should be the next time we go through
	//	   the loop.
	//	4. tieIndependentRank holds the rank independent of ties.  Basically what it would be if
	//	   there were no ties (the position of the record in the array, starting at 1).
	//	5. Start the nextRank at 1 because that's what the rank of the next record we see will be.
	//	6. Start going through the records.
	//	7. Assign the nextRank that we calculated to the rank so that we use it for this record.
	//	8. Calculate the nextRank:
	//		1. If there's a next record and it has the same number of wins and losses as this one, then
	//		   the nextRank will be same as the current rank because there's a tie.
	//		2. Otherwise, it'll be whatever "tieIndepdentedRank" we have.  That's because we'll
	//		   want to basically pick up where we left off before the ties started.
	
	var rank = null;
	var nextRank = 1;
	var nextRecord = null;
	var previousRank = null;
	
	for (var index = 0; index < playerWeekRecords.length; index++){
		var playerWeekRecord = playerWeekRecords[index];
		
		//This is the position of the record independent of whether there are ties.  Just the "raw" position if we
		//started counting at 1.  It will be the same as the rank if there aren't any ties.
		var tieIndependentRank = index + 1;
		//Set the rank to what we calculated it should be the previous time through the loop.
		rank = nextRank;
		
		//Now, need to calculate what it will be the next time.
		//If the next record has the same number of wins and losses, then it'll be the same as now because they're
		//tied.
		//Otherwise, if the next record doesn't, the next rank will be whatever this one's would have
		//been without ties + 1.  If there weren't any ties, then this record's rank would be the "tieIndependentRank".
		//So, that means the next rank would be that + 1.
		nextRecord = null;
		if (index + 1 < playerWeekRecords.length){
			nextRecord = playerWeekRecords[index + 1];
			
			if (playerWeekRecord.record.wins == nextRecord.record.wins && playerWeekRecord.record.losses == nextRecord.record.losses){
				//rank stays the same.
			}
			else {
				//current rank would be index + 1.  We want to be one beyond that.
				nextRank = tieIndependentRank + 1;
			}
		}
		
		//Now, we have the rank and next rank so we need to figure out if we need to put a little 't' to indicate
		//there was a tie.
		//There's a tie if:
		//	1. It's the same as the next rank and we're not at the end.
		//	2. The rank is the same as the previous rank.
		//
		//Number 1 should be pretty straight forward.  If this rank is the same as the next one, it's in a tie.
		//Number 2 is there for the last tie in a series of ties.  The last tie will have a "nextRank" that's different from
		//what it is, but we'll still want to show a tie for it.  So, in that case, we can just look to see if it's the same
		//as the previous rank and, if it is, we know there's a tie.
		var rankText = rank + '';
		if ((nextRank == rank && index + 1 < playerWeekRecords.length) || (rank == previousRank)){
			rankText = rankText + 't';
		}
		
		//Add in the year and week if they weren't selected.
		var yearCell = '';
		if (!isYearSelected){
			yearCell = '<td class="standings-table-cell">' + playerWeekRecord.season.year + '</td>';
		}
		
		var weekCell = '';
		if (!isWeekSelected){
			var labelToUse = shortenWeekLabel(playerWeekRecord.week.label);
			//function createPicksLink(linkText, year, week, team, player){
			var picksLink = createPicksLink(labelToUse, playerWeekRecord.season.year, playerWeekRecord.week.weekNumber, null, playerWeekRecord.player.name);
			weekCell = '<td class="standings-table-cell">' + picksLink + '</td>';
		}
		
		var tiesCell = '';
		if (areThereAnyTies){
			tiesCell = '<td class="standings-table-cell-small">' + playerWeekRecord.record.ties + '</td>';
		}
		
		var percentageString = getWinningPercentage(playerWeekRecord.record.wins, playerWeekRecord.record.losses);
		
		rowsHtml = rowsHtml + 
					   '<tr class="standings-table-row">' +
						'<td class="standings-table-player-cell">' + rankText + '. ' + playerWeekRecord.player.name + '</td>' +
						yearCell +
						weekCell +
						'<td class="standings-table-cell-small">' + playerWeekRecord.record.wins + '</td>' +
						'<td class="standings-table-cell-small">' + playerWeekRecord.record.losses + '</td>' +
						tiesCell + 
						'<td class="standings-table-cell-small">' + percentageString + '</td>';
		
		rowsHtml = rowsHtml + '</tr>';
		
		//Keep the current rank as the previous for the next time through.
		previousRank = rank;
		
	}
	
	var standingsBodyHtml = '<tbody class="standings-table-body">' + rowsHtml + '</tbody>';
	
	standingsHtml = '<table class="standings-table">' + standingsHeaderHtml + standingsBodyHtml + '</table>';
	
	return standingsHtml;
}

/**
 * 
 * This one will create the html for showing all the champions and when they won them.
 * The given array should be sorted in the order that we want it shown in.
 * 
 * @param championships
 * @returns
 */
function createChampionsHtml(championships){
	
	//Steps to do:
	//	1. Make the header and add ties if there are any.
	//	2. Go through and add a row for each championship.
	//	3. That's it.
	
	var areThereAnyTies = false;
	for (var index = 0; index < championships.length; index++){
		
		var championship = championships[index];
		
		if (championship.record.ties > 0){
			areThereAnyTies = true;
			break;
		}
	}
	
	var tiesHeader = '';
	if (areThereAnyTies){
		tiesHeader = '<th class="standings-table-header">T</th>';
	}
	
	var championshipsHeaderHtml = '<thead class="standings-table-head">' +
								  	'<th class="standings-table-player-header"></th>' + 
								  	'<th class="standings-table-header">Year</th>' + 
								  	'<th class="standings-table-header">W</th>' +
								  	'<th class="standings-table-header">L</th>' +
								  	tiesHeader + 
								  	'<th class="standings-table-header">%</th>'
								  '</thead>';
	
	var championshipsBodyHtml = '<tbody>';
	
	for (var index = 0; index < championships.length; index++){
		var championship = championships[index];
		
		var tiesCell = '';
		if (areThereAnyTies){
			tiesCell = '<td class="standings-table-cell">' + championship.record.ties + '</td>';
		}
		
		var percentageString = getWinningPercentage(championship.record.wins, championship.record.losses);
		
		var championshipRowHtml = '<tr class="standings-table-row">' + 
								  	'<td class="standings-table-player-cell">' + championship.player.name + '</td>' +
								  	'<td class="standings-table-cell">' + championship.season.year + '</td>' +
								  	'<td class="standings-table-cell">' + championship.record.wins + '</td>' +
								  	'<td class="standings-table-cell">' + championship.record.losses + '</td>' + 
								  	tiesCell +
								  	'<td class="standings-table-cell">' + percentageString + '</td>' +
								  '</tr>';
		
		championshipsBodyHtml = championshipsBodyHtml + championshipRowHtml;
	}
	
	championshipsBodyHtml = championshipsBodyHtml + '</tbody>';
	
	var championshipsHtml = '<table class="standings-table">' + championshipsHeaderHtml + championshipsBodyHtml + '</table>';
	
	return championshipsHtml;
}

/**
 * 
 * This one will create the html for showing who has the most championships.  It doesn't
 * expect the given array to be sorted.  Each entry in the array will be the player with
 * all the championships they have (not just the count).  We want the whole thing
 * because we want to show the years and records.
 * 
 * @param playerChampionshipsList
 * @returns
 */
function createChampionshipStandingsHtml(playerChampionshipsList){
	
	//Steps to do:
	//	1. Make the header.
	//	2. Make sure they're in the right order by who has the most titles.
	//	3. Make a row in the standings for each person and their titles.
	//	4. That's it.
	
	var championshipsStandingsHeaderHtml = '<thead class="standings-table-head">' +
										  	'<tr class="standings-table-row">' +
										  		'<th class="standings-table-player-header"></th>' +
										  		'<th class="standings-table-header">Championships</th>' +
										  		'<th class="standings-table-header">Years</th>' +
										  	'</tr>' +
										  '</thead>';
										  		
	
	var championshipsStandingsBodyHtml = '<tbody class="standings-table-body">';
	
	//Each item in the list is the player and all another list that has their championships in it.
	//We want the list sorted so that the person with the most championships comes first.
	playerChampionshipsList.sort(function (playerChampionships1, playerChampionships2){
		
		//More championships should come first.
		if (playerChampionships1.championships.length > playerChampionships2.championships.length){
			return -1;
		}
		//Fewer should come last.
		else if (playerChampionships1.championships.length < playerChampionships2.championships.length){
			return 1;
		}
		
		return 0;
	});
	
	//Now that they're sorted, we just have to make a row for each one.
	for (var index = 0; index < playerChampionshipsList.length; index++){
		var playerChampionships = playerChampionshipsList[index];
		
		//And we want each one's rank before we make the row.
		//The rank is based on how many championships a person has, so that's why the first function compares
		//on that.
		var championshipsRank = rank(playerChampionships, playerChampionshipsList, function(playerChampionships1, playerChampionships2){
			
			if (playerChampionships1.championships.length > playerChampionships2.championships.length){
				return -1;
			}
			else if (playerChampionships1.championships.length < playerChampionships2.championships.length){
				return 1;
			}
			
			return 0;
		}, 
		
		function (playerChampionships1, playerChampionships2){
			
			if (playerChampionships1.player.name == playerChampionships2.player.name){
				return true;
			}
			
			return false;
		});

		var rankText = championshipsRank.rank;
		if (championshipsRank.tie){
			rankText = rankText + 't';
		}
		
		var playerChampionshipsRowHtml = '<tr class="standings-table-row">' + 
											'<td class="standings-table-player-cell">' + rankText + '. ' + playerChampionships.player.name + '</td>' +
											'<td class="standings-table-cell">' + playerChampionships.championships.length + '</td>';
		
		//We want to show all the years they won too, so add them as a list.
		var championshipYearsHtml = '<ul class="standings-table-cell-list">';

		for (var championshipIndex = 0; championshipIndex < playerChampionships.championships.length; championshipIndex++){
			var championship = playerChampionships.championships[championshipIndex];
			
			var tiesString = '';
			if (championship.record.ties > 0){
				tiesString = ' - ' + championship.record.ties;
			}
			
			var championshipHtml = '<li>' + championship.season.year + ' (' + championship.record.wins + ' - ' + championship.record.losses + tiesString + ')';
			
			championshipYearsHtml = championshipYearsHtml + championshipHtml;
		}
		
		championshipYearsHtml = championshipYearsHtml + '</ul>';
		
		playerChampionshipsRowHtml = playerChampionshipsRowHtml + '<td class="standings-table-cell">' + championshipYearsHtml + '</td></tr>';
		
		championshipsStandingsBodyHtml = championshipsStandingsBodyHtml + playerChampionshipsRowHtml;
	}
	
	championshipsStandingsBodyHtml = championshipsStandingsBodyHtml + '</tbody>';
	
	var championshipsStandingsHtml = '<table class="standings-table">' + championshipsStandingsHeaderHtml + championshipsStandingsBodyHtml + '</table';
	
	return championshipsStandingsHtml;
}

/**
 * 
 * This function will create a table that shows who won each week in sequential order.
 * If there's a tie, it'll show all the people who tied as having won the week.
 * 
 * @param weeksWonByWeek
 * @returns
 */
function createWeeksWonByWeek(weeksWonByWeek){

	//Steps to do:
	//	1. Make the header (and add the year in if we have it).
	//	2. Add in a row for each week and a list for each player who won
	//	   that week.
	//	3. That's it.
	
	var yearSelected = isSpecificYearSelected();
	
	var yearHeader = '';
	if (!yearSelected){
		yearHeader = '<th class="standings-table-header">Year</th>';
	}
	
	var weeksWonByWeekHeaderHtml = '<thead class="standings-table-head">' +
								   		'<tr>' + 
								   			yearHeader + 
								   			'<th class="standings-table-header">Week</th>' +
								   			'<th class="standings-table-header">Record</th>' +
								   			'<th class="standings-table-header">Winner</th>' +
								   		'</tr>' +
								   	'</thead>';
	
	var weeksWonByWeekBodyHtml = '<tbody class="standings-table-body">';
	
	for (var index = 0; index < weeksWonByWeek.length; index++){
		var weekRecord = weeksWonByWeek[index];
	
		var yearCell = '';
		if (!yearSelected){
			yearCell = '<td class="standings-table-cell">' + weekRecord.season.year + '</td>';
		}
		
		var recordHtml = weekRecord.record.wins + ' - ' + weekRecord.record.losses;
		
		if (weekRecord.record.ties > 0){
			recordHtml = recordHtml + ' - ' + weekRecord.record.ties;
		}
		
		//We want to show the players in a consistent order, so sort them by name.
		sortPlayersByName(weekRecord.players);
		
		var playerHtml = '<ul class="standings-table-cell-list">';
		for (var playerIndex = 0; playerIndex < weekRecord.players.length; playerIndex++){
			var player = weekRecord.players[playerIndex];
			
			var playerPicksLink = createPicksLink(player.name, weekRecord.season.year, weekRecord.week.weekNumber, null, player.name);
			
			var plHtml = '<li>' + playerPicksLink + '</li>';
			playerHtml = playerHtml + plHtml;
		}
		
		playerHtml = playerHtml + '</ul>';

		var weeksWonByWeekRow = '<tr class="standings-table-row">' +
									yearCell +
									'<td class="standings-table-cell">' + shortenWeekLabel(weekRecord.week.label) + '</td>' +
								    '<td class="standings-table-cell">' + recordHtml + '</td>' +
								    '<td class="standings-table-cell">' + playerHtml + '</td>' +
								'</tr>';
		
		weeksWonByWeekBodyHtml = weeksWonByWeekBodyHtml + weeksWonByWeekRow;
	}
	
	weeksWonByWeekBodyHtml = weeksWonByWeekBodyHtml + '</tbody>';
	
	var weeksWonByWeekHtml = '<table class="standings-table">' + weeksWonByWeekHeaderHtml + weeksWonByWeekBodyHtml + '</table>';
	
	return weeksWonByWeekHtml;
}

/**
 * 
 * A convenience function for sorting players by their name.  Here in case
 * we do it in multiple places.
 * 
 * @param players
 * @returns
 */
function sortPlayersByName(players){
	
	//Steps to do:
	//	1. Just compare each player by its name.
	
	players.sort(function (player1, player2){
		
		if (player1.name < player2.name){
			return -1;
		}
		else if (player1.name > player2.name){
			return 1;
		}
		
		return 0;
	});
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
 * This function will create the html table that shows how accurate people's picks are.
 * Here so people can see if they suck with certain teams.  It doesn't expect the given
 * summaries to be sorted, so it'll do the sorting.
 * 
 * @param pickAccuracySummaries
 * @returns
 */
function createPickAccuracySummariesHtml(pickAccuracySummaries){

	//Steps to do:
	//	1. Make the header.
	//	2. Make sure they're sorted in the right order (by how many times somebody was right).
	//	3. Go through and add a row for each team and its accuracy.
	//	4. Add a detail link that shows more details (how many times picked to win, lose, ...).
	
	var specificTeamSelected = isSpecificTeamSelected();
	var teamHeader = '';
	if (!specificTeamSelected){
		teamHeader = '<th class="standings-table-header">Team</th>';
	}
	
	var pickAccuracySummariesHeadHtml = '<thead class="standings-table-head">' +
											'<tr class="standings-table-row">' +
												teamHeader +
												'<th class="standings-table-header">Right</th>' +
												'<th class="standings-table-header">Wrong</th>' + 
												'<th class="standings-table-header">%</th>' +
												'<th class="standings-table-header"></th>' +
											'</tr>' +
										'</thead>';
	
	var pickAccuracySummariesBodyHtml = '<tbody class="standings-table-body">';
	
	sortPickAccuracySummariesByTimesRight(pickAccuracySummaries);
	
	for (var index = 0; index < pickAccuracySummaries.length; index++){
		var pickAccuracySummary = pickAccuracySummaries[index];
		
		//Add in the team if they didn't pick a specific one.
		var teamCell = '';
		if (!specificTeamSelected){
			teamCell = '<td class="standings-table-cell">' + pickAccuracySummary.team.abbreviation + '</td>';
		}
		
		var percentage = getWinningPercentage(pickAccuracySummary.timesRight, pickAccuracySummary.timesWrong);

		var detailId = 'pick-accuracy-details-' + index;

		var tiesHtml = '';
		
		var timesPickedToWin = pickAccuracySummary.timesPickedToWinRight + pickAccuracySummary.timesPickedToWinWrong;
		var timesPickedToLose = pickAccuracySummary.timesPickedToLoseRight + pickAccuracySummary.timesPickedToLoseWrong;

		var hasTies = false;
		var tiesRecord = '';
		if (pickAccuracySummary.actualTies > 0){
			hasTies = true;
			tiesRecord = ' - ' + pickAccuracySummary.actualTies;
		}

		var year = getSelectedYear();
		//Add in a link so they can see the exact picks they made with the team.
		var recordPicksLink = createPicksLink('Picks', year, null, pickAccuracySummary.team.abbreviation, pickAccuracySummary.player.name);
		
		//And add the detail that says how many times they picked them to win and lose, and how many times they were right
		//with each.
		var detailHtml = '<tr id="' + detailId + '" style="display: none;">' +
						    '<td class="standings-table-cell" colspan="5">' + 
							    '<table style="width: 100%;">' +
							 		'<tr><td>Actual record</td><td style="text-align: right;">' + pickAccuracySummary.actualWins + ' - ' + pickAccuracySummary.actualLosses + tiesRecord + '</td></tr>' +
							 		'<tr><td>Predicted record</td><td style="text-align: right;">' + pickAccuracySummary.predictedWins + ' - ' + pickAccuracySummary.predictedLosses + '</td></tr>' +
							 		'<tr><td>Times picked to win (record)</td><td style="text-align: right;">' + timesPickedToWin + ' (' + pickAccuracySummary.timesPickedToWinRight + ' - ' + pickAccuracySummary.timesPickedToWinWrong + ')</td></tr>' +
							 		'<tr><td>Times picked to lose (record)</td><td style="text-align: right;">' + timesPickedToLose + ' (' + pickAccuracySummary.timesPickedToLoseRight + ' - ' + pickAccuracySummary.timesPickedToLoseWrong + ')</td></tr>' +
							 		'<tr><td>&nbsp;</td><td style="text-align: right;">' + recordPicksLink + '</tr></td>' +
							 	'</table>' + 
						 	'</td>' + 
						 '</tr>';
		
		var pickAccuracySummaryRowHtml = '<tr>' +
											teamCell + 
											'<td class="standings-table-cell">' + pickAccuracySummary.timesRight + '</td>' +
											'<td class="standings-table-cell">' + pickAccuracySummary.timesWrong + '</td>' +
											'<td class="standings-table-cell">' + percentage + '</td>' +
											'<td class="standings-table-cell">' + 
												'<a id="pick-accuracy-details-link-' + index + '" href="javascript:" onClick="toggleShowPickAccuracyDetails(' + index + ')" style="margin-left: 20px; float:right;">show details</a>' + 
											'</td>' +
										 '</tr>' + 
										 detailHtml;
		
		pickAccuracySummariesBodyHtml = pickAccuracySummariesBodyHtml + pickAccuracySummaryRowHtml;
	}
	
	pickAccuracySummariesBodyHtml = pickAccuracySummariesBodyHtml + '</tbody>';
	
	var pickAccuracySummariesHtml = '<table class="standings-table">' + pickAccuracySummariesHeadHtml + pickAccuracySummariesBodyHtml + '</table>';
	
	return pickAccuracySummariesHtml;
}

/**
 * 
 * Here so we can make sure the pick accuracies are in the right order (with the most
 * accuracte team coming first).
 * 
 * @param pickAccuracySummaries
 * @returns
 */
function sortPickAccuracySummariesByTimesRight(pickAccuracySummaries){
	
	//Steps to do:
	//	1. Just sort them by how many times the person was right picking a team.
	
	pickAccuracySummaries.sort(function (pickAccuracySummaryA, pickAccuracySummaryB){

		//More times right = front of the list.
		if (pickAccuracySummaryA.timesRight > pickAccuracySummaryB.timesRight){
			return -1;
		}
		//Fewer times right = back of the list.
		else if (pickAccuracySummaryA.timesRight < pickAccuracySummaryB.timesRight){
			return 1;
		}
		
		return 0;
		
	});
}

/**
 * 
 * Shows or hides the details for pick accuracy.  If it's currently shown, it'll hide
 * it and if it's currently hidden, it'll show it.
 * 
 * @param index
 * @returns
 */
function toggleShowPickAccuracyDetails(index){
	
	//Steps to do:
	//	1. Get whether the container at the index is shown.
	//	2. Hide it if it is, show it if it's not.
	
	var isVisible = $('#pick-accuracy-details-' + index).is(':visible');
	
	if (isVisible){
		$('#pick-accuracy-details-' + index).hide();
		$('#pick-accuracy-details-link-' + index).text('show details');
	}
	else {
		$('#pick-accuracy-details-' + index).show();
		$('#pick-accuracy-details-link-' + index).text('hide details');
	}
}

/**
 * 
 * This function will create a link that'll take them to the picks for the given
 * year, week, team, and player (all optional) and show the given text.  Here because
 * we wanted to do this in a few places, so I figured it was best to do it as a function.
 * 
 * @param linkText
 * @param year
 * @param week
 * @param team
 * @param player
 * @returns
 */
function createPicksLink(linkText, year, week, team, player){
	
	//Steps to do:
	//	1. Just make a link that'll call the javascript function
	//	   that actually updates the view.
	//	2. All the arguments are optional, so only add them in if
	//	   they're given.
	
	var picksLink = '<a href="javascript:" onClick="showPickView(';
	
	if (isDefined(year)){
		picksLink = picksLink + '\'' + year + '\', ';
	}
	else {
		picksLink = picksLink + 'null, ';
	}
	
	if (isDefined(week)){
		picksLink = picksLink + '\'' + week + '\', ';
	}
	else {
		picksLink = picksLink + 'null, ';
	}
	
	if (isDefined(team)){
		picksLink = picksLink + '\'' + team + '\', ';
	}
	else {
		picksLink = picksLink + 'null, ';
	}
	
	if (isDefined(player)){
		picksLink = picksLink + '\'' + player + '\'';
	}
	else {
		picksLink = picksLink + 'null';
	}
	
	picksLink = picksLink + ');">' + linkText + '</a>';
	
	return picksLink;
}

/**
 * 
 * This function will show the picks grid for the given year, week, team, and player.
 * All the arguments are optional.  It will just set each one as the selected
 * year, week, team, and player (if it's given) and then cause the picks to be shown.
 * 
 * It'll flip the global "havePicksBeenShown" switch to true so that the view shows
 * all the picks for the given parameters and doesn't try to overrule it and only show
 * a week's worth of picks.
 * 
 * @param year
 * @param week
 * @param team
 * @param player
 * @returns
 */
function showPickView(year, week, team, player){

	//Steps to do:
	//	1. If we're coming from this function, then we don't want
	//	   the updatePicks function saying "no, you can't see all the picks",
	//	   so we need to flip the switch that disables that feature.
	//	2. Set all the parameters that were given.
	//	3. Call the function that'll show them on the screen.
	
	//If this switch is true, we'll show the picks for the parameters no matter
	//whether it's a week's worth or not.  If it's not, it'll show only a week's
	//worth as a way to prevent accidentally showing all the picks (which takes a while to do).
	NFL_PICKS_GLOBAL.havePicksBeenShown = true;
	
	setSelectedType('picks');
	
	if (isDefined(year)){
		setSelectedYear(year);
	}
	
	if (isDefined(week)){
		setSelectedWeek(week);
	}
	
	if (isDefined(player)){
		setSelectedPlayer(player);
	}
	
	if (isDefined(team)){
		setSelectedTeam(team);
	}
	
	updateView();
}

/**
 * 
 * This function will shorten the given label so it's easier
 * to show on phones and stuff with small widths.  Up yours, twitter
 * and bootstrap.
 * 
 * @param label
 * @returns
 */
function shortenWeekLabel(label){
	
	if ('Conference Championship' == label){
		return 'Conf Champ';
	}
	
	return label;
}

/**
 * Here to hold the current weeks games so we can reference them when showing the picks
 * that people make.
 * 
 * I think this is the only global "non-global" variable
 */
//var currentMakePicksGames = null;

/**
 * 
 * This function will get the picks for the current week so that people
 * can "make" their own picks easier.
 * 
 * @returns
 */
function updateMakePicks(){
	
	//Steps to do:
	//	1. Send the request to the server.
	//	2. Keep the current games around in case we want to use them later.
	//	3. Make the html table.
	//	4. Show it as the content.
	
	$.ajax({url: 'nflpicks?target=makePicks',
			contentType: 'application/json; charset=UTF-8'}
	)
	.done(function(data) {
		var gamesForNextWeek = $.parseJSON(data);

		NFL_PICKS_GLOBAL.gamesForCurrentWeek = gamesForNextWeek;
		
		var picksGridHtml = createMakePicksGrid(gamesForNextWeek);
		
		$('#contentContainer').empty();
		$('#contentContainer').append(picksGridHtml);
	})
	.fail(function() {
	})
	.always(function() {
	});
	
}

/**
 * 
 * This function will make a grid that makes it so they can make picks for all
 * the given games.  The actual picks will just go in a text area, but that's better
 * than nothing!
 * 
 * When they make a pick, it'll call the "updatePickedPicks" function and that'll update
 * a text area with the picks down below.
 * 
 * @param games
 * @returns
 */
function createMakePicksGrid(games){
	
	//Steps to do:
	//	1. Make the header.
	//	2. Add a row for each game.
	//	3. That's it.
	
	var picksGridHtml = '';
	
	var gridHeaderHtml = '<thead>' +
						 	'<th align="left" class="table-header">Game</th>' + 
						 	'<th class="table-header">Pick</th>' + 
						 '</thead>';
	
	var pickRowsHtml = '';
	
	for (var index = 0; index < games.length; index++){
		var game = games[index];
		
		var rowClassName = 'even-row';
		
		if (index % 2 == 1){
			rowClassName = 'odd-row';
		}

		//Here so the borders are right.
		var pickGameClass = 'edit-pick-game';
		var pickTeamClass = 'edit-pick-team';
		
		if (index + 1 >= games.length){
			pickGameClass = 'edit-pick-last-game';
			pickTeamClass = 'edit-pick-last-team';
		}
		
		var gameRow = '<tr class="' + rowClassName + '">' + 
						'<td class="' + pickGameClass + '">' + 
							'<span>' + game.awayTeam.abbreviation + '</span>' + 
							' @ ' + 
							'<span>' + game.homeTeam.abbreviation + '</span>' +  
						'</td>';
	
		var gameId = game.id;
		var options = [{label: '', value: '0'},
		               {label: game.homeTeam.abbreviation, value: game.homeTeam.id},
		               {label: game.awayTeam.abbreviation, value: game.awayTeam.id}];
		var selectPickId = 'pick-' + game.id;

		//When they change a pick, call the "updatePickedPicks" function to update how we show what they picked.
		var selectPickHtml = createSelectHtml(selectPickId, options, null, 'edit-pick-select', null, 'updatePickedPicks()');
					
		gameRow = gameRow + '<td class="' + pickTeamClass + '">' + 
								selectPickHtml + 
							'</td>' +
				  '</tr>';
		
		pickRowsHtml = pickRowsHtml + gameRow;
	}

	var gridBodyHtml = '<tbody>' + pickRowsHtml + '</tbody>';
	
	picksGridHtml = '<table class="edit-picks-table" align="center">' + gridHeaderHtml + gridBodyHtml + '</table>' +
						'<div id="missing-picks-container" style="text-align:center; padding-top: 15px;"></div>' + 
						'<div style="margin-top: 20px; margin-bottom: 40px; text-align: center;">' +
							'<textarea id="picked-picks" style="width: 300px; height: 100px;">&nbsp;</textarea>' + 
						'</div>';
	
	picksGridHtml = '<div style="text-align: center;"><p>The teams you pick will go in a box at the bottom.  Copy and paste it into a text to pick the games.</p><p style="font-weight:bold;">Just picking them without sending them to me doesn\'t do jack squat.</p><p>Happy now, Jerry and Benny boy?</p></div>' + picksGridHtml;
	
	return picksGridHtml;
}

/**
 * 
 * This function will update the picks that they made by adding them to the
 * text area so they can be easily copied and pasted.  It'll go through
 * the current games in the NFL_PICKS_GLOBAL.gamesForCurrentWeek array
 * and get the pick for each one, and then add that to the text area.
 * 
 * @returns
 */
function updatePickedPicks(){
	
	//Steps to do:
	//	1. Go through each game in the week and get the picked they picked for it.
	//	2. If they didn't pick a team, that's a missing pick, so keep track of that.
	//	3. Get which team they picked and show the abbreviation for that team in the
	//	   output.
	//	4. Update the text area with what they picked and what picks are missing.
	
	var pickedPicksUpdate = '';
	var missingPicksString = '';
	var numberOfMissingPicks = 0;
	
	for (var index = 0; index < NFL_PICKS_GLOBAL.gamesForCurrentWeek.length; index++){
		var game = NFL_PICKS_GLOBAL.gamesForCurrentWeek[index];
		
		var selectedPick = getSelectedPick(game.id);
		
		var abbreviation = '';
		if (game.homeTeam.id == selectedPick){
			abbreviation = game.homeTeam.abbreviation;
		}
		else if (game.awayTeam.id == selectedPick){
			abbreviation = game.awayTeam.abbreviation;
		}
		//If we're here, they didn't pick anything for that game, so add it to the list.
		else {
			numberOfMissingPicks++;
			if (missingPicksString != ''){
				missingPicksString = missingPicksString + ', ';
			}
			
			missingPicksString = missingPicksString + game.awayTeam.abbreviation + ' @ ' + game.homeTeam.abbreviation;
		}
		
		if (abbreviation != ''){
			
			if ('' != pickedPicksUpdate){
				pickedPicksUpdate = pickedPicksUpdate + ', ';
			}

			pickedPicksUpdate = pickedPicksUpdate + abbreviation; 
		}
	}
	
	$('#missing-picks-container').empty();
	if (numberOfMissingPicks > 0){
		if (numberOfMissingPicks == 1){
			$('#missing-picks-container').append('There is ' + numberOfMissingPicks + ' missing pick: ' + missingPicksString);
		}
		else {
			$('#missing-picks-container').append('There are ' + numberOfMissingPicks + ' missing picks: ' + missingPicksString);
		}
	}
	
	$('#picked-picks').val(pickedPicksUpdate);
}

/**
 * 
 * Gets the pick they made for the given game when making their picks.
 * 
 * @param gameId
 * @returns
 */
function getSelectedPick(gameId){
	return $('#pick-' + gameId).val();
}

/**
 * 
 * Creates the "pick splits" grid that shows who picked who in terms of home
 * and away, so we can see when one person was the only person to pick a team,
 * for example.
 * 
 * @param pickSplits
 * @returns
 */
function createPickSplitsGridHtml(pickSplits){
	
	//Steps to do:
	//	1. Make the header and figure out if we need to add the year and week.
	//	2. Go through each game and make a row for it.
	//	3. Put the people who picked home in the home column and the away people
	//	   in the away column.
	//	4. That's it.  Most of the rest of the stuff is css stuff.
	
	var yearSelected = isSpecificYearSelected();
	var weekSelected = isSpecificWeekSelected();
	
	var yearHeader = '';
	if (!yearSelected){
		yearHeader = '<th class="table-header">Year</th>';
	}
	
	var weekHeader = '';
	if (!weekSelected){
		weekHeader = '<th class="table-header">Week</th>';
	}
	
	var pickSplitsHeaderHtml = '<thead>' +
								 yearHeader + 
								 weekHeader + 
							 	 '<th class="table-header">Game</th>' + 
							 	 '<th class="table-header">Picked Home</th>' + 
							 	 '<th class="table-header">Picked Away</th>' +
							 '</thead>';
	
	var rowsHtml = '';
	
	for (var index = 0; index < pickSplits.length; index++){
		var pickSplit = pickSplits[index];
		
		var isBottomRow = false;
		if (index + 1 == pickSplits.length){
			isBottomRow = true;
		}
	
		var rowClassName = 'even-row';
		if (index % 2 == 1){
			rowClassName = 'odd-row';
		}

		//We want to show the winners in green, losers in red, and ties in yellow.
		var homePlayersClass = '';
		var awayPlayersClass = '';
		var homeTeamClass = '';
		var awayTeamClass = '';

		if (isDefined(pickSplit.winningTeamAbbreviation)){
			if (pickSplit.winningTeamAbbreviation == pickSplit.awayTeamAbbreviation){
				awayTeamClass = 'winner';
				homePlayersClass = 'loser';
				awayPlayersClass = 'winner';
			}
			else if (pickSplit.winningTeamAbbreviation == pickSplit.homeTeamAbbreviation){
				homeTeamClass = 'winner';
				homePlayersClass = 'winner';
				awayPlayersClass = 'loser';
			}
			else {
				awayTeamClass = 'tie';
				homeTeamClass = 'tie';
				homePlayersClass = 'tie';
				awayPlayersClass = 'tie';
			}
		}

		//Add in the year and week if they didn't pick a specific one.
		var year = '';
		var week = '';
		
		if (!yearSelected){
			//And we have to handle whether we're on the bottom or not with the cell.
			var cssClassToUse = 'first-pick-cell';
			if (isBottomRow){
				cssClassToUse = 'first-pick-cell-bottom';
			}
			
			year = '<td class="' + cssClassToUse + '">' + pickSplit.year + '</td>';
		}
		
		if (!weekSelected){
			
			//Same deal with the week but we have to handle whether it's the leftmost column
			//or if the year is first.
			var cssClassToUse = null;
			
			if (!yearSelected && !isBottomRow){
				cssClassToUse = 'pick-cell';
			}
			else if (!yearSelected && isBottomRow){
				cssClassToUse = 'pick-cell-bottom';
			}
			else if (yearSelected && !isBottomRow){
				cssClassToUse = 'first-pick-cell';
			}
			else if (yearSelected && isBottomRow){
				cssClassToUse = 'first-pick-cell-bottom';
			}
		
			week = '<td class="' + cssClassToUse + '">' + pickSplit.weekNumber + '</td>';
		}

		//And with the game too.  It needs different borders depending on whether it's
		//the first column, last row, etc...
		var isGameFirstCell = weekSelected && yearSelected;
		
		var gameCssClassToUse = null;
		
		if (!isGameFirstCell && !isBottomRow){
			gameCssClassToUse = 'pick-cell';
		}
		else if (!isGameFirstCell && isBottomRow){
			gameCssClassToUse = 'pick-cell-bottom';
		}
		else if (isGameFirstCell && !isBottomRow){
			gameCssClassToUse = 'first-pick-cell';
		}
		else if (isGameFirstCell && isBottomRow){
			gameCssClassToUse = 'first-pick-cell-bottom';
		}
		
		var gameRow = '<tr class="' + rowClassName + '">' + 
						year +
						week +
						'<td class="' + gameCssClassToUse + '">' + 
							'<span class="' + awayTeamClass + '">' + pickSplit.awayTeamAbbreviation + '</span>' + 
							' @ ' + 
							'<span class="' + homeTeamClass + '">' + pickSplit.homeTeamAbbreviation + '</span>' +  
						'</td>';
		
		//Now we just have to add the players who picked the home team to a string.
		var numberOfHomePlayers = 0;
		var homePlayersString = '';
		if (isDefined(pickSplit.homeTeamPlayers) && pickSplit.homeTeamPlayers.length > 0){
			//And we want to sort them so the names are shown in a consistent order.
			//sortAlphabetically(pickSplit.homeTeamPlayers);
			numberOfHomePlayers = pickSplit.homeTeamPlayers.length;
			for (var playerIndex = 0; playerIndex < pickSplit.homeTeamPlayers.length; playerIndex++){
				var player = pickSplit.homeTeamPlayers[playerIndex];
				
				if (playerIndex > 0){
					homePlayersString = homePlayersString + ', ';
				}
				
				homePlayersString = homePlayersString + player;
			}
		}
		
		//And the same thing for the away players.
		var numberOfAwayPlayers = 0;
		var awayPlayersString = '';
		if (isDefined(pickSplit.awayTeamPlayers) && pickSplit.awayTeamPlayers.length > 0){
			//sortAlphabetically(pickSplit.awayTeamPlayers);
			numberOfAwayPlayers = pickSplit.awayTeamPlayers.length;
			for (var playerIndex = 0; playerIndex < pickSplit.awayTeamPlayers.length; playerIndex++){
				var player = pickSplit.awayTeamPlayers[playerIndex];
				
				if (playerIndex > 0){
					awayPlayersString = awayPlayersString + ', ';
				}
				
				awayPlayersString = awayPlayersString + player;
			}
		}
		
		var pickGameClass = '';
		var pickResultClass = 'pick-cell';
		
		if (isBottomRow){
			pickGameClass = 'pick-game-bottom';
			pickResultClass = 'pick-cell-bottom';
		}
		
		//And just add the columns in for the picks and that's it.
		gameRow = gameRow + 
				  '<td class="' + pickResultClass + '"><span class="' + homePlayersClass + '">' + pickSplit.homeTeamAbbreviation + ' (' + numberOfHomePlayers + ')<br/> ' + homePlayersString + '</span></td>' + 
				  '<td class="' + pickResultClass + '"><span class="' + awayPlayersClass + '">' + pickSplit.awayTeamAbbreviation + ' (' + numberOfAwayPlayers + ')<br/> ' + awayPlayersString + '</span></td>' + 
				  '</tr>';
		
		rowsHtml = rowsHtml + gameRow;
	}
		
	var pickSplitsBodyHtml = '<tbody>' + rowsHtml + '</tbody>';
	
	var pickSplitsHtml = '<table class="picks-table" align="center">' + pickSplitsHeaderHtml + pickSplitsBodyHtml + '</table>';;
	
	return pickSplitsHtml;
}

