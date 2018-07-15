
var previousType = null;

var havePicksBeenShown = false;

$(document).ready(
	function(){
		//Do this in edit too....
		//other stuff to do:
		//	1. make it work without javascript
		//	1. add stats
		//	2. add comments and clean up code
		//	3. insert everything from the csvs
		getSelectionCriteriaAndInitialize();
});

function initializeView(){
	setSelectionsFromUrlParameters();
	updateView();
}

function setSelectionsFromUrlParameters(){
	
	var parameters = getUrlParameters();
	
	if (!isDefined(parameters)){
		return;
	}
	
	setSelectionsFromParameters(parameters);
}

function setSelectionsFromParameters(parameters){
	
	if (!isDefined(parameters)){
		return;
	}
	
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
}

function getUrlParameters() {
	
	if (isBlank(location.search)){
		return null;
	}
	
    var parameterNamesAndValues = location.search.substring(1, location.search.length).split('&');
    
    var urlParameters = {};
    
    for (var index = 0; index < parameterNamesAndValues.length; index++) {
        var parameterNameAndValue = parameterNamesAndValues[index].split('=');
        var name = decodeURIComponent(parameterNameAndValue[0]);
        var value = decodeURIComponent(parameterNameAndValue[1]);
        urlParameters[name] = value;
    }
    
    return urlParameters;
}

function getSelectionCriteriaAndInitialize(){
	
	$.ajax({url: 'nflpicks?target=selectionCriteria',
			contentType: 'application/json; charset=UTF-8'}
	)
	.done(function(data) {
		var selectionCriteriaContainer = $.parseJSON(data);
		
		var years = selectionCriteriaContainer.years;
		
		var yearOptions = [{label: 'All', value: 'all'}];

		for (var index = 0; index < years.length; index++){
			var year = years[index];
			yearOptions.push({label: year, value: year});
		}
		
		setOptionsInSelect('year', yearOptions);
		
		var players = selectionCriteriaContainer.players;
		
		var playerOptions = [{label: 'Everybody', value: 'all'}];
		
		for (var index = 0; index < players.length; index++){
			var player = players[index];
			playerOptions.push({label: player, value: player});
		}
		
		setOptionsInSelect('player', playerOptions);
		
		var teams = selectionCriteriaContainer.teams;
		
		teams.sort(function (teamA, teamB){
			
			if (teamA.abbreviation < teamB.abbreviation){
				return -1;
			}
			else if (teamA.abbreviation > teamB.abbreviation){
				return 1;
			}
			
			return 0;
		});
		
		var teamOptions = [{label: 'All', value: 'all'}];
		
		for (var index = 0; index < teams.length; index++){
			var team = teams[index];
			teamOptions.push({label: team.abbreviation, value: team.abbreviation});
		}
		
		setOptionsInSelect('team', teamOptions);
		
		initializeView();
	})
	.fail(function() {
	})
	.always(function() {
	});
}

function updateView(){
	
	var type = $('#type option:selected').val();
	
	updateSelectors(type);
	
	if ('picks' == type){
		updatePicks();
	}
	else if ('standings' == type) {
		updateRecords();
	}
	else if ('stats' == type){
		updateStats();
	}
}

