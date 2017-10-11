
var teams = null;
var picks = null;

var picksGrid = null;

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
}

function getUrlParameters() {
	
	if (isBlank(location.search)){
		return null;
	}
	
    var parameterNamesAndValues = location.search.substring(1, location.search.length).split('&');
    
    var urlParameters = {};
    
    for (var index = 0; index < parameterNamesAndValues.length; index++) {
        var parameterNameAndValue = parameterNamesAndValues[index].split('=');
        var name = decodeURIComponent(parameterNameAndValue[0]).toLowerCase();
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

	if ('picks' == type){
		hideAllWeekOption();
		updatePicks();
	}
	else if ('standings' == type) {
		showAllWeekOption();
		updateRecords();
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

function updatePicks(){
	var player = getSelectedPlayer();
//	if ('all' == player){
//		//Get the first player option's value
//		player = $('#player option')[1].value;
//		setSelectedPlayer(player);
//	}
	var year = getSelectedYear();
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
		tiesHeader = '<th align="left">Ties</th>';
	}
	
	var standingsHeaderHtml = '<thead>' +
						 	'<th class="standings-header"></th>' +
						 	'<th class="standings-header">Wins</th>' + 
						 	'<th class="standings-header">Losses</th>' +
						 	tiesHeader + 
						 	'<th class="standings-header">Win %</th>' + 
						 	'<th class="standings-header">GB</th>';
	
	
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
	//		1. If there's a next record and it has the same number of losses as this one, then
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
		//If the next record has the same number of losses, then it'll be the same as now because they're
		//tied.
		//Otherwise, if the next record doesn't, the next rank will be whatever this one's would have
		//been without ties + 1.  If there weren't any ties, then this record's rank would be the "tieIndependentRank".
		//So, that means the next rank would be that + 1.
		nextRecord = null;
		if (index + 1 < records.length){
			nextRecord = records[index + 1];
			
			if (record.losses == nextRecord.losses){
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
			//Base the games back on wins instead of losses since they
			//don't get a loss if they don't pick.
			var calculatedGamesBack = topWins - record.wins;
			gamesBack = calculatedGamesBack + '';
		}
		
		var tiesCell = '';
		if (areThereAnyTies){
			tiesCell = '<td class="records-data-cell">' + record.ties + '</td>';
		}
		
		rowsHtml = rowsHtml + 
					   '<tr>' +
						'<td class="records-cell">' + rankText + '. ' + record.player.name + '</td>' +
						'<td class="records-data-cell">' + record.wins + '</td>' +
						'<td class="records-data-cell">' + record.losses + '</td>' +
						tiesCell + 
						'<td class="records-data-cell">' + percentageString + '</td>' +
						'<td class="records-data-cell">' + gamesBack + '</td>';
		
		rowsHtml = rowsHtml + '</tr>';
		
		//Keep the current rank as the previous for the next time through.
		previousRank = rank;
		
	}
	
	var standingsBodyHtml = '<tbody>' + rowsHtml + '</tbody>';
	
	standingsHtml = '<table align="center">' + standingsHeaderHtml + standingsBodyHtml + '</table>';
	
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