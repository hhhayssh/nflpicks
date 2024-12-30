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
	setSelectedYears(year);
	
	var week = NFL_PICKS_GLOBAL.initialWeek;
	if (isDefined(parameters) && isDefined(parameters.week)){
		week = parameters.week;
	}
	setSelectedWeeks(week);
	
	var player = NFL_PICKS_GLOBAL.initialPlayer;
	if (isDefined(parameters) && isDefined(parameters.player)){
		player = parameters.player;
	}
	setSelectedPlayers(player);
	
	var statName = NFL_PICKS_GLOBAL.initialStatName;
	if (isDefined(parameters) && isDefined(parameters.statName)){
		statName = parameters.statName;
	}
	setSelectedStatName(statName);
	
//	var team = NFL_PICKS_GLOBAL.initialTeam;
//	if (isDefined(parameters) && isDefined(parameters.team)){
//		team = parameters.team;
//	}
//	setSelectedTeams(team);
	
	var team1 = NFL_PICKS_GLOBAL.initialTeam1;
	if (isDefined(parameters) && isDefined(parameters.team1)){
		team1 = parameters.team1;
	}
	setSelectedTeams1(team1);
	
	var team2 = NFL_PICKS_GLOBAL.initialTeam2;
	if (isDefined(parameters) && isDefined(parameters.team2)){
		team2 = parameters.team2;
	}
	setSelectedTeams2(team2);
	
	resetPlayerSelections();
	resetYearSelections();
	resetWeekSelections();
//	resetTeamSelections();
	resetTeam1Selections();
	resetTeam2Selections();
	
	updateTypeLink();
	updatePlayersLink();
	updateWeeksLink();
	updateYearsLink();
	updateTeamsLink();
	updateStatNameLink();
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
	
	if (isDefined(parameters.type)){
		setSelectedType(parameters.type);
	}
	
	if (isDefined(parameters.player)){
		setSelectedPlayers(parameters.player);
	}
	
	if (isDefined(parameters.year)){
		setSelectedYears(parameters.year);
	}
	
	if (isDefined(parameters.week)){
		setSelectedWeeks(parameters.week);
	}
	
	if (isDefined(parameters.team)){
		setSelectedTeams(parameters.team);
	}
	
	if (isDefined(parameters.statName)){
		setSelectedStatName(parameters.statName);
	}
	
	if (isDefined(parameters.multiselectPlayer)){
		setMultiselectPlayer(parameters.multiselectPlayer);
		setMultiselectPlayerValue(parameters.multiselectPlayer);
	}
	
	if (isDefined(parameters.multiselectYear)){
		setMultiselectYear(parameters.multiselectYear);
		setMultiselectYearValue(parameters.multiselectYear);
	}
	
	if (isDefined(parameters.multiselectWeek)){
		setMultiselectWeek(parameters.multiselectWeek);
		setMultiselectWeekValue(parameters.multiselectWeek);
	}
	
	if (isDefined(parameters.multiselectTeam)){
		setMultiselectTeam(parameters.multiselectTeam);
		setMultiselectTeamValue(parameters.multiselectTeam);
	}
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
	parameters.player = getSelectedPlayerValues();
	parameters.year = getSelectedYearValues();
	parameters.week = getSelectedWeekValues();
	parameters.statName = getSelectedStatName();
	parameters.team1 = getSelectedTeam1Values();
	parameters.team2 = getSelectedTeam2Values();
	parameters.multiselectPlayer = getMultiselectPlayer();
	parameters.multiselectYear = getMultiselectYear();
	parameters.multiselectWeek = getMultiselectWeek();
	parameters.multiselectTeam = getMultiselectTeam();
	
	return parameters;
}


/**
 * 
 * This function will update the available options for the criteria based on what's selected.
 * It's here mainly for the situation where you select an option in a "selector" and that option
 * should cause options in other selectors to be either shown or hidden.
 * 
 * I made it for the situation where somebody picks a year and we should only show the teams
 * that actually existed that year.  Like, if somebody picks "2020" as the year, we shouldn't
 * show "OAK", but we should show "LV".
 * 
 * ... And now it's here to handle the change in week for the 2021 season where a 17th game was added.
 * 
 * It will farm the work out to other functions that handle the specific scenarios for each
 * kind of "selector".
 * 
 * @returns
 */
function updateAvailableCriteriaOptions(){
	
	updateAvailableTeamOptions();
	
	updateAvailableWeekOptions();
	
	//updateAvailableWeekOptions.................
	//
	//main_updateAvailableTeamOptions
}

//updateSelectors

/**
 * 
 * This function will update the selectors for the given type.  It just calls
 * the specific type's update function.
 * 
 * It will also update the multi-selects so that the selected values are updated
 * if they're selecting multiple "items" (multiple players, weeks, or years).
 * 
 * @param type
 * @returns
 */
function updateSelectors(type){
	
	//Steps to do:
	//	1. Call the function based on the type.
	//	2. Update the multi selects.
	
	if ('picks' == type){
		updatePicksSelectors(type);
	}
	else if ('standings' == type){
		updateStandingsSelectors(type);
	}
	else if ('divisionStandings' == type){
		updateDivisionStandingsSelectors(type);
	}
	else if ('stats' == type){
		updateStatsSelectors(type);
	}
}

/**
 * 
 * A convenience function for hiding all the selector containers.  Not much
 * to it.
 * 
 * @returns
 */
function hideSelectorContainers(){
	hideTypeSelector();
	hidePlayerSelector();
	hideYearSelector();
	hideWeekSelector();
	hideStatNameSelector();
	hideTeamSelector();
}