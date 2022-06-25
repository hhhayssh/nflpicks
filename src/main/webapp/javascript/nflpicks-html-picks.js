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
//	var selectedWeek = getSelectedWeek();
	var weekSelected = isASingleWeekSelected();
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
			
			var labelToUse = pick.weekSequenceNumber + '';
			//Use the label if it's not the regular season.
			if (pick.weekType != 'regular_season'){
				labelToUse = pick.weekLabel;
			}
		
			weekCell = '<td class="' + cssClassToUse + '">' + labelToUse + '</td>';
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