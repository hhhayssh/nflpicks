/**
 * 
 * This function will make a grid that makes it so they can make picks for all
 * the given games.  The actual picks will just go in a text area, but that's better
 * than nothing!
 * 
 * When they make a pick, it'll call the "updatePickedPicks" function and that'll update
 * a text area with the picks down below.
 * 
 * @param games
 * @returns
 */
function createMakePicksGrid(games){
	
	//Steps to do:
	//	1. Make the header.
	//	2. Add a row for each game.
	//	3. That's it.
	
	var picksGridHtml = '';
	
	var gridHeaderHtml = '<thead>' +
						 	'<th align="left" class="table-header">Game</th>' + 
						 	'<th class="table-header">Pick</th>' + 
						 '</thead>';
	
	var pickRowsHtml = '';
	
	for (var index = 0; index < games.length; index++){
		var game = games[index];
		
		var rowClassName = 'even-row';
		
		if (index % 2 == 1){
			rowClassName = 'odd-row';
		}

		//Here so the borders are right.
		var pickGameClass = 'edit-pick-game';
		var pickTeamClass = 'edit-pick-team';
		
		if (index + 1 >= games.length){
			pickGameClass = 'edit-pick-last-game';
			pickTeamClass = 'edit-pick-last-team';
		}
		
		var gameRow = '<tr class="' + rowClassName + '">' + 
						'<td class="' + pickGameClass + '">' + 
							'<span>' + game.awayTeam.abbreviation + '</span>' + 
							' @ ' + 
							'<span>' + game.homeTeam.abbreviation + '</span>' +  
						'</td>';
	
		var gameId = game.id;
		var options = [{label: '', value: '0'},
		               {label: game.homeTeam.abbreviation, value: game.homeTeam.id},
		               {label: game.awayTeam.abbreviation, value: game.awayTeam.id}];
		var selectPickId = 'pick-' + game.id;

		//When they change a pick, call the "updatePickedPicks" function to update how we show what they picked.
		var selectPickHtml = createSelectHtml(selectPickId, options, null, 'edit-pick-select', null, 'updatePickedPicks()');
					
		gameRow = gameRow + '<td class="' + pickTeamClass + '">' + 
								selectPickHtml + 
							'</td>' +
				  '</tr>';
		
		pickRowsHtml = pickRowsHtml + gameRow;
	}

	var gridBodyHtml = '<tbody>' + pickRowsHtml + '</tbody>';
	
	picksGridHtml = '<table class="edit-picks-table" align="center">' + gridHeaderHtml + gridBodyHtml + '</table>' +
						'<div id="missing-picks-container" style="text-align:center; padding-top: 15px;"></div>' + 
						'<div id="picked-picks-container" style="margin-top: 20px; text-align: center;">' +
							'<textarea id="picked-picks" style="width: 300px; height: 100px;">&nbsp;</textarea>' + 
						'</div>' +
						'<div id="picked-picks-copy-container" style="margin-top: 15px; text-align: center; margin-bottom: 40px;" >' +
							'<button id="picked-picks-copy-button" onClick="onClickCopyPicks();">Copy</button>' +
							'<div id="picked-picks-copied-container" style="margin-top: 10px; display: none;">' + 
								'<span>Picks copied to clipboard!</span>' + 
							'</div>' +
						'</div>';
	
	picksGridHtml = '<div style="text-align: center;"><p><a href="javascript:" onClick="updateView();">Back to the regular stuff</a></p><p>The teams you pick will go in a box at the bottom.  Copy and paste it into a text to pick the games.</p><p style="font-weight:bold;">Just picking them without sending them to me doesn\'t do jack squat.</p><p>Happy now, Jerry and Benny boy?</p></div>' + picksGridHtml;
	
	return picksGridHtml;
}
