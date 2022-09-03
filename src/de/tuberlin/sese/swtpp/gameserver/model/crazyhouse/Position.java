package de.tuberlin.sese.swtpp.gameserver.model.crazyhouse;

import java.io.Serializable;

public class Position implements Serializable {
	
	//Attributes
	
	char column;
	int row;
	boolean reserve;
	
	public Position(char harf, int sayi) {
		this.column = harf;
		this.row = sayi; 
		this.reserve = false;
	}
	
	public Position() {
		this.reserve = true;
	}
	
	// Methods
	/*
	public char getColumn() {
		return this.column;
	}
	
	public void setColumn(char harf) {
		this.column = harf;
	}
	
	public int getRow() {
		return this.row;
	}
	
	public void setRow(int sayi) {
		this.row = sayi;
	}
	
	public boolean getReserve() {
		
		return this.reserve;
	}
	
	public void setReserve(boolean res) {
		this.reserve = res;
	}
	*/
	public String getPosition() {
		StringBuilder output = new StringBuilder();
		if(this.reserve)
			return "r";
		
		output.append(this.column);
		output.append(this.row);
		
		return output.toString();
			
	}
	
	
	
}
