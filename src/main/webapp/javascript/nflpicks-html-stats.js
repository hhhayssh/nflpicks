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
	var weekSelected = isASingleWeekSelected();
	
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
							 	 '<th class="table-header">Picked Away</th>' + 
							 	 '<th class="table-header">Picked Home</th>' +
							 '</thead>';
	
	var rowsHtml = '';
	
	if (isEmpty(pickSplits)){
		rowsHtml = rowsHtml + '<tr><td colspan="5" style="text-align: center;">No results</td></tr>';
	}
	
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
			
			var labelToUse = pickSplit.weekSequenceNumber + '';
			//Use the label if it's not the regular season.
			if (pickSplit.weekType != 'regular_season'){
				labelToUse = pickSplit.weekLabel;
			}
		
			week = '<td class="' + cssClassToUse + '">' + labelToUse + '</td>';
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
				  '<td class="' + pickResultClass + '"><span class="' + awayPlayersClass + '">' + pickSplit.awayTeamAbbreviation + ' (' + numberOfAwayPlayers + ')<br/> ' + awayPlayersString + '</span></td>' +
				  '<td class="' + pickResultClass + '"><span class="' + homePlayersClass + '">' + pickSplit.homeTeamAbbreviation + ' (' + numberOfHomePlayers + ')<br/> ' + homePlayersString + '</span></td>' +
				  '</tr>';
		
		rowsHtml = rowsHtml + gameRow;
	}
		
	var pickSplitsBodyHtml = '<tbody>' + rowsHtml + '</tbody>';
	
	var pickSplitsHtml = '<table class="picks-table" align="center">' + pickSplitsHeaderHtml + pickSplitsBodyHtml + '</table>';;
	
	return pickSplitsHtml;
}

/**
 * 
 * This function will create the "week comparison" html.  It expects the given "week records"
 * to basically be a sorted list of "player week records" (which are the records for a given year, week,
 * and record, sorted in that order).
 * 
 * It will go through and try to make a table like this:
 * 
 * Week comparison
 * 
 * 		Year	Week		Player				Result
 * 		2019	12			Doodle (10 - 4) 	Doodle (+1)
 * 							Jerry (9 - 5)
 * 		2019	13			Jerry (9 - 7)		Jerry (+1)
 * 							Doodle (8 - 8)
 * 
 * This will show who won each week and how much they won it by.  I made it so it's hopefully easy
 * to see what the differences between people are (like to see where somebody blows it, pretty much).
 * 
 * @param weekRecords
 * @returns
 */
function createWeekComparisonHtml(weekRecords){
	
	//Steps to do:
	//	1. Make the header (add in one for ties and a year if they haven't picked one).
	//	2. Go through and output each record and that's pretty much it.

	//Only show the years and weeks if a specific one isn't selected.
	var yearHeader = '';
	var aYearIsSelected = isSpecificYearSelected();
	if (!aYearIsSelected){
		yearHeader = '<th class="common-table-header">Year</th>';
	}
	
	var weekHeader = '';
	var aWeekIsSelected = isASingleWeekSelected();
	if (!aWeekIsSelected){
		weekHeader = '<th class="common-table-header">Week</th>';
	}
	
	var tableHead = '<thead class="standings-table-head">' + 
						'<tr class="standings-table-row">' + 
							yearHeader +
							weekHeader +
							'<th class="common-table-header">Player</th>' +
							'<th class="common-table-header">Winner</th>' + 
						'</tr>' +
					'</thead>';
	
	var tableBody = '<tbody class="standings-table-body">';
	
	if (isEmpty(weekRecords)){
		tableBody = tableBody + '<tr><td colspan="7" style="text-align: center;">No results</td></tr>';
	}

	//All the records should be sorted by year, week, and record.  They're all in one giant list
	//so we'll have to kind of "detect" the change from one year and week to the next.
	//These variables will let us do that.
	//Whenever the record we just got has a different year or week from what we just processed,
	//that means the year or week changed, so we should output a row using the records we've been
	//"collecting" for the (now previous) week.
	var previousWeekRecord = null;
	//These are the records for the current week.  We're "collecting" from the big list into this one
	//and, once we hit a new week, we'll use this array to output the row for the records we collected.
	var currentRecords = [];
	
	for (var index = 0; index < weekRecords.length; index++){
		var weekRecord = weekRecords[index];

		//The first time through, we don't have a previous record yet so we should just set it now.
		if (previousWeekRecord == null){
			previousWeekRecord = weekRecord;
		}
		
		//We should output a row if:
		//	1. The year changed between the previous record and this one.
		//	2. The week changed between the previous record and this one.
		//	3. We're at the end of the list.
		if (previousWeekRecord.season.year != weekRecord.season.year || 
				previousWeekRecord.week.label != weekRecord.week.label ||
				index + 1 == weekRecords.length){
			
			//If this is the last record in the list, add it to the current records.
			if (index + 1 == weekRecords.length){
				currentRecords.push(weekRecord);
			}
			
			//Add the year if we have it.
			var yearCell = '';
			if (!aYearIsSelected){
				yearCell = '<td class="common-table-cell">' + previousWeekRecord.season.year + '</td>';
			}
			
			var weekCell = '';
			if (!aWeekIsSelected){
				//We'll want a link to the picks grid for the week.
				var weekLabelToUse = shortenWeekLabel(previousWeekRecord.week.label);
				var picksLink = createPicksLink(weekLabelToUse, previousWeekRecord.season.year, previousWeekRecord.week.weekKey, null, null);
				weekCell = '<td class="common-table-cell">' + picksLink + '</td>'; 
			}

			//And we have the first part of the row.
			var row = '<tr class="standings-table-row">' +
						yearCell +
						weekCell;
			
			//Now we just have to add a column for the records for the players for the week.
			//And then another that shows the result (who won and by how much).
			var playersCell = '<td class="common-table-cell"><ul style="list-style: none; padding: 0px;">';
			
			//The record with the largest number of wins for the week.  Keeping this so we can calculate the difference
			//between it and the one that was second.
			var topWins = 0;
			var previousWins = 0;
			var winDifference = 0;

			//The names of the players who won the week.  There could be more than one.
			var winningPlayerNames = [];
			
			for (var currentRecordIndex = 0; currentRecordIndex < currentRecords.length; currentRecordIndex++){
				var currentRecord = currentRecords[currentRecordIndex];
				
				//Add the record for the player to the list.
				var recordLabelToUse = '';
				if (isDefined(currentRecord.record.ties) && currentRecord.record.ties != 0){
					recordLabelToUse = '(' + currentRecord.record.wins + ' - ' + currentRecord.record.losses + ' - ' + currentRecord.record.ties + ')';
				}
				else {
					recordLabelToUse = '(' + currentRecord.record.wins + ' - ' + currentRecord.record.losses + ')';
				}
				
				playersCell = playersCell + '<li>' + currentRecord.player.name + ' ' + recordLabelToUse + '</li>';
				
				//If this record has as many or more wins than the current top wins, add them to the list.
				//Since all the records are sorted by the number of wins, we should only go into this the first
				//time we see a record or when a record ties the one we saw the first time.
				if (currentRecord.record.wins >= topWins){
					winningPlayerNames.push(currentRecord.player.name);
					topWins = currentRecord.record.wins;
				}
				//Otherwise, the record didn't win during the week.  If we haven't calculated a difference
				//for the week yet, that means it came in second.  In that case, we'll want to calculate the 
				//difference between it and the "top wins" so we can show how much the top person won by.
				else {
					if (winDifference == 0){
						winDifference = topWins - currentRecord.record.wins;
					}
				}
				
				previousWins = currentRecord.record.wins;
			}
			
			//And now we have the players all in a list so we can add them to the table.
			playersCell = playersCell + '</ul></td>';
			
			//For the result cell, we just want to list the names of the winners and, for the first winner,
			//how much all the winners beat 2nd place by.
			var resultCell = '<td class="common-table-cell"><ul class="standings-table-cell-list">';
			for (var winningNameIndex = 0; winningNameIndex < winningPlayerNames.length; winningNameIndex++){
				var playerName = winningPlayerNames[winningNameIndex];
				
				//Add how much the player won by if we're on the first player.
				if (winningNameIndex == 0){
					if (winDifference == 0){
						playerName = playerName + ' (tie)';
					}
					else {
						playerName = playerName + ' (+' + winDifference + ')';
					}
				}
				
				resultCell = resultCell + '<li>' + playerName + '</li>';
			}
			
			//And we're done with the result table.
			resultCell = resultCell + '</ul></td>';
			
			row = row + playersCell + resultCell + '</tr>';
			
			tableBody = tableBody + row;
			
			currentRecords = [];
		}
		
		currentRecords.push(weekRecord);
		previousWeekRecord = weekRecord;
	}
	
	tableBody = tableBody + '</tbody>';

	var weekRecordsByPlayerHtml = '<table class="standings-table">' + tableHead + tableBody + '</table>';
	
	return weekRecordsByPlayerHtml;
}

