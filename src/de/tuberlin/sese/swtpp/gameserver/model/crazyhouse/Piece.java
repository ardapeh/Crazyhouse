package de.tuberlin.sese.swtpp.gameserver.model.crazyhouse;

import java.io.Serializable;
import java.lang.*;

public class Piece implements Serializable {

	//Attributes
	char farbe; // 'w' = White, 'b' = Black
	char type; 
	boolean inReserve;
	Position pos;
	
	//Constructor
	public Piece(char parca, String konum) {
		if(konum.equals("r")) {
		   setInReserve(true);
		}else {
			setInReserve(false);
		}
		
		if(Character.isUpperCase(parca)) {
			setFarbe('w');
			setType(parca);
		}
		else {
				setFarbe('b');
				setType(parca);
			}
		setPosition(konum);
	}
	
	public Piece(String konum) {
		setFarbe('e');
		setType('-');
		setPosition(konum);
		setInReserve(false);
	}
	
	//Getters, Setters
	public void setFarbe(char renk) {
		this.farbe = renk;
	}
	
	public char getFarbe() {
		return this.farbe;
	}
	
	
	public void setType(char tip) {
		this.type = tip;
	}
	
	public char getType() {
		return this.type;
	}
	
	public void setInReserve(boolean reserve) {
		this.inReserve = reserve;
	}
	
	public void setPosition(String pos) {
		if(pos.equals("r")) {
			this.pos = new Position();
		}else {		
		this.pos = new Position(pos.charAt(0), Character.getNumericValue(pos.charAt(1)));
		}
	}
	
	public String getPosition() {
		
		return this.pos.getPosition();
	}
	
	
	//Print piece info for testing
	/*
	public String toString() {
		String renk = "";
		String position = "";
		
		if(getFarbe() == 'w') {
			renk = "White";
		}else {
			renk = "Black";
		}
		
		
		if(getInReserve())
			return "This piece is " + renk + " and in reserve. (Type: " + getType() + ")";
		
		
		return "This piece is " + renk + " and in " + getPosition() + ". (Type: " + getType() + ")";
	}
	
	*/
}
