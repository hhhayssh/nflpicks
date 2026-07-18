
/**
 * 
 * This function will get the accumulated record for the player with the given id
 * from the given list of accumulated records.  If there isn't a record with that player,
 * it'll return null.
 * 
 * @param playerId
 * @param accumulatedRecords
 * @returns
 */
function getAccumulatedRecord(playerId, accumulatedRecords){
	
	for (var index = 0; index < accumulatedRecords.length; index++){
		var accumulatedRecord = accumulatedRecords[index];
		
		if (playerId == accumulatedRecord.player.id){
			return accumulatedRecord;
		}
	}
	
	return null;
}

/**
 * 
 * This function will sort the accumulated records in the usual order: 
 * by the largest number of wins.  If two records have the same number of wins, then
 * it's by the fewest number of losses.  If two records have the same number of wins
 * and losses, then it's alphabetical by name.
 * 
 * @param accumulatedRecords
 * @returns
 */
function sortAccumulatedRecords(accumulatedRecords){
	
	accumulatedRecords.sort(function(accumulatedRecord1, accumulatedRecord2){
		
		//Most wins goes first.
		if (accumulatedRecord1.record.wins > accumulatedRecord2.record.wins){
			return -1;
		}
		else if (accumulatedRecord1.record.wins < accumulatedRecord2.record.wins){
			return 1;
		}
		//If they have the same number of wins...
		else {
			//Fewest losses goes first.
			if (accumulatedRecord1.record.losses < accumulatedRecord2.record.losses){
				return -1;
			}
			else if (accumulatedRecord1.record.losses > accumulatedRecord2.record.losses){
				return 1;
			}
			//If they have the same number of wins and losses...
			else {
				//Go by their name.
				if (accumulatedRecord1.player.name < accumulatedRecord2.player.name){
					return -1;
				}
				else if (accumulatedRecord1.player.name > accumulatedRecord2.player.name){
					return 1;
				}
			}
		}
		
		//Blah
		return 0;
	});
}



function sortChalkRecords(chalkGeniusRecords){
	
	chalkGeniusRecords.sort(function(chalkGeniusRecord1, chalkGeniusRecord2){
		
		//Most wins goes first.
		if (chalkGeniusRecord1.chalkRightCount > chalkGeniusRecord2.chalkRightCount){
			return -1;
		}
		else if (chalkGeniusRecord1.chalkRightCount < chalkGeniusRecord2.chalkRightCount){
			return 1;
		}
		//If they have the same number of wins...
		else {
			//Fewest losses goes first.
			if (chalkGeniusRecord1.chalkWrongCount < chalkGeniusRecord2.chalkWrongCount){
				return -1;
			}
			else if (chalkGeniusRecord1.chalkWrongCount > chalkGeniusRecord2.chalkWrongCount){
				return 1;
			}
			//If they have the same number of wins and losses...
			else {
				//Go by their name.
				if (chalkGeniusRecord1.player.name < chalkGeniusRecord2.player.name){
					return -1;
				}
				else if (chalkGeniusRecord1.player.name > chalkGeniusRecord2.player.name){
					return 1;
				}
			}
		}
		
		//Blah
		return 0;
	});
}



function sortGeniusRecords(chalkGeniusRecords){
	
	chalkGeniusRecords.sort(function(chalkGeniusRecord1, chalkGeniusRecord2){
		
		//Most wins goes first.
		if (chalkGeniusRecord1.geniusRightCount > chalkGeniusRecord2.geniusRightCount){
			return -1;
		}
		else if (chalkGeniusRecord1.geniusRightCount < chalkGeniusRecord2.geniusRightCount){
			return 1;
		}
		//If they have the same number of wins...
		else {
			//Fewest losses goes first.
			if (chalkGeniusRecord1.geniusWrongCount < chalkGeniusRecord2.geniusWrongCount){
				return -1;
			}
			else if (chalkGeniusRecord1.geniusWrongCount > chalkGeniusRecord2.geniusWrongCount){
				return 1;
			}
			//If they have the same number of wins and losses...
			else {
				//Go by their name.
				if (chalkGeniusRecord1.player.name < chalkGeniusRecord2.player.name){
					return -1;
				}
				else if (chalkGeniusRecord1.player.name > chalkGeniusRecord2.player.name){
					return 1;
				}
			}
		}
		
		//Blah
		return 0;
	});
}
