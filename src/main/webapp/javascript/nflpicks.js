/**
 * 
 * This is the "container" for global variables.  I made it so we won't have to worry
 * about "conflicts" with local variable names.
 * 
 */
var NFL_PICKS_GLOBAL = {

	/**
	 * Here to store data from the server so we can hopefully load it once and then
	 * get to it whenever it's needed as we're dealing with other stuff.
	 */
	data: {
		teams: [],
		players: [],
		years: []
	},
		
	/**
	 * The possible types for what they can view.  Like standings, picks, and stats.
	 * Holds label and value pairs of all the possible types.
	 */
	types: [],
	
	/**
	 * Holds label value pairs of all the players we show.  Not all of them will be "real"... like if we
	 * want to show "Everybody" or something like that.  It'll be in here too.
	 */
	players: [],
	
	/**
	 * All of the real players in label value pairs.  This is so we can send only real players to the server
	 * and pick them apart from the non-real players.
	 */
	realPlayers: [],
	
	/**
	 * All the years we want to show.  It'll have year ranges too (like "jurassic period" and "modern era").  It's
	 * label and value pairs like the other arrays.
	 */
	years: [],
	
	/**
	 * All the label and value pairs for the real and individual years.
	 */
	realYears: [],
	
	/**
	 * All the weeks we want to show in label value pairs.  It'll have ranges too (like "regular season" and "playoffs).
	 */
	weeks: [],
	
	/**
	 * All the individual and real weeks that we want to send to the server.
	 */
	realWeeks: [],
	
	/**
	 * All of the label/value pairs of the different stats we can show.
	 */
	statNames: [],

	/**
	 * The current selections for everything they can pick.
	 */
	selections: {},

	/**
	 * Whether they're selecting more than one player at a time
	 * or not.
	 */
	multiselectPlayer: false,
	
	/**
	 * Whether they're selecting more than one week at a time or not.
	 */
	multiselectWeek: false,
	
	/**
	 * Whether they're selecting more than one year at a time or not.
	 */
	multiselectYear: false,
		
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
	
	var team = NFL_PICKS_GLOBAL.initialTeam;
	if (isDefined(parameters) && isDefined(parameters.team)){
		team = parameters.team;
	}
	setSelectedTeams(team);
	
	resetPlayerSelections();
	resetYearSelections();
	resetWeekSelections();
	resetTeamSelections();
	
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
	parameters.player = getSelectedPlayerValues();
	parameters.year = getSelectedYearValues();
	parameters.week = getSelectedWeekValues();
	parameters.statName = getSelectedStatName();
	parameters.team = getSelectedTeamValues();
	parameters.multiselectPlayer = getMultiselectPlayer();
	parameters.multiselectYear = getMultiselectYear();
	parameters.multiselectWeek = getMultiselectWeek();
	parameters.multiselectTeam = getMultiselectTeam();
	
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
		
		NFL_PICKS_GLOBAL.data.teams = selectionCriteriaContainer.teams;
		NFL_PICKS_GLOBAL.data.players = selectionCriteriaContainer.players;
		NFL_PICKS_GLOBAL.data.years = selectionCriteriaContainer.years;
		
		var types = [{label: 'Standings', value: 'standings'},
		             {label: 'Picks', value: 'picks'},
		             {label: 'Stats', value: 'stats'}];
		
		NFL_PICKS_GLOBAL.types = types;
		
		var typeSelectorHtml = createTypeSelectorHtml(types);
		$('#typesContainer').empty();
		$('#selectorContainer').append(typeSelectorHtml);
		
		var years = selectionCriteriaContainer.years;
		//We want the "all" year option to be first.
		var yearOptions = [{label: 'All', value: 'all'},
		                   {label: 'Jurassic Period (2010-2015)', value: 'jurassic-period'},
		                   {label: 'First year (2016)', value: 'first-year'},
		                   {label: 'Modern Era (2017 - now)', value: 'modern-era'}];
		var realYears = [];
		for (var index = 0; index < years.length; index++){
			var year = years[index];
			yearOptions.push({label: year, value: year});
			realYears.push({label: year, value: year});
		}
		NFL_PICKS_GLOBAL.years = yearOptions;
		NFL_PICKS_GLOBAL.realYears = realYears;
		var yearSelectorHtml = createYearSelectorHtml(yearOptions);
		$('#yearsContainer').empty();
		$('#selectorContainer').append(yearSelectorHtml);
		
		var weekOptions = [{label: 'All', value: 'all'},
		                   {label: 'Regular season', value: 'regular-season'},
		                   {label: 'Playoffs', value: 'playoffs'},
		                   {label: 'Week 1', value: '1'}, {label: 'Week 2', value: '2'},
		                   {label: 'Week 3', value: '3'}, {label: 'Week 4', value: '4'},
		                   {label: 'Week 5', value: '5'}, {label: 'Week 6', value: '6'},
		                   {label: 'Week 7', value: '7'}, {label: 'Week 8', value: '8'},
		                   {label: 'Week 9', value: '9'}, {label: 'Week 10', value: '10'},
		                   {label: 'Week 11', value: '11'}, {label: 'Week 12', value: '12'},
		                   {label: 'Week 13', value: '13'}, {label: 'Week 14', value: '14'},
		                   {label: 'Week 15', value: '15'}, {label: 'Week 16', value: '16'},
		                   {label: 'Week 17', value: '17'}, {label: 'Wild Card', value: '18'},
		                   {label: 'Divisional', value: '19'}, {label: 'Conference Championship', value: '20'},
		                   {label: 'Superbowl', value: '21'}
		                   ];
		
		var realWeeks = [{label: 'Week 1', value: '1'}, {label: 'Week 2', value: '2'},
		                 {label: 'Week 3', value: '3'}, {label: 'Week 4', value: '4'},
		                 {label: 'Week 5', value: '5'}, {label: 'Week 6', value: '6'},
		                 {label: 'Week 7', value: '7'}, {label: 'Week 8', value: '8'},
		                 {label: 'Week 9', value: '9'}, {label: 'Week 10', value: '10'},
		                 {label: 'Week 11', value: '11'}, {label: 'Week 12', value: '12'},
		                 {label: 'Week 13', value: '13'}, {label: 'Week 14', value: '14'},
		                 {label: 'Week 15', value: '15'}, {label: 'Week 16', value: '16'},
		                 {label: 'Week 17', value: '17'}, {label: 'Wild Card', value: '18'},
		                 {label: 'Divisional', value: '19'}, {label: 'Conference Championship', value: '20'},
		                 {label: 'Superbowl', value: '21'}];
		
		//need to refactor the NFL_PICKS_GLOBAL so that it has all the options
		//and all the data
		//NFL_PICKS_GLOBAL.criteria.weeks - the weeks as selection criteria
		//NFL_PICKS_GLOBAL.data.weeks - all the actual weeks
		NFL_PICKS_GLOBAL.weeks = weekOptions;
		NFL_PICKS_GLOBAL.realWeeks = realWeeks;
		var weekSelectorHtml = createWeekSelectorHtml(weekOptions);
		$('#selectorContainer').append(weekSelectorHtml);
		
		var players = selectionCriteriaContainer.players;
		//We want the "all" player option to be the first one.
		var playerOptions = [{label: 'Everybody', value: 'all'}];
		var realPlayers = [];
		for (var index = 0; index < players.length; index++){
			var player = players[index];
			var playerObject = {label: player, value: player};
			playerOptions.push(playerObject);
			realPlayers.push(playerObject);
		}
		setOptionsInSelect('player', playerOptions);

		NFL_PICKS_GLOBAL.players = playerOptions;
		NFL_PICKS_GLOBAL.realPlayers = realPlayers;
		var playerSelectorHtml = createPlayerSelectorHtml(playerOptions);
		$('#selectorContainer').append(playerSelectorHtml);
		
		//Need to filter the teams so that we only show teams that had a game in a given year.
		//Probably just do a ui filter because we probably don't want to make a trip to the server
		//
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
		var teamSelectorHtml = createTeamSelectorHtml(teamOptions);
		$('#selectorContainer').append(teamSelectorHtml);
		NFL_PICKS_GLOBAL.teams = teamOptions;
		
		var statNameOptions = [{label: 'Champions', value: 'champions'},
		                       {label: 'Championship Standings', value: 'championshipStandings'},
		                       {label: 'Season Standings', value: 'seasonStandings'},
		                       {label: 'Week Standings', value: 'weekStandings'},
		                       {label: 'Weeks Won Standings', value: 'weeksWonStandings'},
		                       {label: 'Weeks Won By Week', value: 'weeksWonByWeek'},
		                       {label: 'Week Records By Player', value: 'weekRecordsByPlayer'},
		                       {label: 'Pick Accuracy', value: 'pickAccuracy'},
		                       {label: 'Pick Splits', value: 'pickSplits'},
		                       {label: 'Week Comparison', value: 'weekComparison'}];

		var statNameSelectorHtml = createStatNameSelectorHtml(statNameOptions);
		$('#selectorContainer').append(statNameSelectorHtml);
		NFL_PICKS_GLOBAL.statNames = statNameOptions;
		

		//The current year and week come from the server.
		NFL_PICKS_GLOBAL.currentYear = selectionCriteriaContainer.currentYear;
		NFL_PICKS_GLOBAL.currentWeekNumber = selectionCriteriaContainer.currentWeekNumber;
		//Initially, we want to see the standings for the current year for everybody, so set those
		//as the initial types.
		NFL_PICKS_GLOBAL.initialType = 'standings';
		NFL_PICKS_GLOBAL.initialYear = NFL_PICKS_GLOBAL.currentYear + '';
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
	
	//If there are previous parameters, and we should push them, then push them on the backward
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
	
	//And update the options for the criteria in each selector.
	updateAvailableCriteriaOptions();
	
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
	
	updateTypeLink();
	updatePlayersLink();
	updateYearsLink();
	updateWeeksLink();
	updateTeamsLink();
	updateStatNameLink();
	
	//And we need to make sure we're showing the right "forward" and "back" links.
	updateNavigationLinksVisibility();
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
 * It will farm the work out to other functions that handle the specific scenarios for each
 * kind of "selector".
 * 
 * @returns
 */
function updateAvailableCriteriaOptions(){
	
	updateAvailableTeamOptions();
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
	
	//Hid the teams that should be hidden in the selector.
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
	else if ('stats' == type){
		updateStatsSelectors(type);
	}
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
	
	showPlayersLink();
	showWeeksLink();
	showYearsLink();
	hideTeamsLink();
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
	else if ('seasonStandings' == statName){
		showPlayersLink();
		showYearsLink();
		hideWeeksLink();
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
	
	NFL_PICKS_GLOBAL.selections.weeks = weeksArray;
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
	
	NFL_PICKS_GLOBAL.selections.teams = teamsArray;
}

/**
 * 
 * Gets the selected stat name.
 * 
 * @returns
 */
function getSelectedStatName(){
	return $('input[name=statName]:checked').val();
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
	$('input[name=statName]').val([statName]);
	NFL_PICKS_GLOBAL.selections.statName = statName;
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
	
	var playerValuesForRequest = getPlayerValuesForRequest();
	var yearValuesForRequest = getYearValuesForRequest();
	var weekValuesForRequest = getWeekValuesForRequest();
	
	setContent('<div style="text-align: center;">Loading...</div>');
	
	$.ajax({url: 'nflpicks?target=standings&player=' + playerValuesForRequest + '&year=' + yearValuesForRequest + '&week=' + weekValuesForRequest,
		contentType: 'application/json; charset=UTF-8'}
	)
	.done(function(data) {
		var standingsContainer = $.parseJSON(data);
		//We want to show the records that came back, but we're going to have to sort them
		//to make sure they're in the order we want.
		var records = standingsContainer.records;

		//We want the record with the most wins coming first.  If they have the same number
		//of wins, we want the one with fewer losses coming first.
		//And if they're tied, we want them ordered by name.
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
			
			if (record1.player.name < record2.player.name){
				return -1;
			}
			else if (record1.player.name > record2.player.name){
				return 1;
			}
			
			return 0;
		});
		
		//Now that we have them sorted, we can create the html for the standings.
		var standingsHtml = createStandingsHtml(standingsContainer.records);
		
		//And set it as the content.
		setContent(standingsHtml);
	})
	.fail(function() {
		setContent('<div style="text-align: center;">Error</div>');
	})
	.always(function() {
	});
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

	var selectedYearValues = getSelectedYearValues();
	var selectedWeekValues = getSelectedWeekValues();
	
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
	if (selectedYearValues.includes('all') && !NFL_PICKS_GLOBAL.havePicksBeenShown && !hasYearInUrl){
		var currentYear = NFL_PICKS_GLOBAL.currentYear + '';
		setSelectedYears(currentYear);
		updateYearsLink();
	}

	//Do the same thing with the week.  We only want to show picks for all the weeks if
	//they went out of their way to say that's what they wanted to do.
	var hasWeekInUrl = false;
	if (isDefined(parameters) && isDefined(parameters.week)){
		hasWeekInUrl = true;
	}
	
	//If it's "all" and the picks haven't been shown and the "all" didn't come from the url,
	//it's their first time seeing the picks, so we should show the ones for the current week.
	if (selectedWeekValues.includes('all') && !NFL_PICKS_GLOBAL.havePicksBeenShown && !hasWeekInUrl){
		var currentWeek = NFL_PICKS_GLOBAL.currentWeekNumber + '';
		setSelectedWeeks(currentWeek);
		updateWeeksLink();
	}
	
	//At this point, we're going to show them the picks, so we should flip that switch.
	NFL_PICKS_GLOBAL.havePicksBeenShown = true;
	
	var playerValuesForRequest = getPlayerValuesForRequest();
	var yearValuesForRequest = getYearValuesForRequest();
	var weekValuesForRequest = getWeekValuesForRequest();
	var teamValuesForRequest = getTeamValuesForRequest();

	setContent('<div style="text-align: center;">Loading...</div>');
	
	//Go to the server and get the grid.
	$.ajax({url: 'nflpicks?target=compactPicksGrid&player=' + playerValuesForRequest + '&year=' + yearValuesForRequest + '&week=' + weekValuesForRequest + '&team=' + teamValuesForRequest,
		contentType: 'application/json; charset=UTF-8'}
	)
	.done(function(data) {
		//Update the UI with what the server sent back.
		var picksGrid = $.parseJSON(data);
		var picksGridHtml = createPicksGridHtml(picksGrid);
		setContent(picksGridHtml);
	})
	.fail(function() {
		setContent('<div style="text-align: center;">Error</div>');
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
	var selectedPlayerValues = getPlayerValuesForRequest();
	var selectedYearValues = getYearValuesForRequest();
	var selectedWeekValues = getWeekValuesForRequest();
	var selectedTeamValues = getTeamValuesForRequest();
	
	//If the stat name is the "pick splits", we want to do the same thing we do with the picks grid.
	//Only show "all" for the year or the week if they actually set it to "all".
	//If it's the first time we're showing the pick splits, we only want to show all of them if that
	//was in the url.
	if (statName == 'pickSplits'){
		//Since we're showing how players are split up, we want to show all players.
		var selectedYearValues = getSelectedYearValues();
		var selectedWeekValues = getSelectedWeekValues();

		var urlParameters = getUrlParameters();
		
		//Same deal as with the picks grid...
		var hasYearInUrl = false;
		if (isDefined(urlParameters) && isDefined(urlParameters.year)){
			hasYearInUrl = true;
		}
		
		//If the year is "all", we haven't shown the picks, and "all" didn't come from the url, then we
		//want the year we show the pick splits for to be the current year.
		if (selectedYearValues.includes('all') && !NFL_PICKS_GLOBAL.havePickSplitsBeenShown && !hasYearInUrl){
			var currentYear = NFL_PICKS_GLOBAL.currentYear + '';
			setSelectedYears(currentYear);
			updateYearsLink();
		}
		
		//Same deal as with the year and with the picks grid...
		var hasWeekInUrl = false;
		if (isDefined(urlParameters) && isDefined(urlParameters.week)){
			hasWeekInUrl = true;
		}
		
		//If the week is "all", we haven't shown the picks, and "all" didn't come from the url, then we
		//want the week we show the pick splits for to be the current week.
		if (selectedWeekValues.includes('all') && !NFL_PICKS_GLOBAL.havePickSplitsBeenShown && !hasWeekInUrl){
			var currentWeek = NFL_PICKS_GLOBAL.currentWeekNumber + '';
			setSelectedWeeks(currentWeek);
			updateWeeksLink();
		}
		
		//And, since we're here, that means we've shown the pick splits to the user, so the next time, we won't
		//do the funny business with the week and year.
		NFL_PICKS_GLOBAL.havePickSplitsBeenShown = true;
	}
	
	var playerValuesForRequest = getPlayerValuesForRequest();
	var yearValuesForRequest = getYearValuesForRequest();
	var weekValuesForRequest = getWeekValuesForRequest();
	var teamValuesForRequest = getTeamValuesForRequest();

	setContent('<div style="text-align: center;">Loading...</div>');
	
	//Send the request to the server.
	$.ajax({url: 'nflpicks?target=stats&statName=' + statName + '&player=' + playerValuesForRequest + '&year=' + yearValuesForRequest + 
				 '&week=' + weekValuesForRequest + '&team=' + teamValuesForRequest,
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
		else if ('seasonStandings' == statName){
			var seasonRecords = $.parseJSON(data);
			statsHtml = createSeasonStandingsHtml(seasonRecords);
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
			sortWeekRecordsBySeasonWeekAndRecord(weekRecords);
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
		else if ('weekComparison' == statName){
			var weekRecords = $.parseJSON(data);
			//Like with the other records, we want to sort them before we show them.
			sortWeekRecordsBySeasonWeekAndRecord(weekRecords);
			statsHtml = createWeekComparisonHtml(weekRecords);
		}
		
		setContent(statsHtml);
	})
	.fail(function() {
		setContent('<div style="text-align: center;">Error</div>');
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
 * When somebody clicks the "body" of the page, we want it to hide everything, so
 * that's what this function will do.  It just goes through and calls the function
 * that hides the selectors.  It also resets them too.
 * 
 * @returns
 */
function onClickBody(){
	hideTypeSelector();
	resetAndHidePlayerSelections();
	resetAndHideYearSelections();
	resetAndHideWeekSelections();
	hideStatNameSelector();
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
	
	var isElementVisible = isVisible(id);
	
	if (isVisible){
		$('#' + id).hide();
	}
	else {
		$('#' + id).show();
	}
}

/**
 * 
 * A really dumb function for checking whether an element with the given
 * id is visible or not.  Just calls jquery to do the work.
 * 
 * @param id
 * @returns
 */
function isVisible(id){
	
	var isElementVisible = $('#' + id).is(':visible');
	
	return isElementVisible;
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

		//Sort it alphabetically by name if they have the same record.
		if (weekRecord1.player.name < weekRecord2.player.name){
			return -1;
		}
		else if (weekRecord1.player.name > weekRecord2.player.name){
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
 * This function will sort the given array of "week records" so that it
 * goes in ascending order by the week's year and week (so it's in increasing
 * order by season and by week within each season).
 * 
 * @param weekRecords
 * @returns
 */
function sortWeekRecordsBySeasonWeekAndRecord(weekRecords){
	
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
		//If it's the same year...
		else {
			//Compare on the weeks.
			var week1 = weekRecord1.week.weekNumber;
			var week2 = weekRecord2.week.weekNumber;
			
			//With the earlier week first.
			if (week1 < week2){
				return -1;
			}
			else if (week1 > week2){
				return 1;
			}
			//same week, so sort by the record.
			else {
				if (weekRecord1.record.wins > weekRecord2.record.wins){
					return -1;
				}
				else if (weekRecord1.record.wins < weekRecord2.record.wins){
					return 1;
				}
				else {
					if (weekRecord1.record.losses < weekRecord2.record.losses){
						return -1;
					}
					else if (weekRecord1.record.losses > weekRecord2.record.losses){
						return 1;
					}
					//Same year, week, wins, and losses, sort by the name
					else {
						if (weekRecord1.player.name < weekRecord2.player.name){
							return -1;
						}
						else if (weekRecord1.player.name > weekRecord2.player.name){
							return 1;
						}
					}
				}
			}
		}
		
		return 0;
	});
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
	
	if (selectedWeeks.length > 1){
		return false;
	}
	
	var selectedWeek = selectedWeeks[0].value;
	
	if ('all' == selectedWeek || 'regular-season' == selectedWeek || 'playoffs' == selectedWeek){
		return false;
	}
	
	return true;
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
 * Here so we can make sure the pick accuracies are in the right order (with the most
 * accurate team coming first).
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
		
		//If they have the same times right, sort on times wrong.
		if (pickAccuracySummaryA.timesWrong < pickAccuracySummaryB.timesWrong){
			return -1;
		}
		//Fewer times right = back of the list.
		else if (pickAccuracySummaryA.timesWrong > pickAccuracySummaryB.timesWrong){
			return 1;
		}
		
		//If they have the same times right and wrong, sort by player name.
		if (pickAccuracySummaryA.player.name < pickAccuracySummaryB.player.name){
			return -1;
		}
		else if (pickAccuracySummaryA.player.name > pickAccuracySummaryB.player.name){
			return 1;
		}
		
		//If they have the same player name, sort by team abbreviation.
		if (pickAccuracySummaryA.team.abbreviation < pickAccuracySummaryB.team.abbreviation){
			return -1;
		}
		else if (pickAccuracySummaryA.team.abbreviation > pickAccuracySummaryB.team.abbreviation){
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
		var yearValue = year;
		if (Array.isArray(year)){
			yearValue = arrayToDelimitedValue(year, ',');
		}
		picksLink = picksLink + '\'' + yearValue + '\', ';
	}
	else {
		picksLink = picksLink + 'null, ';
	}
	
	if (isDefined(week)){
		var weekValue = week;
		if (Array.isArray(week)){
			weekValue = arrayToDelimitedValue(week, ',');
		}
		picksLink = picksLink + '\'' + weekValue + '\', ';
	}
	else {
		picksLink = picksLink + 'null, ';
	}
	
	if (isDefined(team)){
		var teamValue = team;
		if (Array.isArray(team)){
			teamValue = arrayToDelimitedValue(team, ',');
		}
		picksLink = picksLink + '\'' + teamValue + '\', ';
	}
	else {
		picksLink = picksLink + 'null, ';
	}
	
	if (isDefined(player)){
		var playerValue = player;
		if (Array.isArray(player)){
			playerValue = arrayToDelimitedValue(player, ',');
		}
		picksLink = picksLink + '\'' + playerValue + '\'';
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
		setSelectedYears(year);
	}
	
	if (isDefined(week)){
		setSelectedWeeks(week);
	}
	
	if (isDefined(player)){
		setSelectedPlayers(player);
	}
	
	if (isDefined(team)){
		selectSingleTeamFull(team);
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

	if ('Playoffs - Wild Card' == label){
		return 'Wild card';
	}
	else if ('Playoffs - Divisional' == label){
		return 'Divisional';
	}
	else if ('Playoffs - Conference Championship' == label || 'Conference Championship' == label){
		return 'Conf champ';
	}
	else if ('Playoffs - Super Bowl' == label){
		return 'Super bowl';
	}
	
	return label;
}

/**
 * 
 * This function will check whether the given value has multiple values
 * in it or not.  Basically, it'll return true if the given value is defined
 * and it has a comma in it.  It assumes the given value is a string and multiple
 * values are separated by commas in that string.
 * 
 * @param value
 * @returns
 */
function doesValueHaveMultipleValues(value){
	
	if (isDefined(value) && value.indexOf(',') != -1){
		return true;
	}
	
	return false;
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