function updateSelectors(type){
	
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
 * 
 * @param type
 * @returns
 */
function updateStatsSelectors(type){
	
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
	
	setPreviousType(type);
}

function getSelectedType(){
	return $('#type option:selected').val();
}

function setSelectedType(type){
	if (doesSelectHaveOptionWithValue('type', type)){
		$('#type').val(type);
	}
}

function getPreviousType(){
	return previousType;
}

function setPreviousType(newPreviousType){
	previousType = newPreviousType;
}

function getSelectedPlayer(){
	return $('#player option:selected').val();
}

function setSelectedPlayer(player){
	if (doesSelectHaveOptionWithValue('player', player)){
		$('#player').val(player);
	}
}

function getSelectedYear(){
	return $('#year option:selected').val();
}

function setSelectedYear(year){
	if (doesSelectHaveOptionWithValue('year', year)){
		$('#year').val(year);
	}
}

function getSelectedWeek(){
	return $('#week option:selected').val();
}

function setSelectedWeek(week){
	if (doesSelectHaveOptionWithValue('week', week)){
		$('#week').val(week);
	}
}

function getSelectedStatName(){
	return $('#statName option:selected').val();
}

function setSelectedStatName(statName){
	if (doesSelectHaveOptionWithValue('statName', statName)){
		$('#statName').val(statName);
	}
}

function getSelectedTeam(){
	return $('#team option:selected').val();
}

function setSelectedTeam(team){
	if (doesSelectHaveOptionWithValue('team', team)){
		$('#team').val(team);
	}
}

function updateRecords(){
	var player = getSelectedPlayer();
	var year = getSelectedYear();
	var week = getSelectedWeek();
	
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
		var records = standingsContainer.records;

		records.sort(function (a, b){
			if (a.wins > b.wins){
				return -1;
			}
			else if (a.wins < b.wins){
				return 1;
			}
			else {
				if (a.losses < b.losses){
					return -1;
				}
				else if (a.losses > b.losses){
					return 1;
				}
			}
			return 0;
		});
		
		var standingsGridHtml = createStandingsGridHtml(standingsContainer.records);
		$('#contentContainer').empty();
		$('#contentContainer').append(standingsGridHtml);
	})
	.fail(function() {
	})
	.always(function() {
	});
}

function hideAllPlayerOption(){
	$('#player option[value=all]').hide();
}

