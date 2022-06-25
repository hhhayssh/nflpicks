/**
 * 
 * This function will create the html for showing the standings for the given records.
 * It expects them to be sorted in the order they should be shown already, so it won't do any
 * sorting of them (it's up to the caller to do that).
 * 
 * @param records
 * @returns
 */
function createStandingsHtml(records){
	
	//I'll just need to a few things here...
	//	1. decide if we want to show by division - this should be done by the calling function
	//	2. if we do, break them up by player division
	
	//Steps to do:
	//	1. Create the header for the standings.
	//	2. Figure out what the most wins and losses are for the records (so
	//	   we can get the games back when making them).
	//	3. Go through each record, get its rank, and add it to the table.
	//	4. Put all the parts of the table together.	
	
	var standingsHtml = '';
	
	//We only want to include the ties header if there's a record in there
	//with a tie.
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
						 	  	'<th class="standings-table-header">GB</th>' + 
						 	  '</thead>';

	//For holding the table rows.
	var rowsHtml = '';
	
	//Here so we can figure out how many games back a record is from the top.
	var topWins = 0;
	var topLosses = 0;
	
	if (!isEmpty(records)){
		topWins = records[0].wins;
		topLosses = records[0].losses;
	}
	//If there aren't any records, then we should just show that there aren't any results.
	else {
		rowsHtml = '<tr><td colspan="5" style="text-align: center;">No results</td></tr>';
	}

	for (var index = 0; index < records.length; index++){
		var record = records[index];

		//Get the rank of this record within all the other records.  The "rank" function
		//takes care of doing that.  We just have to tell it how to compare two records and
		//when records are exactly the same.
		//The "recordWinComparisonFunction" compares records based on their wins and losses,
		//and the inline function says a record is exactly the same when it's for the same player.
		var recordRank = rank(record, records, recordWinComparisonFunction, function (record1, record2){
			
			if (record1.player.id == record2.player.id){
				return true;
			}
			
			return false;
		});
		
		//We want to show the percentage too.
		var percentage = record.wins / (record.wins + record.losses);
		var percentageString = '';
		//And we want it to 3 decimal places.
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
		
		var rankText = recordRank.rank + '';
		if (recordRank.tie){
			rankText = rankText + 't';
		}
		
		//Now we have everything we need for the row.
		rowsHtml = rowsHtml + 
				  '<tr class="standings-table-row">' +
				  	'<td class="standings-table-player-cell">' + rankText + '. ' + record.player.name + '</td>' +
				  	'<td class="standings-table-cell">' + record.wins + '</td>' +
				  	'<td class="standings-table-cell">' + record.losses + '</td>' +
				  	tiesCell + 
				  	'<td class="standings-table-cell">' + percentageString + '</td>' +
				  	'<td class="standings-table-cell">' + gamesBack + '</td>' + 
				  '</tr>';
	}

	//And now we just have to put them together.
	var standingsBodyHtml = '<tbody class="standings-table-body">' + rowsHtml + '</tbody>';
	
	standingsHtml = '<table class="standings-table">' + standingsHeaderHtml + standingsBodyHtml + '</table>';
	
	return standingsHtml;
}


/**
 * 
 * This function will create the html for showing the division standings for the given "division records".
 * It expects them to be sorted in the order they should be shown already, so it won't do any
 * sorting of them (it's up to the caller to do that).
 * 
 * @param divisionRecords
 * @returns
 */
function createDivisionStandingsHtml(divisionRecords){
	
	//I'll just need to a few things here...
	//	1. decide if we want to show by division - this should be done by the calling function
	//	2. if we do, break them up by player division
	
	//Steps to do:
	//	1. Create the header for the standings.
	//	2. Figure out what the most wins and losses are for the records (so
	//	   we can get the games back when making them).
	//	3. Go through each record, get its rank, and add it to the table.
	//	4. Put all the parts of the table together.	

	var divisionStandingsHtml = '';
	
	for (var index = 0; index < divisionRecords.length; index++){
		var divisionRecord = divisionRecords[index];
		
		var divisionRecordHtml = createDivisionRecordHtml(divisionRecord);
		
		divisionStandingsHtml = divisionStandingsHtml + divisionRecordHtml;
	}
	
	return divisionStandingsHtml;
}

function createDivisionRecordHtml(divisionRecord){
	
	var divisionRecordHtml = '<div style="width: 100%;">' + 
								'<div style="width: 100%; text-align: center;">' + 
									'<span style="text-decoration: underline; font-weight: bold;">' + divisionRecord.division.name + '</span>';
	
	var standingsHtml = createStandingsHtmlForRecords(divisionRecord.records);
	
	divisionRecordHtml = divisionRecordHtml + standingsHtml + 
							'</div>' +
					     '</div>';
	
	return divisionRecordHtml;
}

function createStandingsHtmlForRecords(records){
	
	var standingsHtml = '';
	
	//We only want to include the ties header if there's a record in there
	//with a tie.
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
						 	  	'<th class="standings-table-header">GB</th>' + 
						 	  '</thead>';
	
	var areThereAnyTies = hasTies(records);
	
	//For holding the table rows.
	var rowsHtml = '';
	
	//Here so we can figure out how many games back a record is from the top.
	var topWins = 0;
	var topLosses = 0;
	
	if (!isEmpty(records)){
		topWins = records[0].wins;
		topLosses = records[0].losses;
	}
	//If there aren't any records, then we should just show that there aren't any results.
	else {
		rowsHtml = '<tr><td colspan="5" style="text-align: center;">No results</td></tr>';
	}

	if (records == null || records == undefined){
		records = [];
	}
	
	for (var index = 0; index < records.length; index++){
		var record = records[index];

		//Get the rank of this record within all the other records.  The "rank" function
		//takes care of doing that.  We just have to tell it how to compare two records and
		//when records are exactly the same.
		//The "recordWinComparisonFunction" compares records based on their wins and losses,
		//and the inline function says a record is exactly the same when it's for the same player.
		var recordRank = rank(record, records, recordWinComparisonFunction, function (record1, record2){
			
			if (record1.player.id == record2.player.id){
				return true;
			}
			
			return false;
		});
		
		//We want to show the percentage too.
		var percentage = record.wins / (record.wins + record.losses);
		var percentageString = '';
		//And we want it to 3 decimal places.
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
		
		var rankText = recordRank.rank + '';
		if (recordRank.tie){
			rankText = rankText + 't';
		}
		
		//Now we have everything we need for the row.
		rowsHtml = rowsHtml + 
				  '<tr class="standings-table-row">' +
				  	'<td class="standings-table-player-cell">' + rankText + '. ' + record.player.name + '</td>' +
				  	'<td class="standings-table-cell">' + record.wins + '</td>' +
				  	'<td class="standings-table-cell">' + record.losses + '</td>' +
				  	tiesCell + 
				  	'<td class="standings-table-cell">' + percentageString + '</td>' +
				  	'<td class="standings-table-cell">' + gamesBack + '</td>' + 
				  '</tr>';
	}

	//And now we just have to put them together.
	var standingsBodyHtml = '<tbody class="standings-table-body">' + rowsHtml + '</tbody>';
	
	standingsHtml = '<table class="standings-table">' + standingsHeaderHtml + standingsBodyHtml + '</table>';
	
	return standingsHtml;
}