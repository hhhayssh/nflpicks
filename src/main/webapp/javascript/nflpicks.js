
var teams = null;
var picks = null;

var picksGrid = null;

var previousType = null;

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
	
	if (previousType == type && type != 'stats'){
		return;
	}
	
	previousType = type;
	
	hideStandingsSelectors();
	hidePicksSelectors();
	hideStatsSelectors();
	
	if ('picks' == type){
		hideAllWeekOption();
		hideAllYearOption();
		showPicksSelectors();
	}
	else if ('standings' == type){
		showAllWeekOption();
		showAllYearOption();
		showStandingsSelectors();
	}
	else if ('stats' == type){
		showAllWeekOption();
		showAllYearOption();
		showStatsSelectors();
	}
}

function getSelectedType(){
	return $('#type option:selected').val();
}

function setSelectedType(type){
	if (doesSelectHaveOptionWithValue('type', type)){
		$('#type').val(type);
	}
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

function updateRecords(){
	var player = getSelectedPlayer();
	var year = getSelectedYear();
	var week = getSelectedWeek();
	
	$.ajax({url: 'nflpicks?target=standings&players=' + player + '&years=' + year + '&weeks=' + week,
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

function showStatsSelectors(){
	$('#statsNameContainer').show();
	$('#yearContainer').show();
	
	var statName = getSelectedStatName();

	if ('champions' == statName){
		$('#playerContainer').hide();
		$('#yearContainer').hide();
		$('#weekContainer').hide();
	}
	else if ('weekRecordsByPlayer' == statName){
		$('#playerContainer').show();
		hideAllPlayerOption();
		
		$('#weekContainer').show();
		showAllWeekOption();
	}
	else if ('bestWeeks' == statName){
		$('#playerContainer').show();
		showAllPlayerOption();
		$('#weekContainer').show();
		showAllWeekOption();
	}
	else if ('weeksWonByWeek' == statName){
		$('#weekContainer').show();
		showAllWeekOption();
	}
}

function hideStatsSelectors(){
	$('#statsNameContainer').hide();
	$('#yearContainer').hide();
}

function updatePicks(){
	var player = getSelectedPlayer();
//	if ('all' == player){
//		//Get the first player option's value
//		player = $('#player option')[1].value;
//		setSelectedPlayer(player);
//	}
	var year = getSelectedYear();
	if ('all' == year){
		year = $('#year option')[1].value;
		setSelectedYear(year);
	}
	var week = getSelectedWeek();
	if ('all' == week){
		//Get the first player option's value
		week = $('#week option')[1].value;
		setSelectedWeek(week);
	}
	
	$.ajax({url: 'nflpicks?target=picksGrid&player=' + player + '&year=' + year + '&week=' + week,
		contentType: 'application/json; charset=UTF-8'}
	)
	.done(function(data) {
		picksGrid = $.parseJSON(data);
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
	var year = getSelectedYear();
	var player = getSelectedPlayer();
	var week = getSelectedWeek();
	
	//don't always want to do this ... need to base it on stat name
	if (statName != 'bestWeeks' && statName != 'champions' && statName != 'weeksWonByWeek'){
		if ('all' == player){
			player = $('#player option')[1].value;
			setSelectedPlayer(player);
		}
	}
	
	$.ajax({url: 'nflpicks?target=stats&statName=' + statName + '&year=' + year + '&player=' + player + '&week=' + week,
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
			
			console.log('aaaa ...');
			console.log(weeksWonByWeek);
			
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
		else if ('bestWeeks' == statName){
			
			var playerWeekRecords = $.parseJSON(data);
			
			statsHtml = createBestWeeksHtml(playerWeekRecords);
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
	
	var picksGridHtml = '';
	
	var gridHeaderHtml = '<thead>' +
						 	'<th align="left" class="table-header">Game</th>';
	
	for (var index = 0; index < picksGrid.players.length; index++){
		var player = picksGrid.players[index];
		
		gridHeaderHtml = gridHeaderHtml + '<th align="left" colspan="2" class="table-header">' + player.name + '</th>';
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
	
	for (var index = 0; index < picksGrid.games.length; index++){
		var game = picksGrid.games[index];
		
		var rowClassName = 'even-row';
		if (index % 2 == 1){
			rowClassName = 'odd-row';
		}
		
		var pickGameClass = 'pick-game';
		var pickTeamClass = 'pick-team';
		var pickResultClass = 'pick-result';
		
		if (index + 1 >= picksGrid.games.length){
			pickGameClass = 'last-pick-game';
			pickTeamClass = 'last-pick-team';
			pickResultClass = 'last-pick-result';
		}
		
		var homeTeamClass = '';
		var awayTeamClass = '';
		if (isDefined(game.winningTeam) && game.winningTeam.id != 0){
			if (game.winningTeam.id == game.awayTeam.id){
				awayTeamClass = 'winner';
			}
			else if (game.winningTeam.id == game.homeTeam.id){
				homeTeamClass = 'winner';
			}
		}
		else if (game.tie == true){
			awayTeamClass = 'tie';
			homeTeamClass = 'tie';
		}
		
		var gameRow = '<tr class="' + rowClassName + '">' + 
						'<td class="' + pickGameClass + '">' + 
							'<span class="' + awayTeamClass + '">' + game.awayTeam.abbreviation + '</span>' + 
							' @ ' + 
							'<span class="' + homeTeamClass + '">' + game.homeTeam.abbreviation + '</span>' +  
						'</td>';
	
		var gameId = game.id;
		
		for (var playerIndex = 0; playerIndex < picksGrid.players.length; playerIndex++){
			var playerId = picksGrid.players[playerIndex].id;
			var pickForGame = getPickForGame(picksGrid, playerId, gameId);
			
			var doesGameHaveResult = false;
			if ((isDefined(game.winningTeam) && game.winningTeam.id != 0) || game.tie){
				doesGameHaveResult = true;
			}
			
			var team = '';
			var result = '';
			var winnerOrLoserClass = '';
			
			team = 'NONE';
			
			if (isDefined(pickForGame) && isDefined(pickForGame.team)){
				team = pickForGame.team.abbreviation;
			}
			
			if (doesGameHaveResult){
				if (isDefined(pickForGame) && isDefined(pickForGame.result)){
					result = pickForGame.result;
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
			
			gameRow = gameRow + '<td class="' + pickTeamClass + '">' + 
									'<span class="' + winnerOrLoserClass + '">' + team + '</span>' + 
								'</td>' + 
								'<td class="' + pickResultClass + '">' +
									'<span class="' + winnerOrLoserClass + '">' + result + '</span>' + 
								'</td>';
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
	
	weekRecordHtml = '<tr><td class="last-pick-game"></td>' + weekRecordHtml + '</tr>';
	
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
			var recordHtml = year + record.week.label + ' (' + record.record.wins + ' - ' + record.record.losses +
							 ties + ')';
			
			weekRecordsHtml = weekRecordsHtml + '<li>' + recordHtml + '</li>';
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
			var weekA = a.week.week_number;
			var weekB = b.week.week_number;
			
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
		
		var row = '<tr class="standings-table-row">' +
					yearCell +
					'<td class="' + weekClass + '">' + shortenWeekLabel(weekRecord.week.label) + '</td>' +
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

function createBestWeeksHtml(playerWeekRecords){
	
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
		tiesHeader = '<th class="standings-table-header">T</th>';
	}
	
	var standingsHeaderHtml = '<thead class="standings-table-head">' +
						 	'<th class="standings-table-player-header"></th>' +
						 	yearHeader + 
						 	weekHeader +
						 	'<th class="standings-table-header">W</th>' + 
						 	'<th class="standings-table-header">L</th>' +
						 	tiesHeader + 
						 	'<th class="standings-table-header">%</th>';
	
	
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
			weekCell = '<td class="standings-table-cell">' + shortenWeekLabel(playerWeekRecord.week.label) + '</td>';
		}
		
		var tiesCell = '';
		if (areThereAnyTies){
			tiesCell = '<td class="standings-table-cell">' + playerWeekRecord.record.ties + '</td>';
		}
		
		rowsHtml = rowsHtml + 
					   '<tr class="standings-table-row">' +
						'<td class="standings-table-player-cell">' + rankText + '. ' + playerWeekRecord.player.name + '</td>' +
						yearCell +
						weekCell +
						'<td class="standings-table-cell">' + playerWeekRecord.record.wins + '</td>' +
						'<td class="standings-table-cell">' + playerWeekRecord.record.losses + '</td>' +
						tiesCell + 
						'<td class="standings-table-cell">' + percentageString + '</td>';
		
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
		
		var playerHtml = '<ul class="standings-table-cell-list">';
		for (var playerIndex = 0; playerIndex < weekRecord.player.length; playerIndex++){
			var player = weekRecord.player[playerIndex];
			
			var plHtml = '<li>' + player.name + '</li>';
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

function shortenWeekLabel(label){
	
	if ('Conference Championship' == label){
		return 'Conf Champ';
	}
	
	return label;
}