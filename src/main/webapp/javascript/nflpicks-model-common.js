function doesAnyDivisionRecordHaveATie(divisionRecords){
	
	for (var index = 0; index < divisionRecords.length; index++){
		divisionRecord = divsionRecords[index];
		
		var doesDivisionHaveTies = hasTies(divisionRecord.records);
		
		if (doesDivisionHaveTies){
			return true;
		}
	}
	
	return false;
}
/**
 * 
 * A dumb function to check if the given record has a tie or not. Yep.
 * 
 * @param record
 * @returns
 */
function hasTie(record){
	
	if (!isDefined(record)){
		return false;
	}
	
	if (record.ties > 0){
		return true;
	}
	
	return false;
}


/**
 * 
 * A "convenience" function that says whether any record in the given
 * array has any ties.
 * 
 * @param records
 * @returns
 */
function hasTies(records){
	
	//Steps to do:
	//	1. Go through all the records and return true if one has a tie.
	
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

/**
 * 
 * This function will compare the given records and return -1 if the first record
 * has more wins than the second, 1 if it has more, and 0 if it's the same.
 * 
 * If the records have the same number of wins, then it bases the comparison on the
 * losses.  If record1 has the same wins but fewer losses, it should go first, so it 
 * returns -1.
 * 
 * It'll return 0 if they have the exact same number of wins and losses.
 * 
 * We do this in a few different places, so I decided to make a function.
 * 
 * @param record1
 * @param record2
 * @returns
 */
function recordWinComparisonFunction(record1, record2){
	
	//Steps to do:
	//	1. Compare based on the wins first.
	//	2. If they're the same, compare on the losses.
	
	//More wins should go first.
	if (record1.wins > record2.wins){
		return -1;
	}
	//Fewer wins should go last.
	else if (record1.wins < record2.wins){
		return 1;
	}
	else {
		//With the same number of wins, fewer losses should go first.
		if (record1.losses < record2.losses){
			return -1;
		}
		//And more losses should go second.
		else if (record1.losses > record2.losses){
			return 1;
		}
	}
	
	//Same wins and losses = same record.
	return 0;
}



/**
 * 
 * This function will get the number rank of the given object in the given list.
 * It will use the given comparison function to compare the object with other objects
 * in the given list to decide where it fits, and it'll use the given "sameObjectFunction"
 * to make sure an object doesn't "tie with itself".
 * 
 * I made it because I wanted to do it in a few places (the original standings, weeks won standings,
 * and a few other places).
 * 
 * The given list <i>doesn't</i> need to be sorted in order for it to find the object's
 * rank.  
 * 
 * It'll return an object that's like: {rank: 12, tie: true}, so people can get the
 * numeric rank and whether it ties any other object in the list.
 * 
 * Doing it this way, you'll have to call it for every object in the list ... This function
 * is O(n), so that makes ranking every object in a list O(n^2).  That kind of sucks,
 * but it should be ok because we shouldn't be sorting hundreds or thousands of objects.
 * I felt like it was ok to give up some speed to have it so each of the "standings"
 * functions not have to duplicate the ranking code.
 * 
 * @param object
 * @param list
 * @param comparisonFunction
 * @param sameObjectFunction
 * @returns
 */
function rank(object, list, comparisonFunction, sameObjectFunction){

	//Steps to do:
	//	1. Create the initial "rank" for the object and assume it has the highest rank.
	//	2. Go through each object in the list and run the comparison object on it.
	//	3. If it returns a positive number, that means the object is after the current
	//	   one we're comparing it to, so we need to up the rank.
	//	4. If it says they're the same, and the object hasn't already tied another object, 
	//	   then use the "sameObjectFunction" to see whether they're the exact same object or not.
	//	5. If they aren't, then it's a real tie.  If they aren't, then it's not.
	
	var objectRank = {rank: 1, tie: false};
	
	var numberOfRecordsBetter = 0;
	var tie = false;
	
	for (var index = 0; index < list.length; index++){
		var currentObject = list[index];
		
		var comparisonResult = comparisonFunction(object, currentObject);
		
		if (comparisonResult > 0){
			objectRank.rank++;
		}
		else if (comparisonResult == 0){

			if (objectRank.tie == false){
				var isSameObject = sameObjectFunction(object, currentObject);

				if (!isSameObject){
					objectRank.tie = true;
				}
			}
		}
	}
	
	return objectRank;
}

/**
 * 
 * A convenience function that'll sort the given weekRecords array
 * by each weekRecord's length.  A "weekRecord" will have a list
 * of records for each week inside it.  This will sort it so that
 * the one with the most weeks comes first.  
 * 
 * This is for when we're ranking how many weeks a person has won and
 * we want the person who's won the most weeks (has the most records)
 * first.
 * 
 * @param weekRecords
 * @returns
 */
function sortWeekRecords(weekRecords){
	
	//Steps to do:
	//	1. Just run the sorting function on the records we were given.
	
	weekRecords.sort(function (weekRecord1, weekRecord2){
		if (weekRecord1.weekRecords.length > weekRecord2.weekRecords.length){
			return -1;
		}
		else if (weekRecord1.weekRecords.length < weekRecord2.weekRecords.length){
			return 1;
		}

		//Sort it alphabetically by name if they have the same record.
		if (weekRecord1.player.name < weekRecord2.player.name){
			return -1;
		}
		else if (weekRecord1.player.name > weekRecord2.player.name){
			return 1;
		}
		return 0;
	});
}

/**
 * 
 * This function will sort the given week records by season and week
 * so that the "oldest" records appear first.  It's here for when we're showing
 * how many weeks a person one and we want to show the weeks in chronological
 * order.  This function will make sure they're in chronological order.
 * 
 * @param weekRecords
 * @returns
 */
function sortWeekRecordsBySeasonAndWeek(weekRecords){
	
	//Steps to do:
	//	1. Just run the sorting function on the array 
	//	   we were given.
	
	weekRecords.sort(function (weekRecord1, weekRecord2){
		var year1 = parseInt(weekRecord1.season.year);
		var year2 = parseInt(weekRecord2.season.year);
		
		//If the year from one is before the other, we want the earlier one first.
		if (year1 < year2){
			return -1;
		}
		//And later one second.
		else if (year1 > year2){
			return 1;
		}
		else {
			//Otherwise, compare on the weeks.
			var week1 = weekRecord1.week.weekSequenceNumber;
			var week2 = weekRecord2.week.weekSequenceNumber;
			
			//With the earlier week first.
			if (week1 < week2){
				return -1;
			}
			else if (week1 > week2){
				return 1;
			}
		}
		
		return 0;
	});
}

/**
 * 
 * This function will sort the given array of "week records" so that it
 * goes in ascending order by the week's year and week (so it's in increasing
 * order by season and by week within each season).
 * 
 * @param weekRecords
 * @returns
 */
function sortWeekRecordsBySeasonWeekAndRecord(weekRecords){
	
	//Steps to do:
	//	1. Just run the sorting function on the array 
	//	   we were given.
	
	weekRecords.sort(function (weekRecord1, weekRecord2){
		var year1 = parseInt(weekRecord1.season.year);
		var year2 = parseInt(weekRecord2.season.year);
		
		//If the year from one is before the other, we want the earlier one first.
		if (year1 < year2){
			return -1;
		}
		//And later one second.
		else if (year1 > year2){
			return 1;
		}
		//If it's the same year...
		else {
			//Compare on the weeks.
			var week1 = weekRecord1.week.weekSequenceNumber;
			var week2 = weekRecord2.week.weekSequenceNumber;
			
			//With the earlier week first.
			if (week1 < week2){
				return -1;
			}
			else if (week1 > week2){
				return 1;
			}
			//same week, so sort by the record.
			else {
				if (weekRecord1.record.wins > weekRecord2.record.wins){
					return -1;
				}
				else if (weekRecord1.record.wins < weekRecord2.record.wins){
					return 1;
				}
				else {
					if (weekRecord1.record.losses < weekRecord2.record.losses){
						return -1;
					}
					else if (weekRecord1.record.losses > weekRecord2.record.losses){
						return 1;
					}
					//Same year, week, wins, and losses, sort by the name
					else {
						if (weekRecord1.player.name < weekRecord2.player.name){
							return -1;
						}
						else if (weekRecord1.player.name > weekRecord2.player.name){
							return 1;
						}
					}
				}
			}
		}
		
		return 0;
	});
}


/**
 * 
 * This function will get the winning percentage.  Here because we almost always want
 * it formatted the same way, so I figured it was better to do it in a function.
 * 
 * @param wins
 * @param losses
 * @returns
 */
function getWinningPercentage(wins, losses){
	
	//Steps to do:
	//	1. Get the actual percentage.
	//	2. Make it 3 decimal places if it's a real number and blank if it's not.
	
	var percentage = wins / (wins + losses);
	
	var percentageString = '';
	if (!isNaN(percentage)){
		percentageString = percentage.toPrecision(3);
	}
	
	return percentageString;
}

/**
 * 
 * A convenience function for sorting players by their name.  Here in case
 * we do it in multiple places.
 * 
 * @param players
 * @returns
 */
function sortPlayersByName(players){
	
	//Steps to do:
	//	1. Just compare each player by its name.
	
	players.sort(function (player1, player2){
		
		if (player1.name < player2.name){
			return -1;
		}
		else if (player1.name > player2.name){
			return 1;
		}
		
		return 0;
	});
}

/**
 * 
 * Here so we can make sure the pick accuracies are in the right order (with the most
 * accurate team coming first).
 * 
 * @param pickAccuracySummaries
 * @returns
 */
function sortPickAccuracySummariesByTimesRight(pickAccuracySummaries){
	
	//Steps to do:
	//	1. Just sort them by how many times the person was right picking a team.
	
	pickAccuracySummaries.sort(function (pickAccuracySummaryA, pickAccuracySummaryB){

		//More times right = front of the list.
		if (pickAccuracySummaryA.timesRight > pickAccuracySummaryB.timesRight){
			return -1;
		}
		//Fewer times right = back of the list.
		else if (pickAccuracySummaryA.timesRight < pickAccuracySummaryB.timesRight){
			return 1;
		}
		
		//If they have the same times right, sort on times wrong.
		if (pickAccuracySummaryA.timesWrong < pickAccuracySummaryB.timesWrong){
			return -1;
		}
		//Fewer times right = back of the list.
		else if (pickAccuracySummaryA.timesWrong > pickAccuracySummaryB.timesWrong){
			return 1;
		}
		
		//If they have the same times right and wrong, sort by player name.
		if (pickAccuracySummaryA.player.name < pickAccuracySummaryB.player.name){
			return -1;
		}
		else if (pickAccuracySummaryA.player.name > pickAccuracySummaryB.player.name){
			return 1;
		}
		
		//If they have the same player name, sort by team abbreviation.
		if (pickAccuracySummaryA.team.abbreviation < pickAccuracySummaryB.team.abbreviation){
			return -1;
		}
		else if (pickAccuracySummaryA.team.abbreviation > pickAccuracySummaryB.team.abbreviation){
			return 1;
		}
		return 0;
		
	});
}


/**
 * 
 * Here so we can make sure the pick accuracies are in the right order (with the most
 * accurate team coming first).  Separate from the other function because there are no players.
 * 
 * @param collectivePickAccuracySummaries
 * @returns
 */
function sortCollectivePickAccuracySummariesByTimesRight(collectivePickAccuracySummaries){
	
	//Steps to do:
	//	1. Just sort them by how many times the person was right picking a team.
	
	collectivePickAccuracySummaries.sort(function (pickAccuracySummaryA, pickAccuracySummaryB){

		//More times right = front of the list.
		if (pickAccuracySummaryA.timesRight > pickAccuracySummaryB.timesRight){
			return -1;
		}
		//Fewer times right = back of the list.
		else if (pickAccuracySummaryA.timesRight < pickAccuracySummaryB.timesRight){
			return 1;
		}
		
		//If they have the same times right, sort on times wrong.
		if (pickAccuracySummaryA.timesWrong < pickAccuracySummaryB.timesWrong){
			return -1;
		}
		//Fewer times right = back of the list.
		else if (pickAccuracySummaryA.timesWrong > pickAccuracySummaryB.timesWrong){
			return 1;
		}
		
		//If they have the same times right and wrong, sort by team abbreviation.
		if (pickAccuracySummaryA.team.abbreviation < pickAccuracySummaryB.team.abbreviation){
			return -1;
		}
		else if (pickAccuracySummaryA.team.abbreviation > pickAccuracySummaryB.team.abbreviation){
			return 1;
		}
		return 0;
		
	});
}


/**
 * 
 * This function will create a link that'll take them to the picks for the given
 * year, week, team, and player (all optional) and show the given text.  Here because
 * we wanted to do this in a few places, so I figured it was best to do it as a function.
 * 
 * @param linkText
 * @param year
 * @param week
 * @param team
 * @param player
 * @returns
 */
function createPicksLink(linkText, year, week, team, player){
	
	//Steps to do:
	//	1. Just make a link that'll call the javascript function
	//	   that actually updates the view.
	//	2. All the arguments are optional, so only add them in if
	//	   they're given.
	
	var picksLink = '<a href="javascript:" onClick="showPickView(';
	
	if (isDefined(year)){
		var yearValue = year;
		if (Array.isArray(year)){
			yearValue = arrayToDelimitedValue(year, ',');
		}
		picksLink = picksLink + '\'' + yearValue + '\', ';
	}
	else {
		picksLink = picksLink + 'null, ';
	}
	
	if (isDefined(week)){
		var weekValue = week;
		if (Array.isArray(week)){
			weekValue = arrayToDelimitedValue(week, ',');
		}
		picksLink = picksLink + '\'' + weekValue + '\', ';
	}
	else {
		picksLink = picksLink + 'null, ';
	}
	
	if (isDefined(team)){
		var teamValue = team;
		if (Array.isArray(team)){
			teamValue = arrayToDelimitedValue(team, ',');
		}
		picksLink = picksLink + '\'' + teamValue + '\', ';
	}
	else {
		picksLink = picksLink + 'null, ';
	}
	
	if (isDefined(player)){
		var playerValue = player;
		if (Array.isArray(player)){
			playerValue = arrayToDelimitedValue(player, ',');
		}
		picksLink = picksLink + '\'' + playerValue + '\'';
	}
	else {
		picksLink = picksLink + 'null';
	}
	
	picksLink = picksLink + ');">' + linkText + '</a>';
	
	return picksLink;
}


/**
 * 
 * This function will shorten the given label so it's easier
 * to show on phones and stuff with small widths.  Up yours, twitter
 * and bootstrap.
 * 
 * @param label
 * @returns
 */
function shortenWeekLabel(label){

	if ('Playoffs - Wild Card' == label){
		return 'Wild card';
	}
	else if ('Playoffs - Divisional' == label){
		return 'Divisional';
	}
	else if ('Playoffs - Conference Championship' == label || 'Conference Championship' == label){
		return 'Conf champ';
	}
	else if ('Playoffs - Super Bowl' == label){
		return 'Super bowl';
	}
	
	return label;
}