function showAllPlayerOption(){
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

function showStandingsSelectors(){
	$('#playerContainer').show();
	$('#yearContainer').show();
	$('#weekContainer').show();
}

function hideStandingsSelectors(){
	$('#playerContainer').hide();
	$('#yearContainer').hide();
	$('#weekContainer').hide();
}

function showPicksSelectors(){
	$('#playerContainer').show();
	$('#yearContainer').show();
	$('#weekContainer').show();
}

function hidePicksSelectors(){
	$('#playerContainer').hide();
	$('#yearContainer').hide();
	$('#weekContainer').hide();
}

function hideStatsSelectors(){
	hideStatNameContainer();
	$('#yearContainer').hide();
}

function updatePicks(){
	
	var player = getSelectedPlayer();
	var year = getSelectedYear();
	
	var parameters = getUrlParameters();
	
	var hasYearInUrl = false;
	if (isDefined(parameters) && isDefined(parameters.year)){
		hasYearInUrl = true;
	}
	
	if ('all' == year && !havePicksBeenShown && !hasYearInUrl){
		year = getYearForCurrentSeason();
		setSelectedYear(year);
	}
	
	var hasWeekInUrl = false;
	if (isDefined(parameters) && isDefined(parameters.week)){
		hasWeekInUrl = true;
	}
	
	var week = getSelectedWeek();
	if ('all' == week && !havePicksBeenShown && !hasWeekInUrl){
		week = "1";
		setSelectedWeek(week);
	}
	var team = getSelectedTeam();
	
	havePicksBeenShown = true;
	
	//change this to take fromYear and toYear?
	//or just put in the weeks ...
	//regular season = 1,2,3, ... 17
	//playoffs = 18 ... 21
	
	var weekToUse = week;
	if ('regular-season' == week){
		weekToUse = '1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17';
	}
	else if ('playoffs' == week){
		weekToUse = '18,19,20,21';
	}
	
	$.ajax({url: 'nflpicks?target=compactPicksGrid&player=' + player + '&year=' + year + '&week=' + weekToUse + '&team=' + team,
		contentType: 'application/json; charset=UTF-8'}
	)
	.done(function(data) {
		var picksGrid = $.parseJSON(data);
		var picksGridHtml = createPicksGridHtml(picksGrid);
		$('#contentContainer').empty();
		$('#contentContainer').append(picksGridHtml);
	})
	.fail(function() {
	})
	.always(function() {
	});
}

var statsData = null;

function updateStats(){
	
	var statName = getSelectedStatName();
	var player = getSelectedPlayer();

	if (statName == 'weekRecordsByPlayer' || statName == 'pickAccuracy'){
		if (!isDefined(player) || 'all' == player){
			var firstRealPlayer = $('#player option')[1].value;
			setSelectedPlayer(firstRealPlayer);
		}
	}
	else if (statName == 'champions' || statName == 'championshipStandings'){
		setSelectedPlayer('all');
		setSelectedYear('all');
	}

	player = getSelectedPlayer();
	
	var year = getSelectedYear();
	var week = getSelectedWeek();
	var team = getSelectedTeam();
	
	var weekToUse = week;
	if ('regular-season' == week){
		weekToUse = '1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17';
	}
	else if ('playoffs' == week){
		weekToUse = '18,19,20,21';
	}
	
	$.ajax({url: 'nflpicks?target=stats&statName=' + statName + '&year=' + year + '&player=' + player + '&week=' + weekToUse + '&team=' + team,
			contentType: 'application/json; charset=UTF-8'}
	)
	.done(function(data) {
		statsData = data;
		
		var statsHtml = '';
		
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
			sortWeekRecords(weekRecords);
		
			statsHtml = createWeeksWonHtml(weekRecords);
		}
		else if ('weeksWonByWeek' == statName){
			
			var weeksWonByWeek = $.parseJSON(data);
			
			statsHtml = createWeeksWonByWeek(weeksWonByWeek);
		}
		else if ('weekRecordsByPlayer' == statName){
			//add in the player here and get the default
			//this will show somebody's records throughout a season or through all time
			//along with the rank of each one...
			//
			//Year	Week	Record
			//All	All		298 - 90 (2nd)
			//2017	3		9 - 7 (5th)
			
			var weekRecords = $.parseJSON(data);
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
		
		$('#contentContainer').empty();
		$('#contentContainer').append(statsHtml);
	})
	.fail(function() {
	})
	.always(function() {
	});
	
}

function getPickForGame(picksGrid, playerId, gameId){
	
	for (var index = 0; index < picksGrid.picks.length; index++){
		var pick = picksGrid.picks[index];
		
		if (pick.game.id == gameId && pick.player.id == playerId){
			return pick;
		}
	}
	
	return null;
}

function getPicksForGame(picksGrid, gameId){
	
	var picksForGame = [];
	
	for (var index = 0; index < picksGrid.picks.length; index++){
		var pick = picksGrid.picks[index];
		
		if (pick.game.id == gameId){
			picksForGame.push(pick);
		}
	}
	
	return picksForGame;
	
}

function hasTies(records){
	
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

function createStandingsGridHtml(records){
	
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

function createPicksGridHtml(picksGrid){
	
	var yearHeader = '';
	var weekHeader = '';
	
	var yearSelected = isSpecificYearSelected();
	if (!yearSelected){
		yearHeader = '<th align="left" class="table-header">Year</th>';
	}
	
	var selectedWeek = getSelectedWeek();
	var weekSelected = !('all' == selectedWeek || 'regular-season' == selectedWeek || 'playoffs' == selectedWeek);
	if (!weekSelected){
		weekHeader = '<th align="left" class="table-header">Week</th>';
	}
	
	var picksGridHtml = '';
	
	var gridHeaderHtml = '<thead>' +
							yearHeader + 
							weekHeader + 
						 	'<th align="left" class="table-header">Game</th>';
	
	for (var index = 0; index < picksGrid.players.length; index++){
		var player = picksGrid.players[index];
		
		gridHeaderHtml = gridHeaderHtml + '<th align="left" colspan="2" class="table-header">' + player + '</th>';
	}
	
	gridHeaderHtml = gridHeaderHtml + '</thead>';

	var playerRecords = [];
	
	for (var index = 0; index < picksGrid.players.length; index++){
		var player = picksGrid.players[index];
		var playerRecord = {player: player,
							wins: 0,
							losses: 0,
							ties: 0};
		playerRecords[index] = playerRecord;
	}
	
	var pickRowsHtml = '';
	
	for (var index = 0; index < picksGrid.picks.length; index++){
		var pick = picksGrid.picks[index];
		
		var rowClassName = 'even-row';
		if (index % 2 == 1){
			rowClassName = 'odd-row';
		}
		
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
		
		//on the bottom row.
		var isBottomRow = false;
		if (index + 1 == picksGrid.picks.length){
			isBottomRow = true;
		}
		
		var year = '';
		var week = '';
		
		if (!yearSelected){
			var cssClassToUse = 'first-pick-cell';
			if (isBottomRow){
				cssClassToUse = 'first-pick-cell-bottom';
			}
			
			year = '<td class="' + cssClassToUse + '">' + pick.year + '</td>';
		}
		
		if (!weekSelected){
			
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
		
			week = '<td class="' + cssClassToUse + '">' + pick.weekNumber + '</td>';
		}

		var isPickFirstCell = weekSelected && yearSelected;
		
		var pickCssClassToUse = null;
		
		if (!isPickFirstCell && !isBottomRow){
			pickCssClassToUse = 'pick-cell';
		}
		else if (!isPickFirstCell && isBottomRow){
			pickCssClassToUse = 'pick-cell-bottom';
		}
		else if (isPickFirstCell && !isBottomRow){
			pickCssClassToUse = 'first-pick-cell';
		}
		else if (isPickFirstCell && isBottomRow){
			pickCssClassToUse = 'first-pick-cell-bottom';
		}
		
		var gameRow = '<tr class="' + rowClassName + '">' + 
						year +
						week +
						'<td class="' + pickCssClassToUse + '">' + 
							'<span class="' + awayTeamClass + '">' + pick.awayTeamAbbreviation + '</span>' + 
							' @ ' + 
							'<span class="' + homeTeamClass + '">' + pick.homeTeamAbbreviation + '</span>' +  
						'</td>';
		
		var pickGameClass = '';
		var pickResultClass = 'pick-cell';
		
		if (isBottomRow){
			pickGameClass = 'pick-game-bottom';
			pickResultClass = 'pick-cell-bottom';
		}
	
		for (var playerIndex = 0; playerIndex < picksGrid.players.length; playerIndex++){
			var playerName = picksGrid.players[playerIndex];
			
			var pickForPlayer = null;
			
			for (var pickIndex = 0; pickIndex < pick.playerPicks.length; pickIndex++){
				var playerPick = pick.playerPicks[pickIndex];
				
				if (playerPick.player == playerName){
					pickForPlayer = playerPick.pick;
					break;
				}
			}
			
			var doesGameHaveResult = false;
			if (isDefined(pick.winningTeamAbbreviation)){
				doesGameHaveResult = true;
			}
			
			var team = '&nbsp;';
			var result = '';
			var winnerOrLoserClass = '';
			
			if (isDefined(pickForPlayer)){
				team = pickForPlayer;
			}
			
			if (doesGameHaveResult){
				
				if (isDefined(pickForPlayer)){
					if (pick.winningTeamAbbreviation == 'TIE'){
						result = 'T';
					}
					else if (pick.winningTeamAbbreviation == pickForPlayer){
						result = 'W';
					}
					else {
						result = 'L';
					}
				}
				
				//If they didn't make a pick, that doesn't qualify as
				//a loss.  We don't count it as anything.  When retrieving
				//the records for the standings, we don't count missing picks as
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
			
			gameRow = gameRow + '<td class="' + pickGameClass + '">' + 
									'<span class="' + winnerOrLoserClass + '">' + team + '</span>' + 
								'</td>' 
									+ 
								'<td class="' + pickResultClass + '">' +
									'<span class="' + winnerOrLoserClass + '">' + result + '</span>' + 
								'</td>'
									;
		}
		
		gameRow = gameRow + '</tr>';
		
		pickRowsHtml = pickRowsHtml + gameRow;
	}

	var weekRecordHtml = '';
	
	for (var index = 0; index < playerRecords.length; index++){
		var playerRecord = playerRecords[index];
		var pickRecordRowCss = 'pick-record';
		
		if (index + 1 >= playerRecords.length){
			pickRecordRowCss = 'last-pick-record';
		}
		
		var tiesString = '';
		if (isDefined(playerRecord.ties) && playerRecord.ties > 0){
			tiesString = ' - ' + playerRecord.ties;
		}
		
		var playerRecordHtml = '<td colspan="2" class="' + pickRecordRowCss + '">' + playerRecord.wins + ' - ' + playerRecord.losses + tiesString + '</td>';
		weekRecordHtml = weekRecordHtml + playerRecordHtml;
	}
	
	var extra = '';
	
	if (!yearSelected){
		extra = extra + '<td class="first-pick-cell-bottom"></td>';
	}
	
	if (!weekSelected){
		var cssClassToUse = 'pick-cell-bottom';
		if (yearSelected){
			cssClassToUse = 'first-pick-cell-bottom';
		}
		
		extra = extra + '<td class="' + cssClassToUse + '"></td>';
	}
	
	var xCssClassToUse = 'pick-cell-bottom';
	if (yearSelected && weekSelected){
		xCssClassToUse = 'first-pick-cell-bottom';
	}
	
	weekRecordHtml = '<tr>' + extra + '<td class="' + xCssClassToUse + '"></td>' +  weekRecordHtml + '</tr>';
	
	var gridBodyHtml = '<tbody>' + weekRecordHtml + pickRowsHtml + '</tbody>';
	
	picksGridHtml = '<table class="picks-table" align="center">' + gridHeaderHtml + gridBodyHtml + '</table>';
	
	return picksGridHtml;
}

function toggleVisibilty(id){
	
	var isVisible = $('#' + id).is(':visible');
	
	if (isVisible){
		$('#' + id).hide();
	}
	else {
		$('#' + id).show();
	}
}

function toggleShowWeeks(index){
	
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

function createWeeksWonHtml(weekRecords){
	//sort on the number of weeks won
	//rank on that
	
	var selectedYear = getSelectedYear();
	var showYear = false;
	if ('all' == selectedYear){
		showYear = true;
	}
	
	var weeksWonHtml = '<table class="standings-table">' + 
							'<thead class="standings-table-head">' + 
								'<tr class="standings-table-row">' + 
									'<th class="standings-table-player-header"></th>' +
									'<th class="standings-table-header">Weeks won</th>' +
									//'<th>Weeks</th>' +
								'</tr>' + 
							'</thead>';
	
	var weeksWonTableBody = '';
	
	for (var index = 0; index < weekRecords.length; index++){
		var weekRecord = weekRecords[index];
		
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
		
		var rankText = recordRank.rank;
		
		if (recordRank.tie){
			rankText = rankText + 'T';
		}
		
		
		weeksWonTableBody = weeksWonTableBody + 
							'<tr class="standings-table-row">' + 
								'<td class="standings-table-player-cell">' + rankText + '. ' + weekRecord.player.name + '</td>';

		var numberOfWeeksWon = weekRecord.weekRecords.length;
		if (weekRecord.weekRecords.length < 10){
			numberOfWeeksWon = numberOfWeeksWon + '&nbsp;';
		}
		var detailId = 'week-records-' + index;
		var weekRecordsHtml = '<div style="">' + numberOfWeeksWon + ' <a id="show-weeks-link-' + index + '" href="javascript:" onClick="toggleShowWeeks(' + index + ')" style="margin-left: 20px; float:right;">show weeks</a></div>' + 
							  '<div id="' + detailId + '" style="display: none;"><ul class="standings-table-cell-list">';

		
		sortWeekRecordsBySeasonAndWeek(weekRecord.weekRecords);
		
		for (var bIndex = 0; bIndex < weekRecord.weekRecords.length; bIndex++){
			var record = weekRecord.weekRecords[bIndex];

			var ties = '';
			if (record.record.ties > 0){
				ties = ' - ' + record.record.ties;
			}
			var year = '';
			if (showYear){
				year = record.season.year + ', ';
			}
			//createPicksLink
			//createPicksLink(linkText, year, week, team, player)
			
			var recordText = year + record.week.label + ' (' + record.record.wins + ' - ' + record.record.losses +
							 ties + ')';
			
			var picksLink = createPicksLink(recordText, record.season.year, record.week.weekNumber, null, weekRecord.player.name);
			
			weekRecordsHtml = weekRecordsHtml + '<li>' + picksLink + '</li>';
		}
		
		weekRecordsHtml = weekRecordsHtml + '</ul></div>';
		
		weeksWonTableBody = weeksWonTableBody + '<td class="standings-table-cell">' + weekRecordsHtml + '</td></tr>';
		
	}
	
	weeksWonHtml = weeksWonHtml + '<tbody class="standings-table-body">' + weeksWonTableBody + '</tbody></table>';
	
	return weeksWonHtml;
}

function rank(object, list, comparisonFunction, sameObjectFunction){
	
	var objectRank = {rank: 1, tie: false};
	
	//for every object it's less than, its rank goes up 1
	//it starts in last...
	//
	//will be O(n^2) without sorting...
	
	var numberOfRecordsBetter = 0;
	var tie = false;
	
	for (var index = 0; index < list.length; index++){
		var currentObject = list[index];
		
		var comparisonResult = comparisonFunction(object, currentObject);
		
		if (comparisonResult > 0){
			objectRank.rank++;
			//numberOfRecordsBetter++;
		}
		else if (comparisonResult == 0){

			if (objectRank.tie == false){
				if (isDefined(sameObjectFunction)){
					var isSameObject = sameObjectFunction(object, currentObject);
					
					if (!isSameObject){
						objectRank.tie = true;
					}
				}
				else {
					objectRank.tie = true;
				}
			}
		}
	}
	
	//objectRank.rank = 1 + numberOfRecordsBetter; 
	
	return objectRank;
}

function sortWeekRecords(weekRecords){
	
	weekRecords.sort(function (a, b){
		if (a.weekRecords.length > b.weekRecords.length){
			return -1;
		}
		else if (a.weekRecords.length < b.weekRecords.length){
			return 1;
		}
		return 0;
	});
}

function sortWeekRecordsBySeasonAndWeek(weekRecords){
	
	weekRecords.sort(function (a, b){
		var yearA = parseInt(a.season.year);
		var yearB = parseInt(b.season.year);
		
		if (yearA < yearB){
			return -1;
		}
		else if (yearA > yearB){
			return 1;
		}
		else {
			var weekA = a.week.weekNumber;
			var weekB = b.week.weekNumber;
			
			if (weekA < weekB){
				return -1;
			}
			else if (weekA > weekB){
				return 1;
			}
		}
		
		return 0;
	});
}

function isSpecificYearSelected(){

	var selectedYear = getSelectedYear();
	
	if ('all' == selectedYear){
		return false;
	}
	
	return true;
}

function isSpecificTeamSelected(){
	
	var selectedTeam = getSelectedTeam();
	
	if ('all' == selectedTeam){
		return false;
	}
	
	return true;
}

function isSpecificWeekSelected(){

	var selectedWeek = getSelectedWeek();
	
	if ('all' == selectedWeek){
		return false;
	}
	
	return true;
}

function createWeekRecordsByPlayerHtml(weekRecords){
	
	var tiesHeader = '';
	
	var xHasTies = false;
	for (var index = 0; index < weekRecords.length; index++){
		var weekRecord = weekRecords[index];
		
		if (weekRecord.record.ties > 0){
			xHasTies = true;
			break;
		}
	}
	
	if (xHasTies){
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
		
		var tiesCell = '';
		
		if (xHasTies){
			tiesCell = '<td class="standings-table-cell">' + weekRecord.record.ties + '</td>';
		}
		
		var percentage = weekRecord.record.wins / (weekRecord.record.wins + weekRecord.record.losses);
		var percentageString = '';
		if (!isNaN(percentage)){
			percentageString = percentage.toPrecision(3);
		}
		
		var yearCell = '';
		if (!aYearIsSelected){
			yearCell = '<td class="standings-table-player-cell">' + weekRecord.season.year + '</td>';
		}
		
		var weekLabel = shortenWeekLabel(weekRecord.week.label);
		
		var playerPicksLink = createPicksLink(weekLabel, weekRecord.season.year, weekRecord.week.weekNumber, null, weekRecord.player.name);
		
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

function createWeekStandingsHtml(playerWeekRecords){
	
	var standingsHtml = '';
	
	var isYearSelected = true;
	var yearHeader = '';
	var selectedYear = getSelectedYear();
	if ('all' == selectedYear){
		isYearSelected = false;
		yearHeader = '<th class="standings-table-header">Year</th>';
	}
	
	var isWeekSelected = true;
	var weekHeader = '';
	var selectedWeek = getSelectedWeek();
	if ('all' == selectedWeek){
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
		
		var percentage = playerWeekRecord.record.wins / (playerWeekRecord.record.wins + playerWeekRecord.record.losses);
		var percentageString = '';
		if (!isNaN(percentage)){
			percentageString = percentage.toPrecision(3);
		}
		
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

function createChampionsHtml(championships){
	
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
		
		var percentage = championship.record.wins / (championship.record.wins + championship.record.losses);
		var percentageString = '';
		if (!isNaN(percentage)){
			percentageString = percentage.toPrecision(3);
		}
		
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

function createChampionshipStandingsHtml(playerChampionshipsList){
	
	var championshipsStandingsHeaderHtml = '<thead class="standings-table-head">' +
										  	'<tr class="standings-table-row">' +
										  		'<th class="standings-table-player-header"></th>' +
										  		'<th class="standings-table-header">Championships</th>' +
										  		'<th class="standings-table-header">Years</th>' +
										  	'</tr>' +
										  '</thead>';
										  		
	
	var championshipsStandingsBodyHtml = '<tbody class="standings-table-body">';
	
	playerChampionshipsList.sort(function (a, b){
		
		if (a.championships.length > b.championships.length){
			return -1;
		}
		else if (a.championships.length < b.championships.length){
			return 1;
		}
		
		return 0;
	});
	
	for (var index = 0; index < playerChampionshipsList.length; index++){
		var playerChampionships = playerChampionshipsList[index];
		
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

function createWeeksWonByWeek(weeksWonByWeek){
	
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

function sortPlayersByName(players){
	
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

function createPickAccuracySummariesHtml(pickAccuracySummaries){

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
		
		var teamCell = '';
		if (!specificTeamSelected){
			teamCell = '<td class="standings-table-cell">' + pickAccuracySummary.team.abbreviation + '</td>';
		}
		
		var percentage = getPercentage(pickAccuracySummary.timesRight, pickAccuracySummary.timesRight + pickAccuracySummary.timesWrong);

		var detailId = 'pick-accuracy-details-' + index;

		/*
		 actualLosses: 15
	actualWins: 17
	player: Object { name: "Benny boy", id: 1 }
	predictedLosses: 14
	predictedWins: 13
	team: Object { name: "Baltimore Ravens", nickname: "Ravens", id: 5, … }
	timesPickedToLoseRight: 8
	timesPickedToLoseWrong: 6
	timesPickedToWinRight: 9
	timesPickedToWinWrong: 4
	timesRight: 17
	timesWrong: 10

	Team (if not picked)	Right	Wrong	% 	Details

		Details
			Actual record:
			Predicted record:
			Times picked to win: (record in parentheses)
			Times picked to lose: (record in parentheses)
		 */
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
		var recordPicksLink = createPicksLink('Picks', year, null, pickAccuracySummary.team.abbreviation, pickAccuracySummary.player.name);
		
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

function getPercentage(value, total){
	var percentage = value / total;
	var percentageString = '';
	
	if (!isNaN(percentage)){
		percentageString = percentage.toPrecision(3);
	}
	
	return percentageString;
}

function sortPickAccuracySummariesByTimesRight(pickAccuracySummaries){
	
	pickAccuracySummaries.sort(function (pickAccuracySummaryA, pickAccuracySummaryB){
		
		if (pickAccuracySummaryA.timesRight > pickAccuracySummaryB.timesRight){
			return -1;
		}
		else if (pickAccuracySummaryA.timesRight < pickAccuracySummaryB.timesRight){
			return 1;
		}
		
		return 0;
		
	});
}

function toggleShowPickAccuracyDetails(index){
	
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

function createPicksLink(linkText, year, week, team, player){
	
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

function showPickView(year, week, team, player){

	havePicksBeenShown = true;
	
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

function shortenWeekLabel(label){
	
	if ('Conference Championship' == label){
		return 'Conf Champ';
	}
	
	return label;
}

function getYearForCurrentSeason(){
	var currentDate = new Date();
	
	var year = currentDate.getFullYear();
	
	return year;
}