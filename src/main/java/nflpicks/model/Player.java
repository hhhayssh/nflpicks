package nflpicks.model;

import java.util.List;

/**
 * 
 * Represents a player ... Like Spare Tire.  Look it up.
 * 
 * @author albundy
 *
 */
public class Player {
	
	/**
	 * 
	 * The id of the player ... kind of sucks having a player with
	 * an id because it reduces the person but I went with it because it makes
	 * more sense.  I might change it back to being player name so that people never
	 * get an id assigned to them.
	 * 
	 */
	protected int id;
	
	/**
	 * 
	 * The name of the player blah blah blah.
	 * 
	 */
	protected String name;
	
	//should PlayerDivision be in here or should it be external?
	//if it's external, then everything that references it kind of has to change a little i think
	//it should be a list so people can be in more than one division
	//
	//There should be a PlayerDivisionRelationship class and that can be in here
	//or, this can be a list of PlayerDivision?
	//but, a PlayerDivision could have a list of players too
	//or, there could just be a list of PlayerDivision objects
	//
	//the PlayerDivisionRelationship object should have a reference to the player and the
	//
	//	PlayerDivision - player_division
	//		Joe Montana Sports Talk Football
	//		John Madden Football
	//
	//	PlayerDivisionRelationship - player_division_relationship
	//		player_division_id <-> player_id
	//		PlayerDivisionRelationship could be a shallow object with:
	//			player_division_id
	//			divisionName
	//			playerId
	//			playerName
	//
	//	i need to be able to enable and disable divisions too (maybe using a property?)
	//
	//
	//PlayerDivision needs:
	//	id
	//	name
	//	list of players
	//
	//does this object need a list of PlayerDivisionRelationships?
	//it could have them
	//
	//when the Record objects come back from the server, we just need a way to group them
	//or, maybe they should come back grouped by division?
	//	DivisionRecord
	//		Division - division object (with player objects in it)
	//		records - List - Record (with the records for players in that division
	//	
	//yeah i think that's the way to go instead of trying to shove it in this object.
	//
	//when the target is "standings", there just needs to be another variable that says whether you want
	//them by division or not
	//if it's not given, the property that says whether divisions are enabled should control it
	//if it's true, then it should send bak DivisionRecord objects
	//if it's false, it should send back Record objects
	//		
	
	//this could just be "divisions"?
	//like in javascript, i want it to be easy...
	//player.playerDivisionRelationships[index] ... hmm i guess i could call that whatever i wanted to because it's going to
	//iterate through it anyway
	//
	//or, i could refactor it so it's "Division" for the player division and "TeamDivision" for the team division
	//then, PlayerDivision would be the relationship between the two
	//the table names would have to change though
	//it would be "team_conference"
	//"team_division"
	//	... i think that's the way to go because i never use the conference or division anyway
	//
	//I could also make a different package
	//	pick
	//		CompactPick
	//		CompactPickGrid
	//		CompactPlayerPick
	//		Pick
	//		PickSplit
	//	team
	//		Conference
	//		Division
	//		Team
	//	game
	//		Game
	//		Week
	//		Season
	//	player
	//		Player
	//		Division
	//	record
	//		Record
	//		DivisionRecord
	//
	//I think the different packages is the way to go
	//just need to rip it up and then put it back together again
	//
	//and then i need to break up the javascript files some more
	//
	//how should the view be?
	//
	//	select more than one
	//	everybody
	//	all divisions
	//		montana division
	//		madden division
	//	benny boy
	//	boo
	//	...
	//
	//should the divisions be "retro active"?
	//yes when it comes to showing the standings, but no when it comes to championships?
	//or, just yes period?
	//
	//if you pick the montana division and then like 2018 as the year, what happens?
	//it could...
	//	1. show nothing
	//	2. show the people who are currently in the montana division
	//	3. change the selection
	//
	//what makes the most sense?
	//either 1 or 2
	//
	//how should it handle people switching divisions?
	//there needs to be a history because there are division titles
	//will anything outside the division title page worry about when
	//somebody was in a particular division?
	//it doesn't seem like it
	//
	//except for the division title page, and the standings page, every other place should
	//treat it like just a grouping of players
	//
	//how about the picks page?
	//if i pick 2018 and the montana division, what should happen?
	//
	//if it shows nothing, then that means it ties the division to a year
	//if it shows the current division, then it doesn't
	//
	//if a person switches divisions, and we treat the divisions as just groupings of players,
	//that could change the division titles
	//
	//i think a table where it has
	//	player_id, division_id, season_id
	//is the way to go
	//
	//this means it would show nothing if they pick 2018 and then a division
	//
	//i think that makes the most sense
	//
	//otherwise, it would have to be "they apply everywhere but pages x, y, and z"
	//
	//
	//
	//how should it get the records?
	//should it get the records and then group them by division in java?
	//or, should it get the records by division in sql?
	//if you want to show the all time records for a division and a person
	//switched divisions, you' have to detect that in sql because, by the time
	//it gets to java, it's too late.
	
	public Player(){
	}
	
	/**
	 * 
	 * Lets you make a player without using all the setters.
	 * 
	 * @param id
	 * @param name
	 */
	public Player(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 
	 * The hash code should turn this object into a relatively unique number
	 * so that it can be identified by that number easily and so (hopefully)
	 * there aren't that many "collisions" with other objects.  
	 * 
	 * It starts at a prime number repeatedly multiplies and adds the hash codes
	 * of the variables of the objects in this class.  I don't have that great of a handle
	 * on why it's done this way (check the internet if you care) but I know
	 * what it's trying to do.
	 * 
	 */
	@Override
	public int hashCode(){
		
		int primeNumber = 31;
		
		int result = 1;
		
		result = primeNumber * result + Integer.valueOf(id).hashCode();
		result = primeNumber * result + (name == null ? 0 : name.hashCode());
		
		return result;
	}
	
	/**
	 * 
	 * Returns true if the given object has all the same values for all
	 * the variables in this object.
	 * 
	 */
	@Override
	public boolean equals(Object object){
		
		//Steps to do:
		//	1. If the given object is this object, it's equal.
		//	2. If it's null or isn't an instance of this class, it's not equal.
		//	3. Otherwise, just go down through each variable and return
		//	   false if it's not equal.
		//	4. If we get to the end, then all the variables "weren't not equal"
		//	   so that means the object is equal to this one.
		
		if (object == this){
			return true;
		}
		
		if (object == null || !(object instanceof Player)){
			return false;
		}
		
		Player otherPlayer = (Player)object;
		
		int otherId = otherPlayer.getId();
		
		if (id != otherId){
			return false;
		}
		
		String otherName = otherPlayer.getName();
		
		if (name != null){
			if (!name.equals(otherName)){
				return false;
			}
		}
		else {
			if (otherName != null){
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * 
	 * Sends back a string with all the variables in this object.
	 * 
	 */
	@Override
	public String toString(){
		
		String thisObjectAsAString = "id = /*PEOPLE SHOULND'T HAVE IDS!!!* " + id + 
									 ", name = " + name;
		
		return thisObjectAsAString;
	}
}
