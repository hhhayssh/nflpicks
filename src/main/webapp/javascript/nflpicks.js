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
	 * Each selector will put its variables in this (players, teams, ...).
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
	 * Whether they're selecting more than one team at a time or not.
	 */
	multiSelectTeam: false,
	
	/**
	 * If they're selecting teams, this says whether we should treat it as "team 1 @ team 2" or not.
	 */
	team1AtTeam2: false,
		
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
	currentWeekKey: null,
	
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
	initialStatName: null,
	
	/**
	 * Whether divisions are enabled or not.  Here so I can flip back and forth from how it used to be
	 * (everybody in one division) to how it is now (people split into different divisions).
	 */
	divisionsEnabled: true
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
		
		var initialType = 'divisionStandings';
		if (isDefined(selectionCriteriaContainer.divisionsEnabled) &&
				'false' == selectionCriteriaContainer.divisionsEnabled){
			NFL_PICKS_GLOBAL.divisionsEnabled = false;
			initialType = 'standings';
		}
		
		var types = [{label: 'Standings', value: 'standings'},
            {label: 'Picks', value: 'picks'},
            {label: 'Stats', value: 'stats'}];

		//If divisions are enabled, put the "division standings" first.
		if (NFL_PICKS_GLOBAL.divisionsEnabled){
			types.splice(0, 0, {label: 'Division standings', value: 'divisionStandings'});
		}
		
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
		                   {label: 'Regular season', value: 'regular_season'},
		                   {label: 'Playoffs', value: 'playoffs'},
		                   {label: 'Week 1', value: '1'}, {label: 'Week 2', value: '2'},
		                   {label: 'Week 3', value: '3'}, {label: 'Week 4', value: '4'},
		                   {label: 'Week 5', value: '5'}, {label: 'Week 6', value: '6'},
		                   {label: 'Week 7', value: '7'}, {label: 'Week 8', value: '8'},
		                   {label: 'Week 9', value: '9'}, {label: 'Week 10', value: '10'},
		                   {label: 'Week 11', value: '11'}, {label: 'Week 12', value: '12'},
		                   {label: 'Week 13', value: '13'}, {label: 'Week 14', value: '14'},
		                   {label: 'Week 15', value: '15'}, {label: 'Week 16', value: '16'},
		                   {label: 'Week 17', value: '17'}, {label: 'Week 18', value: '18'}, 
		                   {label: 'Wild Card', value: 'wildcard'},
		                   {label: 'Divisional', value: 'divisional'}, 
		                   {label: 'Conference Championship', value: 'conference_championship'}, 
		                   {label: 'Superbowl', value: 'superbowl'}
		                   ];
		
		//could these be 1, 2, 3, 4, ... again and just change the playoffs?
		//yeah i think so
		//week=1,2,3,4,wildcard,divisional,superbowl
		//yeah that's better than
		//week=1,2,3,wildcard,divisional
		//need to change .... importer ... the model util function ... this
		//and that should be it.
		var realWeeks = [{label: 'Week 1', value: '1'}, {label: 'Week 2', value: '2'},
				            {label: 'Week 3', value: '3'}, {label: 'Week 4', value: '4'},
				            {label: 'Week 5', value: '5'}, {label: 'Week 6', value: '6'},
				            {label: 'Week 7', value: '7'}, {label: 'Week 8', value: '8'},
				            {label: 'Week 9', value: '9'}, {label: 'Week 10', value: '10'},
				            {label: 'Week 11', value: '11'}, {label: 'Week 12', value: '12'},
				            {label: 'Week 13', value: '13'}, {label: 'Week 14', value: '14'},
				            {label: 'Week 15', value: '15'}, {label: 'Week 16', value: '16'},
				            {label: 'Week 17', value: '17'}, {label: 'Week 18', value: '18'}, 
				            {label: 'Wild Card', value: 'wildcard'},
				            {label: 'Divisional', value: 'divisional'}, 
				            {label: 'Conference Championship', value: 'conference_championship'}, 
				            {label: 'Superbowl', value: 'superbowl'}
				            ];
		
		//need to refactor the NFL_PICKS_GLOBAL so that it has all the options
		//and all the data
		//NFL_PICKS_GLOBAL.criteria.weeks - the weeks as selection criteria
		//NFL_PICKS_GLOBAL.data.weeks - all the actual weeks
		//global_setWeeks
		//global_setRealWeeks
		//global_getWeeks
		//selector_blah
		//html_blah
		//yeah this needs to be done
		//nflpicks global needs to be defined in a separate javascript file
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
		                       {label: 'Week Comparison', value: 'weekComparison'},
		                       {label: 'Season Progression', value: 'seasonProgression'},
		                       {label: 'Collective Records', value: 'collectiveRecordSummary'},
		                       {label: 'Collective Pick Accuracy', value: 'collectivePickAccuracy'}];
		
		//Only add in the division title options if divisions are enabled.
		if (NFL_PICKS_GLOBAL.divisionsEnabled){
			statNameOptions.splice(2, 0, {label: 'Division Titles', value: 'divisionTitles'});
			statNameOptions.splice(3, 0, {label: 'Division Title Standings', value: 'divisionTitleStandings'});
		}

		var statNameSelectorHtml = createStatNameSelectorHtml(statNameOptions);
		$('#selectorContainer').append(statNameSelectorHtml);
		NFL_PICKS_GLOBAL.statNames = statNameOptions;
		

		//The current year and week come from the server.
		NFL_PICKS_GLOBAL.currentYear = selectionCriteriaContainer.currentYear;
		NFL_PICKS_GLOBAL.currentWeekKey = selectionCriteriaContainer.currentWeekKey;
		//Initially, we want to see the standings for the current year for everybody, so set those
		//as the initial types.
		NFL_PICKS_GLOBAL.initialType = initialType;
		NFL_PICKS_GLOBAL.initialYear = NFL_PICKS_GLOBAL.currentYear + '';
		NFL_PICKS_GLOBAL.initialWeek = 'all';
		NFL_PICKS_GLOBAL.initialPlayer = 'all';
		NFL_PICKS_GLOBAL.initialTeam = 'all';
		NFL_PICKS_GLOBAL.initialTeam1 = 'all';
		NFL_PICKS_GLOBAL.initialTeam2 = 'all';
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
	else if ('divisionStandings' == type){
		updateDivisionStandings();
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
	var team1ValuesForRequest = getTeam1ValuesForRequest();
	var team2ValuesForRequest = getTeam2ValuesForRequest();
	
	setContent('<div style="text-align: center;">Loading...</div>');
	
	$.ajax({url: 'nflpicks?target=standings' + 
			'&player=' + playerValuesForRequest + 
			'&year=' + yearValuesForRequest + 
			'&week=' + weekValuesForRequest + 
			'&team1=' + team1ValuesForRequest + 
			'&team2=' + team2ValuesForRequest,
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
 * This function will go get the standings from the server and show them on the UI.
 * 
 * What standings it gets depends on the player, year, and week that are selected.
 * 
 * @returns
 */
function updateDivisionStandings(){

	//Steps to do:
	//	1. Get the parameters to send (player, year, and week).
	//	2. Send them to the server.
	//	3. Update the UI with the results.
	
	var playerValuesForRequest = getPlayerValuesForRequest();
	var yearValuesForRequest = getYearValuesForRequest();
	var weekValuesForRequest = getWeekValuesForRequest();
	var team1ValuesForRequest = getTeam1ValuesForRequest();
	var team2ValuesForRequest = getTeam2ValuesForRequest();
	var team1AtTeam2ForRequest = getTeam1AtTeam2ValueForRequest();
	
	//need to send the "@" or "vs"
	
	setContent('<div style="text-align: center;">Loading...</div>');
	
	$.ajax({url: 'nflpicks?target=divisionStandings' + 
			'&player=' + playerValuesForRequest + 
			'&year=' + yearValuesForRequest + 
			'&week=' + weekValuesForRequest + 
			'&team1=' + team1ValuesForRequest + 
			'&team2=' + team2ValuesForRequest + 
			'&team1AtTeam2=' + team1AtTeam2ForRequest,
		contentType: 'application/json; charset=UTF-8'}
	)
	.done(function(data) {
		var standingsContainer = $.parseJSON(data);
		//We want to show the records that came back, but we're going to have to sort them
		//to make sure they're in the order we want.
		var divisionRecords = standingsContainer.divisionRecords;

		for (var index = 0; index < divisionRecords.length; index++){
			
			var divisionRecord = divisionRecords[index];
			
			if (divisionRecord.records == null || divisionRecord.records == undefined){
				continue;
			}
			
			//We want the record with the most wins coming first.  If they have the same number
			//of wins, we want the one with fewer losses coming first.
			//And if they're tied, we want them ordered by name.
			divisionRecord.records.sort(function (record1, record2){
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
		}
		
		//Now that we have them sorted, we can create the html for the standings.
		var divisionStandingsHtml = createDivisionStandingsHtml(standingsContainer.divisionRecords);
		
		//And set it as the content.
		setContent(divisionStandingsHtml);
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
		var currentWeek = NFL_PICKS_GLOBAL.currentWeekKey + '';
		setSelectedWeeks(currentWeek);
		updateWeeksLink();
	}
	
	//At this point, we're going to show them the picks, so we should flip that switch.
	NFL_PICKS_GLOBAL.havePicksBeenShown = true;
	
	var playerValuesForRequest = getPlayerValuesForRequest();
	var yearValuesForRequest = getYearValuesForRequest();
	var weekValuesForRequest = getWeekValuesForRequest();
	var team1ValuesForRequest = getTeam1ValuesForRequest();
	var team2ValuesForRequest = getTeam2ValuesForRequest();
	var team1AtTeam2ForRequest = getTeam1AtTeam2ValueForRequest();

	setContent('<div style="text-align: center;">Loading...</div>');
	
	//Go to the server and get the grid.
	$.ajax({url: 'nflpicks?target=compactPicksGrid' + 
		'&player=' + playerValuesForRequest + 
		'&year=' + yearValuesForRequest + 
		'&week=' + weekValuesForRequest + 
		'&team1=' + team1ValuesForRequest + 
		'&team2=' + team2ValuesForRequest + 
		'&team1AtTeam2=' + team1AtTeam2ForRequest,
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
	//var selectedTeamValues = getTeamValuesForRequest();
	
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
			var currentWeek = NFL_PICKS_GLOBAL.currentWeekKey + '';
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
//	var teamValuesForRequest = getTeamValuesForRequest();
	
	var team1ValuesForRequest = getTeam1ValuesForRequest();
	var team2ValuesForRequest = getTeam2ValuesForRequest();
	var team1AtTeam2ForRequest = getTeam1AtTeam2ValueForRequest();

	setContent('<div style="text-align: center;">Loading...</div>');
	
	//Send the request to the server.
	$.ajax({url: 'nflpicks?target=stats&statName=' + statName + '&player=' + playerValuesForRequest + '&year=' + yearValuesForRequest + 
				 '&week=' + weekValuesForRequest + 
				 '&team1=' + team1ValuesForRequest + '&team2=' + team2ValuesForRequest +
				 '&team1AtTeam2=' + team1AtTeam2ForRequest,
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
		else if ('divisionTitles' == statName){
			var divisionTitles = $.parseJSON(data);
			statsHtml = createDivisionTitlesHtml(divisionTitles);
		}
		else if ('divisionTitleStandings' == statName){
			var divisionTitlesForPlayerList = $.parseJSON(data);
			statsHtml = createDivisionTitleStandingsHtml(divisionTitlesForPlayerList);
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
			var start = Date.now();
			var pickAccuracySummaries = $.parseJSON(data);
			var jsonElapsed = Date.now() - start;
			var htmlStart = Date.now();
			statsHtml = createPickAccuracySummariesHtml(pickAccuracySummaries);
			var htmlElapsed = Date.now() - htmlStart;
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
		else if ('seasonProgression' == statName){
			var weekRecords = $.parseJSON(data);
			//Like with the other records, we want to sort them before we show them.
			sortWeekRecordsBySeasonWeekAndRecord(weekRecords);
			statsHtml = createSeasonProgressionHtml(weekRecords);
		}
		else if ('collectiveRecordSummary' == statName){
			var collectiveRecordSummary = $.parseJSON(data);
			statsHtml = createCollectiveRecordSummaryHtml(collectiveRecordSummary);
		}
		else if ('collectivePickAccuracy' == statName){
			var collectivePickAccuracySummary = $.parseJSON(data);
			statsHtml = createCollectivePickAccuracySummaryHtml(collectivePickAccuracySummary);
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
	resetAndHideTeamSelections();
	hideStatNameSelector();
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