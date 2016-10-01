
var teams = null;
var picks = null;

var picksGrid = null;

$(document).ready(
	function(){
		updateView();
});

function showPicksCriteriaContainer(){
	$('#picksPlayerContainer').show();
	$('#picksYearContainer').show();
	$('#picksWeekContainer').show();
}

function hidePicksCriteriaContainer(){
	$('#picksPlayerContainer').hide();
	$('#picksYearContainer').hide();
	$('#picksWeekContainer').hide();
}

function showRecordsCriteriaContainer(){
	$('#recordsPlayerContainer').show();
	$('#recordsYearContainer').show();
	$('#recordsWeekContainer').show();
}

function hideRecordsCriteriaContainer(){
	$('#recordsPlayerContainer').hide();
	$('#recordsYearContainer').hide();
	$('#recordsWeekContainer').hide();
}

function updateView(){
	
	var viewType = $('#viewType option:selected').val();

	if ('picks' == viewType){
		hideRecordsCriteriaContainer();
		showPicksCriteriaContainer();
		updatePicks();
	}
	else if ('records' == viewType) {
		showRecordsCriteriaContainer();
		hidePicksCriteriaContainer();
		updateRecords();
	}
	
}

function updateRecords(){
	var player = $('#recordsPlayer option:selected').val();
	var year = $('#recordsYear option:selected').val();
	var week = $('#recordsWeek option:selected').val();
	
	$.ajax({url: 'nflpicks?target=records&players=' + player + '&years=' + year + '&weeks=' + week,
		contentType: 'application/json; charset=UTF-8'}
	)
	.done(function(data) {
		var recordsContainer = $.parseJSON(data);
		var records = recordsContainer.records;

		records.sort(function (a, b){
			if (a.wins > b.wins){
				return -1;
			}
			else if (a.wins < b.wins){
				return 1;
			}
			return 0;
		});
		
		var recordsGridHtml = createRecordsGridHtml(recordsContainer.records);
		$('#contentContainer').empty();
		$('#contentContainer').append(recordsGridHtml);
	})
	.fail(function() {
	})
	.always(function() {
	});
}

function updatePicks(){
	var player = $('#picksPlayer option:selected').val();
	var year = $('#picksYear option:selected').val();
	var week = $('#picksWeek option:selected').val();
	
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

function createRecordsGridHtml(records){
	
	var recordsGridHtml = '';
	
	var gridHeaderHtml = '<thead>' +
						 	'<th class="records-header"></th>' +
						 	'<th class="records-header">Wins</th>' + 
						 	'<th class="records-header">Losses</th>' +
						 	'<th class="records-header">Win %</th>' + 
						 	'<th class="records-header">GB</th>';
	
	var areThereAnyTies = hasTies(records);
	
	if (areThereAnyTies){
		gridHeaderHtml = gridHeaderHtml + '<th align="left">Ties</th>';
	}
	
	gridHeaderHtml = gridHeaderHtml + '</thead>';
	
	var gridRowsHtml = '';
	
	var topWins = records[0].wins;
	var topLosses = records[0].losses;
	
	for (var index = 0; index < records.length; index++){
		var record = records[index];
		var rank = index + 1;
		var percentage = record.wins / (record.wins + record.losses);
		var percentageString = percentage.toPrecision(3);
		var gamesBack = '';
		
		if (record.losses == topLosses && record.wins == topWins){
			gamesBack = '-';
		}
		else {
			var calculatedGamesBack = record.losses - topLosses;
			gamesBack = calculatedGamesBack + '';
		}
		
		gridRowsHtml = gridRowsHtml + 
					   '<tr>' +
						'<td class="records-cell">' + rank + '. ' + record.player.name + '</td>' +
						'<td class="records-data-cell">' + record.wins + '</td>' +
						'<td class="records-data-cell">' + record.losses + '</td>' +
						'<td class="records-data-cell">' + percentageString + '</td>' +
						'<td class="records-data-cell">' + gamesBack + '</td>';
		
		if (areThereAnyTies){
			gridRowsHtml = gridRowsHtml + '<td>' + record.ties + '</td>';
		}
		
		gridRowsHtml = gridRowsHtml + '</tr>';
	}
	
	var gridBodyHtml = '<tbody>' + gridRowsHtml + '</tbody>';
	
	recordsGridHtml = '<table align="center">' + gridHeaderHtml + gridBodyHtml + '</table>';
	
	return recordsGridHtml;
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
		if (isDefined(game.winningTeamId) && game.winningTeamId != 0){
			if (game.winningTeamId == game.awayTeam.id){
				awayTeamClass = 'winner';
			}
			else if (game.winningTeamId = game.homeTeam.id){
				homeTeamClass = 'winner';
			}
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
			if (isDefined(game.winningTeamId) && game.winningTeamId != 0){
				doesGameHaveResult = true;
			}
			
			var team = '';
			var result = '';
			var winnerOrLoserClass = '';
			if (doesGameHaveResult){
				team = 'NONE';
				if (isDefined(pickForGame) && isDefined(pickForGame.team)){
					team = pickForGame.team.abbreviation;
				}
				if (isDefined(pickForGame) && isDefined(pickForGame.result)){
					result = pickForGame.result;
				}
				else {
					result = 'L';
				}
				
				if (result == 'W'){
					winnerOrLoserClass = 'winner';
					playerRecords[playerIndex].wins++;
				}
				else if (result == 'L'){
					winnerOrLoserClass = 'loser';
					playerRecords[playerIndex].losses++;
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
		
		var playerRecordHtml = '<td colspan="2" class="' + pickRecordRowCss + '">' + playerRecord.wins + ' - ' + playerRecord.losses + '</td>';
		weekRecordHtml = weekRecordHtml + playerRecordHtml;
	}
	
	weekRecordHtml = '<tr><td class="last-pick-game"></td>' + weekRecordHtml + '</tr>';
	
	var gridBodyHtml = '<tbody>' + weekRecordHtml + pickRowsHtml + '</tbody>';
	
	picksGridHtml = '<table class="picks-table" align="center">' + gridHeaderHtml + gridBodyHtml + '</table>';
	
	return picksGridHtml;
}

function isBlank(value){
	
	if (!isDefined(value)){
		return true;
	}
	
	if (value.trim().length() == 0){
		return true;
	}
	
	return false;
}

function isDefined(value){
	if (value == null || value == undefined){
		return false;
	}
	
	return true;
}