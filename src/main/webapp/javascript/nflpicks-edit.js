
$(document).ready(
	function(){
		getSelectionCriteriaAndInitialize();
});

function getSelectionCriteriaAndInitialize(){
	
	var urlParameters = getUrlParameters();
	
	var key = urlParameters.key;
	
	$.ajax({url: 'nflpicks?target=editSelectionCriteria&key=' + key,
		contentType: 'application/json; charset=UTF-8'}
	)
	.done(function(data) {
		var selectionCriteriaContainer = $.parseJSON(data);
		
		if (selectionCriteriaContainer.error){
			$(document.body).empty();
			$(document.body).append('<div>Interception!  40 ... 30 ... 25 ... 20 ... He could go ... all ... the ... way! </div>');
			return;
		}
		
		var years = selectionCriteriaContainer.years;

		var yearOptions = [];

		for (var index = 0; index < years.length; index++){
			var year = years[index];
			yearOptions.push({label: year, value: year});
		}

		setOptionsInSelect('year', yearOptions);
		
		var weekOptions = [{label: 'Week 1', value: '1'}, {label: 'Week 2', value: '2'},
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
		
		setOptionsInSelect('week', weekOptions);

		var players = selectionCriteriaContainer.players;

		var playerOptions = [];

		for (var index = 0; index < players.length; index++){
			var player = players[index];
			playerOptions.push({label: player, value: player});
		}

		setOptionsInSelect('player', playerOptions);

		var currentWeekKey = selectionCriteriaContainer.currentWeekKey;
		var currentYear = selectionCriteriaContainer.currentYear;
		
		setSelectedWeek(currentWeekKey);
		setSelectedYear(currentYear);
		
		updateView();
	})
	.fail(function() {
	})
	.always(function() {
	});
}

var games = null;

var picksGrid = null;

function updateView(){
	var type = getSelectedType();
	
	if ('games' == type){
		hideSelectPlayer();
		getGames();
	}
	else if ('picks' == type){
		showSelectPlayer();
		getPicks();
	}
}

function saveGames(){

	var selectedYear = getSelectedYear();
	var selectedWeek = getSelectedWeek();
	
	var gamesToSave = {year: selectedYear,
					   week: selectedWeek,
					   games: []};
	
	for (var index = 0; index < games.length; index++){
		var game = games[index];
		
		var selectedWinningTeamId = getSelectedWinningTeamId(game.id);
		
		var gameToSave = {id: game.id,
						  winningTeamId: selectedWinningTeamId};

		gamesToSave.games.push(gameToSave);
	}
	
	var dataToSend = JSON.stringify(gamesToSave);
	
	$.ajax({url: 'nflpicks?target=games',
		    method: 'POST',
		    data: dataToSend,
			contentType: 'application/json; charset=UTF-8'}
	)
	.done(function(data) {
		sendToPicks(selectedYear, selectedWeek);
	})
	.fail(function() {
	})
	.always(function() {
	});
}

function savePicks(){
	
	var selectedYear = getSelectedYear();
	var selectedWeek = getSelectedWeek();
	var selectedPlayer = getSelectedPlayer();
	
	var player = getPlayer(selectedPlayer);
	
	var picksToSave = {year: selectedYear,
					   week: selectedWeek,
					   player: selectedPlayer,
					   picks: []};
	
	for (var index = 0; index < picksGrid.games.length; index++){
		var game = picksGrid.games[index];
		
		var selectedWinner = getSelectedPick(game.id, player.id);
		
		var pickToSave = {gameId: game.id,
						  playerId: player.id,
						  teamId: selectedWinner};
		
		picksToSave.picks.push(pickToSave);
	}
	
	var dataToSend = JSON.stringify(picksToSave);
	
	$.ajax({url: 'nflpicks?target=picks',
		    method: 'POST',
		    data: dataToSend,
			contentType: 'application/json; charset=UTF-8'}
	)
	.done(function(data) {
		sendToPicks(selectedYear, selectedWeek);
	})
	.fail(function() {
	})
	.always(function() {
	});
	
}

function getGames(){
	var year = getSelectedYear();
	var week = getSelectedWeek();
	
	$.ajax({url: 'nflpicks?target=games&year=' + year + '&week=' + week,
		contentType: 'application/json; charset=UTF-8'}
	)
	.done(function(data) {
		games = $.parseJSON(data);

		var editGamesGridHtml = createEditGamesGridHtml(games);
		
		$('#contentContainer').empty();
		$('#contentContainer').append(editGamesGridHtml);
	})
	.fail(function() {
	})
	.always(function() {
	});
}

function getPicks(){
	var year = getSelectedYear();
	var week = getSelectedWeek();
	var player = getSelectedPlayer();
	
	$.ajax({url: 'nflpicks?target=picksGrid&player=' + player + '&year=' + year + '&week=' + week,
		contentType: 'application/json; charset=UTF-8'}
	)
	.done(function(data) {
		picksGrid = $.parseJSON(data);
		
		var editPicksGridHtml = createEditPicksGridHtml(picksGrid);
		$('#contentContainer').empty();
		$('#contentContainer').append(editPicksGridHtml);
	})
	.fail(function() {
	})
	.always(function() {
	});
}

function sendToPicks(year, week){
	window.location = 'index.html?year=' + year + '&week=' + week;
}

function getSelectedType(){
	return $('#type option:selected').val();
}

function setSelectedType(type){
	if (doesSelectHaveOptionWithValue('type', type)){
		$('#type').val(type);
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

function getSelectedPlayer(){
	return $('#player option:selected').val();
}

function setSelectedPlayer(player){
	if (doesSelectHaveOptionWithValue('player', player)){
		$('#player').val(player);
	}
}

function getSelectedWinningTeamId(gameId){
	return $('#game-' + gameId).val();
}

function getSelectedPick(gameId, playerId){
	return $('#pick-' + gameId + '-' + playerId).val();
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

function getPlayer(name){
	
	for (var index = 0; index < picksGrid.players.length; index++){
		var player = picksGrid.players[index];
		
		if (player.name == name){
			return player;
		}
	}
	
	return null;
}

function showSelectPlayer(){
	$('#playerContainer').show();
}

function hideSelectPlayer(){
	$('#playerContainer').hide();
}

function createEditGamesGridHtml(games){
	
	var editGamesHtmlHeader = '<thead>' +
									'<th class="table-header">Game</th>' +
									'<th class="table-header">Winner</th>' +
								'</thead>';
	
	var editGamesRowsHtml = '';
	
	for (var index = 0; index < games.length; index++){
		var game = games[index];
		
		var gameClass = 'edit-game';
		if (index + 1 == games.length){
			gameClass = 'edit-game-last';
		}
		
		var rowClassName = 'even-row';
		if (index % 2 == 1){
			rowClassName = 'odd-row';
		}
		
		var homeTeam = game.homeTeam;
		
		var selectId = 'game-' + game.id;

		var options = [{label: '', value: 0},
		               {label: game.homeTeam.abbreviation, value: game.homeTeam.id}, 
		               {label: game.awayTeam.abbreviation, value: game.awayTeam.id},
		               {label: 'Tie', value: -1}];
		
		var winningTeamId = null;
		if (isDefined(game.winningTeam)){
			winningTeamId = game.winningTeam.id;
		}
		else if (isDefined(game.tie) && game.tie){
			winningTeamId = -1;
		}
		
		var teamSelectHtml = createSelectHtml(selectId, options, winningTeamId, 'team-select', null); 
		
		editGamesRowsHtml = editGamesRowsHtml +
							  '<tr class="' + rowClassName + '">' +
							  	'<td class="' + gameClass + '">' + game.awayTeam.abbreviation + ' @ ' + game.homeTeam.abbreviation + '</td>' +
							  	'<td class="' + gameClass + '">' + teamSelectHtml + '</td>' +
							  '</tr>';
	}
	
	var editGamesGridHtml = '<table class="edit-games-table" align="center">' + editGamesHtmlHeader + '<tbody>' + editGamesRowsHtml + '</tbody></table>' +
							  '<div style="margin-top: 20px; margin-bottom: 40px; text-align: center;">' + 
							  	'<button onClick="saveGames();" style="padding: 10px;">Save</button>' + 
							  '</div>';
	
	return editGamesGridHtml;
}

function createEditPicksGridHtml(picksGrid){
	
	var picksGridHtml = '';
	
	var gridHeaderHtml = '<thead>' +
						 	'<th align="left" class="table-header">Game</th>';
	
	for (var index = 0; index < picksGrid.players.length; index++){
		var player = picksGrid.players[index];
		
		gridHeaderHtml = gridHeaderHtml + '<th class="table-header">' + player.name + '</th>';
	}
	
	gridHeaderHtml = gridHeaderHtml + '</thead>';
	
	var pickRowsHtml = '';
	
	for (var index = 0; index < picksGrid.games.length; index++){
		var game = picksGrid.games[index];
		
		var rowClassName = 'even-row';
		
		if (index % 2 == 1){
			rowClassName = 'odd-row';
		}

		var pickGameClass = 'edit-pick-game';
		var pickTeamClass = 'edit-pick-team';
		
		if (index + 1 >= picksGrid.games.length){
			pickGameClass = 'edit-pick-last-game';
			pickTeamClass = 'edit-pick-last-team';
		}
		
		var homeTeamClass = '';
		var awayTeamClass = '';
		
		if (isDefined(game.winningTeam) && game.winningTeam.id != 0){
			if (game.winningTeam.id == game.awayTeam.id){
				awayTeamClass = 'winner';
			}
			else if (game.winningTeam.id = game.homeTeam.id){
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
			
			var selectedTeamId = null;
			if (isDefined(pickForGame) && isDefined(pickForGame.team)){
				selectedTeamId = pickForGame.team.id;
			}
			
			var options = [{label: '', value: '0'},
			               {label: game.homeTeam.abbreviation, value: game.homeTeam.id},
			               {label: game.awayTeam.abbreviation, value: game.awayTeam.id}];
			
			var selectPickId = 'pick-' + game.id + '-' + playerId;
			var selectPickHtml = createSelectHtml(selectPickId, options, selectedTeamId, 'edit-pick-select', null);
						
			gameRow = gameRow + '<td class="' + pickTeamClass + '">' + 
									selectPickHtml + 
								'</td>';
		}
		
		gameRow = gameRow + '</tr>';
		
		pickRowsHtml = pickRowsHtml + gameRow;
	}

	var gridBodyHtml = '<tbody>' + pickRowsHtml + '</tbody>';
	
	picksGridHtml = '<table class="edit-picks-table" align="center">' + gridHeaderHtml + gridBodyHtml + '</table>' +
						'<div style="margin-top: 20px; margin-bottom: 40px; text-align: center;">' + 
						'<button onClick="savePicks();" style="padding: 10px;">Save</button>' + 
				  	'</div>';
	
	return picksGridHtml;
}
