/**
 * 
 * This function will get the picks for the current week so that people
 * can "make" their own picks easier.
 * 
 * @returns
 */
function updateMakePicks(){
	
	//Steps to do:
	//	1. Send the request to the server.
	//	2. Keep the current games around in case we want to use them later.
	//	3. Make the html table.
	//	4. Show it as the content.
	
	$.ajax({url: 'nflpicks?target=makePicks',
			contentType: 'application/json; charset=UTF-8'}
	)
	.done(function(data) {
		var gamesForNextWeek = $.parseJSON(data);

		NFL_PICKS_GLOBAL.gamesForCurrentWeek = gamesForNextWeek;
		
		var picksGridHtml = createMakePicksGrid(gamesForNextWeek);
		
		$('#contentContainer').empty();
		$('#contentContainer').append(picksGridHtml);
	})
	.fail(function() {
	})
	.always(function() {
	});
	
}

function onClickCopyPicks(){

	var pickedPicksText = $('#picked-picks').val();

	var successful = copyToClipboard(pickedPicksText);
	
	if (successful){
		$('#picked-picks-copied-container').show();
	}
}

/**
 * 
 * This function will update the picks that they made by adding them to the
 * text area so they can be easily copied and pasted.  It'll go through
 * the current games in the NFL_PICKS_GLOBAL.gamesForCurrentWeek array
 * and get the pick for each one, and then add that to the text area.
 * 
 * @returns
 */
function updatePickedPicks(){
	
	//Steps to do:
	//	1. Go through each game in the week and get the picked they picked for it.
	//	2. If they didn't pick a team, that's a missing pick, so keep track of that.
	//	3. Get which team they picked and show the abbreviation for that team in the
	//	   output.
	//	4. Update the text area with what they picked and what picks are missing.
	
	var pickedPicksUpdate = '';
	var missingPicksString = '';
	var numberOfMissingPicks = 0;
	
	for (var index = 0; index < NFL_PICKS_GLOBAL.gamesForCurrentWeek.length; index++){
		var game = NFL_PICKS_GLOBAL.gamesForCurrentWeek[index];
		
		var selectedPick = getSelectedPick(game.id);
		
		var abbreviation = '';
		if (game.homeTeam.id == selectedPick){
			abbreviation = game.homeTeam.abbreviation;
		}
		else if (game.awayTeam.id == selectedPick){
			abbreviation = game.awayTeam.abbreviation;
		}
		//If we're here, they didn't pick anything for that game, so add it to the list.
		else {
			numberOfMissingPicks++;
			if (missingPicksString != ''){
				missingPicksString = missingPicksString + ', ';
			}
			
			missingPicksString = missingPicksString + game.awayTeam.abbreviation + ' @ ' + game.homeTeam.abbreviation;
		}
		
		if (abbreviation != ''){
			
			if ('' != pickedPicksUpdate){
				pickedPicksUpdate = pickedPicksUpdate + ', ';
			}

			pickedPicksUpdate = pickedPicksUpdate + abbreviation; 
		}
	}
	
	$('#missing-picks-container').empty();
	if (numberOfMissingPicks > 0){
		if (numberOfMissingPicks == 1){
			$('#missing-picks-container').append('There is ' + numberOfMissingPicks + ' missing pick: ' + missingPicksString);
		}
		else {
			$('#missing-picks-container').append('There are ' + numberOfMissingPicks + ' missing picks: ' + missingPicksString);
		}
	}
	
	$('#picked-picks').val(pickedPicksUpdate);
}

/**
 * 
 * Gets the pick they made for the given game when making their picks.
 * 
 * @param gameId
 * @returns
 */
function getSelectedPick(gameId){
	return $('#pick-' + gameId).val();
}