/**
 * 
 * This function will create the "season progression" html.  The season progression
 * is basically like totaling up all the records for a season (or multiple seasons) as
 * they go.  So, like, if it's for a season, after the first week it'll show the records
 * for week 1.  After week 2, it'll show the records <i>through</i> week 2 (week 1 + week 2).
 * After week 3, it'll show the records through week 3 (week 1 + week 2 + week 3)... and so on.
 * 
 * The table it makes is just like the "week comparison" one where it'll show the records
 * in one column and then who won and by how much in the other column.  
 * 
 * It's here so we can see how records change as it goes along...
 * 
 * @param weekRecords
 * @returns
 */
function createSeasonProgressionHtml(weekRecords){
	
	//Steps to do:
	//	1. Make the header (add in one for ties and a year if they haven't picked one).
	//	2. Go through the records and, when we've hit a "batch" for a week...
	//	3. Add those weeks to the "accumulated records" we've processed so far.
	//	4. Output the accumulated records up to that point.
	//	5. Keep going through the records, stopping to add them to the accumulated records
	//	   when we hit the end of a week.

	//Only show the years and weeks if a specific one isn't selected.
	var yearHeader = '';
	var aYearIsSelected = isSpecificYearSelected();
	if (!aYearIsSelected){
		yearHeader = '<th class="common-table-header">Year</th>';
	}
	
	var weekHeader = '';
	var aWeekIsSelected = isASingleWeekSelected();
	if (!aWeekIsSelected){
		weekHeader = '<th class="common-table-header">Week</th>';
	}
	
	var tableHead = '<thead class="standings-table-head">' + 
						'<tr class="standings-table-row">' + 
							yearHeader +
							weekHeader +
							'<th class="common-table-header">Player</th>' +
							'<th class="common-table-header">Winner</th>' + 
						'</tr>' +
					'</thead>';
	
	var tableBody = '<tbody class="standings-table-body">';
	
	if (isEmpty(weekRecords)){
		tableBody = tableBody + '<tr><td colspan="7" style="text-align: center;">No results</td></tr>';
	}

	//All the records should be sorted by year, week, and record.  They're all in one giant list
	//so we'll have to kind of "detect" the change from one year and week to the next.
	//These variables will let us do that.
	//Whenever the record we just got has a different year or week from what we just processed,
	//that means the year or week changed, so we should output a row using the records we've been
	//"collecting" for the (now previous) week.
	var previousWeekRecord = null;
	//These are the records for the current week.  We're "collecting" from the big list into this one
	//and, once we hit a new week, we'll use this array to output the row for the records we collected.
	var currentRecords = [];
	
	//These are the records for all the weeks so far.  When we hit the "end" of the records for a week,
	//we'll take the records for that week and add them to the records here (so that this has the total
	//for a player's record up to that point).
	var accumulatedRecords = [];
	
	for (var index = 0; index < weekRecords.length; index++){
		var weekRecord = weekRecords[index];

		//The first time through, we don't have a previous record yet so we should just set it now.
		if (previousWeekRecord == null){
			previousWeekRecord = weekRecord;
		}
		
		//We should output a row if:
		//	1. The year changed between the previous record and this one.
		//	2. The week changed between the previous record and this one.
		//	3. We're at the end of the list.
		if (previousWeekRecord.season.year != weekRecord.season.year || 
				previousWeekRecord.week.label != weekRecord.week.label ||
				index + 1 == weekRecords.length){
			
			//If this is the last record in the list, add it to the current records.
			if (index + 1 == weekRecords.length){
				currentRecords.push(weekRecord);
			}
			
			//Add the year if we have it.
			var yearCell = '';
			if (!aYearIsSelected){
				yearCell = '<td class="common-table-cell">' + previousWeekRecord.season.year + '</td>';
			}
			
			var weekCell = '';
			if (!aWeekIsSelected){
				//We'll want a link to the picks grid for the week.
				var weekLabelToUse = shortenWeekLabel(previousWeekRecord.week.label);
				var picksLink = createPicksLink(weekLabelToUse, previousWeekRecord.season.year, previousWeekRecord.week.weekKey, null, null);
				weekCell = '<td class="common-table-cell">' + picksLink + '</td>'; 
			}

			//And we have the first part of the row.
			var row = '<tr class="standings-table-row">' +
						yearCell +
						weekCell;
			
			//Now we just have to add a column for the records for the players for the week.
			//And then another that shows the result (who won and by how much).
			var playersCell = '<td class="common-table-cell"><ul style="list-style: none; padding: 0px;">';
			
			//The record with the largest number of wins for the week.  Keeping this so we can calculate the difference
			//between it and the one that was second.
			var topWins = 0;
			var previousWins = 0;
			var winDifference = 0;

			//The names of the players who won the week.  There could be more than one.
			var winningPlayerNames = [];
			
			//Since we're through a week, it's time to add what we have for this week to what we've "accumulated" so far.
			for (var currentRecordIndex = 0; currentRecordIndex < currentRecords.length; currentRecordIndex++){
				var currentRecord = currentRecords[currentRecordIndex];
			
				//For each record, get the "accumulated" record for that player.
				var accumulatedRecord = getAccumulatedRecord(currentRecord.player.id, accumulatedRecords);

				//If we find a record, then we just have to add the current record's wins and losses to it.
				if (accumulatedRecord != null){
					accumulatedRecord.record.wins = accumulatedRecord.record.wins + currentRecord.record.wins;
					accumulatedRecord.record.losses = accumulatedRecord.record.losses + currentRecord.record.losses;
					if (isDefined(currentRecord.record.ties) && currentRecord.record.ties != 0){
						accumulatedRecord.record.ties = accumulatedRecord.record.ties + currentRecord.record.ties;
					}
				}
				//Otherwise, if we don't find a record, the this is the first time through, so the record to this point
				//is just the current record's record.  So, just copy everything to it.
				else {
					accumulatedRecord = {};
					accumulatedRecord.player = currentRecord.player;
					accumulatedRecord.record = currentRecord.record;
					//Don't really need the season and week, but I'm copying them anyway.
					accumulatedRecord.season = currentRecord.season;
					accumulatedRecord.week = currentRecord.week;
					accumulatedRecords.push(accumulatedRecord);
				}
			}
			
			//Now that we have all the current records added in, we have to "resort" the accumulated records because
			//the "ranking" might have changed.
			sortAccumulatedRecords(accumulatedRecords);
			
			//Now that they're sorted, we just have to output them in the order they're in, just like we do with the
			//week comparison html.
			for (var accumulatedRecordIndex = 0; accumulatedRecordIndex < accumulatedRecords.length; accumulatedRecordIndex++){
				var accumulatedRecord = accumulatedRecords[accumulatedRecordIndex];
				
				//Add the record for the player to the list.
				var recordLabelToUse = '';
				if (isDefined(accumulatedRecord.record.ties) && accumulatedRecord.record.ties != 0){
					recordLabelToUse = '(' + accumulatedRecord.record.wins + ' - ' + accumulatedRecord.record.losses + ' - ' + accumulatedRecord.record.ties + ')';
				}
				else {
					recordLabelToUse = '(' + accumulatedRecord.record.wins + ' - ' + accumulatedRecord.record.losses + ')';
				}
				
				playersCell = playersCell + '<li>' + accumulatedRecord.player.name + ' ' + recordLabelToUse + '</li>';
				
				//If this record has as many or more wins than the current top wins, add them to the list.
				//Since all the records are sorted by the number of wins, we should only go into this the first
				//time we see a record or when a record ties the one we saw the first time.
				if (accumulatedRecord.record.wins >= topWins){
					winningPlayerNames.push(accumulatedRecord.player.name);
					topWins = accumulatedRecord.record.wins;
				}
				//Otherwise, the record didn't win during the week.  If we haven't calculated a difference
				//for the week yet, that means it came in second or worse.  In that case, we'll want to calculate the 
				//difference between it and the "top wins" so we can show how much the top person won by.
				else {
					if (winDifference == 0){
						winDifference = topWins - accumulatedRecord.record.wins;
					}
				}
				
				previousWins = accumulatedRecord.record.wins;
			}
			
			//And now we have the players all in a list so we can add them to the table.
			playersCell = playersCell + '</ul></td>';
			
			//For the result cell, we just want to list the names of the winners and, for the first winner,
			//how much all the winners beat 2nd place by.
			var resultCell = '<td class="common-table-cell"><ul class="standings-table-cell-list">';
			for (var winningNameIndex = 0; winningNameIndex < winningPlayerNames.length; winningNameIndex++){
				var playerName = winningPlayerNames[winningNameIndex];
				
				//Add how much the player won by if we're on the first player.
				if (winningNameIndex == 0){
					if (winDifference == 0){
						playerName = playerName + ' (tie)';
					}
					else {
						playerName = playerName + ' (+' + winDifference + ')';
					}
				}
				
				resultCell = resultCell + '<li>' + playerName + '</li>';
			}
			
			//And we're done with the result table.
			resultCell = resultCell + '</ul></td>';
			
			row = row + playersCell + resultCell + '</tr>';
			
			tableBody = tableBody + row;
			
			currentRecords = [];
		}
		
		currentRecords.push(weekRecord);
		previousWeekRecord = weekRecord;
	}
	
	tableBody = tableBody + '</tbody>';

	var weekRecordsByPlayerHtml = '<table class="standings-table">' + tableHead + tableBody + '</table>';
	
	return weekRecordsByPlayerHtml;
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
	
	if (weekRecords.length == 0){
		var noResultsHtml = '<table class="standings-table">' + 
								'<thead class="standings-table-head">' + 
									'<tr class="standings-table-row">' + 
										'<th class="standings-table-player-header"></th>' +
										'<th class="standings-table-header">Weeks won</th>' +
									'</tr>' + 
								'</thead>' + 
								'<tbody>' +
									'<tr class="standings-table-row">' +
										'<td colspan="2" style="text-align: center;">No results</td>' +
									'</tr>' +
								'</tbody>';
		
		return noResultsHtml;
	}
	
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
//		var selectedYear = getSelectedYear();
		var showYear = false;
		if (!isSpecificYearSelected()){
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
			var labelToUse = shortenWeekLabel(record.week.label);
			var picksLink = createPicksLink(labelToUse, record.season.year, record.week.weekKey, null, weekRecord.player.name);
			
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
	
	var aPlayerIsSelected = isSpecificPlayerSelected();
	
	var playerHeader = '';
	if (!aPlayerIsSelected){
		playerHeader = '<th class="standings-table-player-header">Player</th>';
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
							playerHeader +
							yearHeader +
							'<th class="' + weekClass + '">Week</th>' + 
							'<th class="standings-table-header">W</th>' + 
							'<th class="standings-table-header">L</th>' +
							tiesHeader +
							'<th class="standings-table-header">%</th>' +
						'</tr>' +
					'</thead>';
	
	var tableBody = '<tbody class="standings-table-body">';
	
	if (isEmpty(weekRecords)){
		tableBody = tableBody + '<tr><td colspan="7" style="text-align: center;">No results</td></tr>';
	}
	
	for (var index = 0; index < weekRecords.length; index++){
		var weekRecord = weekRecords[index];
		
		var playerCell = '';
		if (!aPlayerIsSelected){
			playerCell = '<td class="standings-table-player-cell">' + weekRecord.player.name + '</td>';
		}
		
		var yearCell = '';
		if (!aYearIsSelected){
			yearCell = '<td class="standings-table-player-cell">' + weekRecord.season.year + '</td>';
		}
		
		var weekLabel = shortenWeekLabel(weekRecord.week.label);
		
		var playerPicksLink = createPicksLink(weekLabel, weekRecord.season.year, weekRecord.week.weekKey, null, weekRecord.player.name);

		var tiesCell = '';
		//Add a cell for ties if there are any.
		if (areThereAnyTies){
			tiesCell = '<td class="standings-table-cell">' + weekRecord.record.ties + '</td>';
		}
		
		//And we want to show the winning percentage too.
		var percentageString = getWinningPercentage(weekRecord.record.wins, weekRecord.record.losses);
		
		var row = '<tr class="standings-table-row">' +
					playerCell +
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
	
	var isWeekSelected = isASingleWeekSelected();
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
	
	//Going to keep this "old school" since doing it "new school" would mean O(n^2) for 
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
			var picksLink = createPicksLink(labelToUse, playerWeekRecord.season.year, playerWeekRecord.week.weekKey, null, playerWeekRecord.player.name);
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
	
	if (championships.length == 0){
		var noResultsTable = '<table class="standings-table">' + 
								championshipsHeaderHtml + 
								'<tbody>' + 
							 		'<tr class="standings-table-row">' + 
							 			'<td colspan="6" style="text-align: center;">No results</td>' + 
							 		'</tr>' + 
							 	'</tbody>' + 
							 '</table>';
		
		return noResultsTable;
	}

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
	
	var championshipsHtml = '<table class="standings-table">' + championshipsHeaderHtml + championshipsBodyHtml + '</table>' + 
						    '<div class="champion-promos-link-container"><a href="champions.html">Don\'t you forget about me...</a></div>';
	
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
	
	var yearHeader = '<th class="standings-table-header">Year</th>';
	var yearSelected = isSpecificYearSelected();
	if (yearSelected){
		yearHeader = '';
	}
	
	var championshipsStandingsHeaderHtml = '<thead class="standings-table-head">' +
										  	'<tr class="standings-table-row">' +
										  		'<th class="standings-table-player-header"></th>' +
										  		'<th class="standings-table-header">Championships</th>' +
										  		'<th class="standings-table-header">Years</th>' +
										  	'</tr>' +
										  '</thead>';
										  		
	
	var championshipsStandingsBodyHtml = '<tbody class="standings-table-body">';
	
	if (playerChampionshipsList.length == 0){
		var noResultsTable = '<table class="standings-table">' + 
							 	championshipsStandingsHeaderHtml +
								'<tbody class="standings-table-body">' +
							 		'<tr class="standings-table-row">' + 
							 			'<td colspan="3" style="text-align: center;">No results</td>' + 
									'</tr>' + 
								'</tbody>' + 
							'</table>';
		
		return noResultsTable;
	}
	
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
		
		if (playerChampionships1.player.name < playerChampionships2.player.name){
			return -1;
		}
		else if (playerChampionships1.player.name > playerChampionships2.player.name){
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
 * This one will create the html for showing all the division titles and when they won them.
 * The given array should be sorted in the order that we want it shown in.
 * 
 * @param championships
 * @returns
 */
function createDivisionTitlesHtml(divisionTitles){
	
	//Steps to do:
	//	1. Make the header and add ties if there are any.
	//	2. Go through and add a row for each championship.
	//	3. That's it.
	
	var areThereAnyTies = false;
	for (var index = 0; index < divisionTitles.length; index++){
		
		var divisionTitle = divisionTitles[index];
		
		if (divisionTitle.record.ties > 0){
			areThereAnyTies = true;
			break;
		}
	}
	
	var tiesHeader = '';
	if (areThereAnyTies){
		tiesHeader = '<th class="standings-table-header">T</th>';
	}
	
	var divisionTitlesHeaderHtml = '<thead class="standings-table-head">' +
								  	'<th class="standings-table-player-header"></th>' +
								  	'<th class="standings-table-header">Division</th>' +
								  	'<th class="standings-table-header">Year</th>' + 
								  	'<th class="standings-table-header">W</th>' +
								  	'<th class="standings-table-header">L</th>' +
								  	tiesHeader + 
								  	'<th class="standings-table-header">%</th>'
								  '</thead>';
	
	var divisionTitlesBodyHtml = '<tbody>';
	
	if (divisionTitles.length == 0){
		var noResultsTable = '<table class="standings-table">' + 
								divisionTitlesHeaderHtml + 
								'<tbody>' + 
							 		'<tr class="standings-table-row">' + 
							 			'<td colspan="7" style="text-align: center;">No results</td>' + 
							 		'</tr>' + 
							 	'</tbody>' + 
							 '</table>';
		
		return noResultsTable;
	}

	for (var index = 0; index < divisionTitles.length; index++){
		var divisionTitle = divisionTitles[index];

		var tiesCell = '';
		if (areThereAnyTies){
			tiesCell = '<td class="standings-table-cell">' + divisionTitle.record.ties + '</td>';
		}

		var percentageString = getWinningPercentage(divisionTitle.record.wins, divisionTitle.record.losses);

		var divisionTitleRowHtml = '<tr class="standings-table-row">' + 
		'<td class="standings-table-player-cell">' + divisionTitle.player.name + '</td>' +
		'<td class="standings-table-cell">' + divisionTitle.division.abbreviation + '</td>' +
		'<td class="standings-table-cell">' + divisionTitle.season.year + '</td>' +
		'<td class="standings-table-cell">' + divisionTitle.record.wins + '</td>' +
		'<td class="standings-table-cell">' + divisionTitle.record.losses + '</td>' + 
		tiesCell +
		'<td class="standings-table-cell">' + percentageString + '</td>' +
		'</tr>';

		divisionTitlesBodyHtml = divisionTitlesBodyHtml + divisionTitleRowHtml;
	}
	
	divisionTitlesBodyHtml = divisionTitlesBodyHtml + '</tbody>';
	
	var divisionTitlesHtml = '<table class="standings-table">' + divisionTitlesHeaderHtml + divisionTitlesBodyHtml + '</table>';
	
	return divisionTitlesHtml;
}

/**
 * 
 * This one will create the html for showing who has the most division titles.  It doesn't
 * expect the given array to be sorted.  Each entry in the array will be the player with
 * all the division titles they have (not just the count).  We want the whole thing
 * because we want to show the years and records.
 * 
 * @param playerDivisionTitlesList
 * @returns
 */
function createDivisionTitleStandingsHtml(playerDivisionTitlesList){
	
	//Steps to do:
	//	1. Make the header.
	//	2. Make sure they're in the right order by who has the most titles.
	//	3. Make a row in the standings for each person and their titles.
	//	4. That's it.
	
	var yearHeader = '<th class="standings-table-header">Year</th>';
	var yearSelected = isSpecificYearSelected();
	if (yearSelected){
		yearHeader = '';
	}
	
	var divisionTitlesStandingsHeaderHtml = '<thead class="standings-table-head">' +
										  	'<tr class="standings-table-row">' +
										  		'<th class="standings-table-player-header"></th>' +
										  		'<th class="standings-table-header">Titles</th>' +
										  		'<th class="standings-table-header">Years</th>' +
										  	'</tr>' +
										  '</thead>';
										  		
	
	var divisionTitlesStandingsBodyHtml = '<tbody class="standings-table-body">';
	
	if (playerDivisionTitlesList.length == 0){
		var noResultsTable = '<table class="standings-table">' + 
							 	divisionTitlesStandingsHeaderHtml +
								'<tbody class="standings-table-body">' +
							 		'<tr class="standings-table-row">' + 
							 			'<td colspan="3" style="text-align: center;">No results</td>' + 
									'</tr>' + 
								'</tbody>' + 
							'</table>';
		
		return noResultsTable;
	}
	
	//Each item in the list is the player and all another list that has their division titles in it.
	//We want the list sorted so that the person with the most division titles comes first.
	playerDivisionTitlesList.sort(function (playerDivisionTitles1, playerDivisionTitles2){
		
		//More division titles should come first.
		if (playerDivisionTitles1.divisionTitles.length > playerDivisionTitles2.divisionTitles.length){
			return -1;
		}
		//Fewer should come last.
		else if (playerDivisionTitles1.divisionTitles.length < playerDivisionTitles2.divisionTitles.length){
			return 1;
		}
		
		if (playerDivisionTitles1.player.name < playerDivisionTitles2.player.name){
			return -1;
		}
		else if (playerDivisionTitles1.player.name > playerDivisionTitles2.player.name){
			return 1;
		}
		
		return 0;
	});
	
	//Now that they're sorted, we just have to make a row for each one.
	for (var index = 0; index < playerDivisionTitlesList.length; index++){
		var playerDivisionTitles = playerDivisionTitlesList[index];
		
		//And we want each one's rank before we make the row.
		//The rank is based on how many division titles a person has, so that's why the first function compares
		//on that.
		var divisionTitlesRank = rank(playerDivisionTitles, playerDivisionTitlesList, function(playerDivisionTitles1, playerDivisionTitles2){
			
			if (playerDivisionTitles1.divisionTitles.length > playerDivisionTitles2.divisionTitles.length){
				return -1;
			}
			else if (playerDivisionTitles1.divisionTitles.length < playerDivisionTitles2.divisionTitles.length){
				return 1;
			}
			
			return 0;
		}, 
		
		function (playerDivisionTitles1, playerDivisionTitles2){
			
			if (playerDivisionTitles1.player.name == playerDivisionTitles2.player.name){
				return true;
			}
			
			return false;
		});

		var rankText = divisionTitlesRank.rank;
		if (divisionTitlesRank.tie){
			rankText = rankText + 't';
		}
		
		var playerDivisionTitlesRowHtml = '<tr class="standings-table-row">' + 
											'<td class="standings-table-player-cell">' + rankText + '. ' + playerDivisionTitles.player.name + '</td>' +
											'<td class="standings-table-cell">' + playerDivisionTitles.divisionTitles.length + '</td>';
		
		//We want to show all the years they won too, so add them as a list.
		var divisionTitleYearsHtml = '<ul class="standings-table-cell-list">';

		for (var divisionTitleIndex = 0; divisionTitleIndex < playerDivisionTitles.divisionTitles.length; divisionTitleIndex++){
			var divisionTitle = playerDivisionTitles.divisionTitles[divisionTitleIndex];
			
			var tiesString = '';
			if (divisionTitle.record.ties > 0){
				tiesString = ' - ' + divisionTitle.record.ties;
			}
			
			var divisionTitleHtml = '<li>' + divisionTitle.division.abbreviation + ', ' + divisionTitle.season.year + ' (' + divisionTitle.record.wins + ' - ' + divisionTitle.record.losses + tiesString + ')';
			
			divisionTitleYearsHtml = divisionTitleYearsHtml + divisionTitleHtml;
		}
		
		divisionTitleYearsHtml = divisionTitleYearsHtml + '</ul>';
		
		playerDivisionTitlesRowHtml = playerDivisionTitlesRowHtml + '<td class="standings-table-cell">' + divisionTitleYearsHtml + '</td></tr>';
		
		divisionTitlesStandingsBodyHtml = divisionTitlesStandingsBodyHtml + playerDivisionTitlesRowHtml;
	}
	
	divisionTitlesStandingsBodyHtml = divisionTitlesStandingsBodyHtml + '</tbody>';
	
	var divisionTitlesStandingsHtml = '<table class="standings-table">' + divisionTitlesStandingsHeaderHtml + divisionTitlesStandingsBodyHtml + '</table';
	
	return divisionTitlesStandingsHtml;
}

/**
 * 
 * This one will create the html for and ranking each seaons's records.  It doesn't
 * expect the given array to be sorted.  Each entry in the array will be seasons each
 * person has had and their record in those seasons.
 * 
 * @param playerChampionshipsList
 * @returns
 */
function createSeasonStandingsHtml(seasonRecords){
	
	//Steps to do:
	//	1. Make the header.
	//	2. Make sure they're in the right order by who has the most titles.
	//	3. Make a row in the standings for each person and their titles.
	//	4. That's it.
	
	var standingsHtml = '';
	
	var isYearSelected = isSpecificYearSelected();
	var yearHeader = '';
	if (!isYearSelected){
		yearHeader = '<th class="standings-table-header">Year</th>';
	}
	
	var areThereAnyTies = false;
	for (var index = 0; index < seasonRecords.length; index++){
		var seasonRecord = seasonRecords[index];
		
		if (seasonRecord.record.ties > 0){
			areThereAnyTies = true;
			break;
		}
	}
	
	var tiesHeader = '';
	if (areThereAnyTies){
		tiesHeader = '<th class="standings-table-header-small">T</th>';
	}
	
	var standingsHeaderHtml = '<thead class="standings-table-head">' +
						 			'<th class="standings-table-player-header"></th>' +
						 			yearHeader + 
						 			'<th class="standings-table-header-small">W</th>' + 
						 			'<th class="standings-table-header-small">L</th>' +
						 			tiesHeader + 
						 			'<th class="standings-table-header-small">%</th>' + 
						 			'<th class="standings-table-header"></th>' + 
						 	   '</thead>';
	
	if (seasonRecords.length == 0){
		var noResultsTable = '<table class="standings-table">' + 
								standingsHeaderHtml +
								'<tbody class="standings-table-body">' +
							 		'<tr class="standings-table-row">' + 
							 			'<td colspan="8" style="text-align: center;">No results</td>' + 
									'</tr>' + 
								'</tbody>' + 
							'</table>';
		
		return noResultsTable;
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
	
	var rowsHtml = '';
	
	for (var index = 0; index < seasonRecords.length; index++){
		var seasonRecord = seasonRecords[index];
		
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
		if (index + 1 < seasonRecords.length){
			nextRecord = seasonRecords[index + 1];
			
			if (seasonRecord.record.wins == nextRecord.record.wins && seasonRecord.record.losses == nextRecord.record.losses){
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
		if ((nextRank == rank && index + 1 < seasonRecords.length) || (rank == previousRank)){
			rankText = rankText + 't';
		}
		
		//Add in the year and week if they weren't selected.
		var yearCell = '';
		if (!isYearSelected){
			yearCell = '<td class="standings-table-cell">' + seasonRecord.season.year + '</td>';
		}
		
		var tiesCell = '';
		if (areThereAnyTies){
			tiesCell = '<td class="standings-table-cell-small">' + seasonRecord.record.ties + '</td>';
		}
		
		var percentageString = getWinningPercentage(seasonRecord.record.wins, seasonRecord.record.losses);
		
		var championship = '<td></td>';
		if (seasonRecord.championship){
			championship = '<td><img src="files/wwf-belt-icon.png" width="32" height="15" title="Won Championship"/></td>';
		}
		
		rowsHtml = rowsHtml + 
					   '<tr class="standings-table-row">' +
						'<td class="standings-table-player-cell">' + rankText + '. ' + seasonRecord.player.name + '</td>' +
						yearCell +
						'<td class="standings-table-cell-small">' + seasonRecord.record.wins + '</td>' +
						'<td class="standings-table-cell-small">' + seasonRecord.record.losses + '</td>' +
						tiesCell + 
						'<td class="standings-table-cell-small">' + percentageString + '</td>' + 
						'<td class="standings-table-cell-small">' + championship + '</td>';
		
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
	
	if (isEmpty(weeksWonByWeek)){
		weeksWonByWeekBodyHtml = weeksWonByWeekBodyHtml + '<tr><td colspan="4" style="text-align: center;">No results</td></tr>';
	}
	
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
			
			var playerPicksLink = createPicksLink(player.name, weekRecord.season.year, weekRecord.week.weekKey, null, player.name);
			
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
	
	var singlePlayerSelected = isSpecificPlayerSelected();
	var playerHeader = '';
	if (!singlePlayerSelected){
		playerHeader = '<th class="standings-table-header">Player</th>';
	}
	
	var specificTeamSelected = isSpecificTeamSelected();
	var teamHeader = '';
	if (!specificTeamSelected){
		teamHeader = '<th class="standings-table-header">Team</th>';
	}
	
	var pickAccuracySummariesHeadHtml = '<thead class="standings-table-head">' +
											'<tr class="standings-table-row">' +
												playerHeader + 
												teamHeader +
												'<th class="standings-table-header">Right</th>' +
												'<th class="standings-table-header">Wrong</th>' + 
												'<th class="standings-table-header">%</th>' +
												'<th class="standings-table-header"></th>' +
											'</tr>' +
										'</thead>';
	
	var pickAccuracySummariesBodyHtml = '<tbody class="standings-table-body">';
	
	sortPickAccuracySummariesByTimesRight(pickAccuracySummaries);
	
	if (isEmpty(pickAccuracySummaries)){
		pickAccuracySummariesBodyHtml = pickAccuracySummariesBodyHtml + '<tr><td colspan="6" style="text-align: center;">No results</td></tr>';
	}
	
	for (var index = 0; index < pickAccuracySummaries.length; index++){
		var pickAccuracySummary = pickAccuracySummaries[index];
		
		//Add in the team if they didn't pick a specific one.
		var teamCell = '';
		if (!specificTeamSelected){
			teamCell = '<td class="standings-table-cell">' + pickAccuracySummary.team.abbreviation + '</td>';
		}
		
		var playerCell = '';
		if (!singlePlayerSelected){
			playerCell = '<td class="standings-table-cell">' + pickAccuracySummary.player.name + '</td>';
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

		var years = getSelectedYearValues();
		//Add in a link so they can see the exact picks they made with the team.
		var recordPicksLink = createPicksLink('Picks', years, null, pickAccuracySummary.team.abbreviation, pickAccuracySummary.player.name);
		
		//And add the detail that says how many times they picked them to win and lose, and how many times they were right
		//with each.
		var detailHtml = '<tr id="' + detailId + '" style="display: none;">' +
						    '<td class="standings-table-cell" colspan="5">' + 
							    '<table style="width: 100%; border-collapse: collapse;">' +
							 		'<tr><td>Actual record</td><td style="text-align: right;">' + pickAccuracySummary.actualWins + ' - ' + pickAccuracySummary.actualLosses + tiesRecord + '</td></tr>' +
							 		'<tr><td style="border-bottom: thin solid black;">Predicted record</td><td style="text-align: right; border-bottom:thin solid black;">' + pickAccuracySummary.predictedWins + ' - ' + pickAccuracySummary.predictedLosses + '</td></tr>' +
							 		'<tr><td>When picking to win</td><td style="text-align: right;">' + pickAccuracySummary.timesPickedToWinRight + ' - ' + pickAccuracySummary.timesPickedToWinWrong + '</td></tr>' +
							 		'<tr><td style="border-bottom: thin solid black;">When picking to lose</td><td style="text-align: right; border-bottom: thin solid black;"">' + pickAccuracySummary.timesPickedToLoseRight + ' - ' + pickAccuracySummary.timesPickedToLoseWrong + '</td></tr>' +
							 		'<tr><td>Picks record</td><td style="text-align: right;">' + pickAccuracySummary.timesRight + ' - ' + pickAccuracySummary.timesWrong + '</td></tr>' +
							 		'<tr><td>&nbsp;</td><td style="text-align: right;">' + recordPicksLink + '</tr></td>' +
							 	'</table>' + 
						 	'</td>' + 
						 '</tr>';
		
		var pickAccuracySummaryRowHtml = '<tr>' +
											playerCell +
											teamCell +
											'<td class="standings-table-cell">' + pickAccuracySummary.timesRight + '</td>' +
											'<td class="standings-table-cell">' + pickAccuracySummary.timesWrong + '</td>' +
											'<td class="standings-table-cell">' + percentage + '</td>' +
											'<td class="standings-table-cell">' + 
												'<a id="pick-accuracy-details-link-' + index + '" href="javascript:" onClick="toggleShowPickAccuracyDetails(' + index + ')" style="margin-left: 20px;">Details</a>' + 
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
		$('#pick-accuracy-details-link-' + index).text('Details');
	}
	else {
		$('#pick-accuracy-details-' + index).show();
		$('#pick-accuracy-details-link-' + index).text('Hide');
	}
}

/**
 * 
 * This function will create the html for showing the "collective record summary", which
 * is a summary of everybody's records combined.
 * 
 * @param records
 * @returns
 */
function createCollectiveRecordSummaryHtml(collectiveRecordSummary){
	
	//Steps to do:
	//	1. Create the header for the standings.
	//	2. Figure out whether ties should be in it or not.
	//	3. Put it all in a table.
	
	var collectiveRecordSummaryHtml = '';
	
	var collectiveRecords = collectiveRecordSummary.collectiveRecords;
	
	//We only want to include the ties header if there's a record in there
	//with a tie.
	var areThereAnyTies = hasTies(collectiveRecords);
	var tiesHeader = '';
	if (areThereAnyTies){
		tiesHeader = '<th class="standings-table-header">T</th>';
	}
	
	var yearSelected = isSpecificYearSelected();
	var yearsHeader = '<th class="standings-table-header"></th>';
	if (!yearSelected){
		yearsHeader = '<th class="standings-table-header">Year</th>';
	}
	
	var collectiveRecordSummaryHeaderHtml = '<thead class="standings-table-head">' +
												yearsHeader +
												'<th class="standings-table-header">W</th>' + 
												'<th class="standings-table-header">L</th>' +
												tiesHeader + 
												'<th class="standings-table-header">%</th>' + 
											'</thead>';

	//For holding the table rows.
	var rowsHtml = '';
	
	if (isEmpty(collectiveRecords)){
		rowsHtml = '<tr><td colspan="5" style="text-align: center;">No results</td></tr>';
	}
	
	for (var index = 0; index < collectiveRecords.length; index++){
		var collectiveRecord = collectiveRecords[index];
		
		var percentage = collectiveRecord.wins / (collectiveRecord.wins + collectiveRecord.losses);
		var percentageString = '';
		//And we want it to 3 decimal places.
		if (!isNaN(percentage)){
			percentageString = percentage.toPrecision(3);
		}
		
		var yearsCell = '<td class="standings-table-cell"></td>';
		if (!yearSelected){
			yearsCell = '<td class="standings-table-cell">' + collectiveRecord.season.year + '</td>';
		}

		var tiesCell = '';
		if (areThereAnyTies){
			tiesCell = '<td class="standings-table-cell">' + collectiveRecord.ties + '</td>';
		}

		//Now we have everything we need for the row.
		rowsHtml = rowsHtml + 
				   '<tr class="standings-table-row">' +
				    yearsCell +
				   	'<td class="standings-table-cell">' + collectiveRecord.wins + '</td>' +
				   	'<td class="standings-table-cell">' + collectiveRecord.losses + '</td>' +
				   	tiesCell + 
				   	'<td class="standings-table-cell">' + percentageString + '</td>' +
				   '</tr>';
	}
	
	var summaryPercentage = collectiveRecordSummary.wins / (collectiveRecordSummary.wins + collectiveRecordSummary.losses);
	var summaryPercentageString = '';
	//And we want it to 3 decimal places.
	if (!isNaN(summaryPercentage)){
		summaryPercentageString = summaryPercentage.toPrecision(3);
	}
	
	var summaryTiesCell = '';
	if (areThereAnyTies){
		summaryTiesCell = '<td style="padding-top: 5px; border-top: thin solid black;" class="standings-table-cell">' + collectiveRecordSummary.ties + '</td>';
	}
	
	var summaryTableHtml = '<tr>' + 
								'<td style="padding-top: 5px; border-top: thin solid black;" class="standings-table-cell">Total</td>' +
								'<td style="padding-top: 5px; border-top: thin solid black;" class="standings-table-cell">' + collectiveRecordSummary.wins + '</td>' +
								'<td style="padding-top: 5px; border-top: thin solid black;" class="standings-table-cell">' + collectiveRecordSummary.losses + '</td>' +
								summaryTiesCell +
								'<td style="padding-top: 5px; border-top: thin solid black;" class="standings-table-cell">' + summaryPercentageString + '</td>' +
							'</tr>';
	
	//And now we just have to put them together.
	var collectiveRecordSummaryBodyHtml = '<tbody class="standings-table-body">' + rowsHtml + summaryTableHtml +'</tbody>';
	
	collectiveRecordSummaryHtml = '<table style="border-collapse: collapse;" class="standings-table">' + collectiveRecordSummaryHeaderHtml + collectiveRecordSummaryBodyHtml + '</table>';
	
	return collectiveRecordSummaryHtml;
}


/**
 * 
 * This function will create the html table that shows how accurate people's picks are, collectively.
 * Here so people can see if they suck with certain teams.  It doesn't expect the given
 * summaries to be sorted, so it'll do the sorting.
 * 
 * Just like the other one, except without the players column.
 * 
 * @param pickAccuracySummaries
 * @returns
 */
function createCollectivePickAccuracySummaryHtml(pickAccuracySummaries){

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
	
	sortCollectivePickAccuracySummariesByTimesRight(pickAccuracySummaries);
	
	if (isEmpty(pickAccuracySummaries)){
		pickAccuracySummariesBodyHtml = pickAccuracySummariesBodyHtml + '<tr><td colspan="5" style="text-align: center;">No results</td></tr>';
	}
	
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

		var years = getSelectedYearValues();
		var players = getSelectedPlayerValues();
		
		//Add in a link so they can see the exact picks they made with the team.
		var recordPicksLink = createPicksLink('Picks', years, null, pickAccuracySummary.team.abbreviation, players);
		
		//And add the detail that says how many times they picked them to win and lose, and how many times they were right
		//with each.
		var detailHtml = '<tr id="' + detailId + '" style="display: none;">' +
						    '<td class="standings-table-cell" colspan="5">' + 
							    '<table style="width: 100%; border-collapse: collapse;">' +
							 		'<tr><td>Actual record</td><td style="text-align: right;">' + pickAccuracySummary.actualWins + ' - ' + pickAccuracySummary.actualLosses + tiesRecord + '</td></tr>' +
							 		'<tr><td style="border-bottom: thin solid black;">Predicted record</td><td style="text-align: right; border-bottom:thin solid black;">' + pickAccuracySummary.predictedWins + ' - ' + pickAccuracySummary.predictedLosses + '</td></tr>' +
							 		'<tr><td>When picking to win</td><td style="text-align: right;">' + pickAccuracySummary.timesPickedToWinRight + ' - ' + pickAccuracySummary.timesPickedToWinWrong + '</td></tr>' +
							 		'<tr><td style="border-bottom: thin solid black;">When picking to lose</td><td style="text-align: right; border-bottom: thin solid black;"">' + pickAccuracySummary.timesPickedToLoseRight + ' - ' + pickAccuracySummary.timesPickedToLoseWrong + '</td></tr>' +
							 		'<tr><td>Picks record</td><td style="text-align: right;">' + pickAccuracySummary.timesRight + ' - ' + pickAccuracySummary.timesWrong + '</td></tr>' +
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
												'<a id="pick-accuracy-details-link-' + index + '" href="javascript:" onClick="toggleShowPickAccuracyDetails(' + index + ')" style="margin-left: 20px;">Details</a>' + 
											'</td>' +
										 '</tr>' + 
										 detailHtml;
		
		pickAccuracySummariesBodyHtml = pickAccuracySummariesBodyHtml + pickAccuracySummaryRowHtml;
	}
	
	pickAccuracySummariesBodyHtml = pickAccuracySummariesBodyHtml + '</tbody>';
	
	var pickAccuracySummariesHtml = '<table class="standings-table">' + pickAccuracySummariesHeadHtml + pickAccuracySummariesBodyHtml + '</table>';
	
	return pickAccuracySummariesHtml;
}