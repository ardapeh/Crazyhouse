package de.tuberlin.sese.swtpp.gameserver.model.crazyhouse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;


public class Board implements Serializable{
	
	char Tahta[][];
	String Reserve;
	String columns = "abcdefgh";
	ArrayList<Piece> pieces = new ArrayList<>();
	ArrayList<String> emptySpots = new ArrayList<>();

	
	public Board(String state) {
		this.Tahta = new char[8][8];
		setBoardFEN(state);
		Reserve = "";
			
	}

	//Getters, Setters
	public String getBoardFEN() {		
		return getBoard()+this.Reserve;
	}
	

	//Prepare board for a given FEN-String(excl. Reserve)
	public void setBoard(String state) {
		int row = 0;
		int column = 0;
		int char2digit = 0;
		
		for (int i = 0; i < state.length(); i++) {
			if (state.charAt(i) == '/') {
				row++;
				column = 0;
				continue;
			}
			else if(Character.isDigit(state.charAt(i))){
				char2digit = Character.getNumericValue(state.charAt(i));
				while(char2digit != 0) {
					this.Tahta[row][column] = '-';
					if(column != 7)
						column++;
					char2digit--;
				}
				continue;
			}else
			
				this.Tahta[row][column] = state.charAt(i);
				if(column != 7)
					column++;
				
		}
		
	}
	
	//Prepare Board for a given FEN-String(incl Reserve)
	public void setBoardFEN(String state) {
		this.pieces.removeAll(this.pieces);
		this.Reserve = "";
		StringBuilder noReserve = new StringBuilder();
		int counter = 0;
		int reserveStartindex = 0;
		char[] charArray = state.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			if(charArray[i] == '/')
				counter++;
			
			noReserve.append(charArray[i]);
			if(counter == 8) {
				reserveStartindex = i+1;
				break;
			}
				
		}
		setBoard(noReserve.toString());
		createPieces();
		
		for (int i = reserveStartindex; i < charArray.length; i++) {
			this.pieces.add(new Piece(charArray[i], "r"));
		}
		saveEmptySpots();
		updateBoardReserve();
		
		
		
	}
	
	
	//Get the board as FEN-String(excl. Reserve)
	public String getBoard() {
		StringBuilder output = new StringBuilder();
		int counter = 0;
		for (int i = 0; i < this.Tahta.length; i++) {
			for (int j = 0; j < this.Tahta.length; j++) {
				if(this.Tahta[i][j] == '-') {
					counter++;
					
				}else if(counter > 0) {
					output.append(Integer.toString(counter) + this.Tahta[i][j]);
					counter = 0;
					
				}else {
					output.append(this.Tahta[i][j]);
				}				
			}
			if(counter > 0) {
				output.append(counter);
				counter = 0;
			}
			output.append('/');
		}
		return output.toString();
	}
	
	//Save Reserve as String
	public void updateBoardReserve() {
		StringBuilder unsorted = new StringBuilder();
		
		for(Piece parca: pieces) {
			if(parca.getPosition() == "r")
				unsorted.append(parca.type);
		}
		char[] charArray = unsorted.toString().toCharArray();
		Arrays.sort(charArray);
		String sorted = new String(charArray);
		
		
		this.Reserve = sorted;
	}
		
	//EOF Getters, Setters
	
	
	//Methods
	
	//Save empty spots on Board
	public void saveEmptySpots() {
		this.emptySpots.removeAll(emptySpots);
		
		for(int i = 0; i < this.Tahta.length; i++) {
			for(int j = 0; j < this.Tahta.length; j++) {
				StringBuilder empty = new StringBuilder();
				if(this.Tahta[i][j] == '-') {					
					empty.append(columns.charAt(j));
					empty.append(8-i);
					this.emptySpots.add(empty.toString());
				}
			}
		}
	}
	
	
	//Create all pieces on Board (incl. reserves)
	public void createPieces() {
		
		for(int i = 0; i < this.Tahta.length; i++) {
			for(int j = 0; j < this.Tahta.length; j++) {
				if(this.Tahta[i][j] == '-')
					continue;
				StringBuilder pos = new StringBuilder();
				
				pos.append(columns.charAt(j));
				pos.append(8-i);
				
				pieces.add(new Piece(this.Tahta[i][j], pos.toString()));
			}
		}
		
	}
	
	//Access piece for given position on board
	public Piece getPiece(String pos) {
		Piece output = null;
		if(emptySpots.contains(pos))
			return output = new Piece(pos);
		
		for(Piece temp: pieces) {
			if(temp.getPosition().equals(pos)) {
				output = temp;
				break;
			}
			
		}
		
		return output;
	}
		
	/*
	//Print board for testing
		public void print() {
			for (int i = 8; i > 0; i--) {
				for (int j = 0; j < this.Tahta.length; j++) {
					if(j == 0)
						System.out.print(i + " ");
					System.out.print("[" + this.Tahta[8-i][j] + "]");
				}
				System.out.println();
			}
			String columnss = "ABCDEFGH";
			System.out.print("   ");
			for (int i = 0; i < 8; i++) {
				System.out.print(columnss.charAt(i) + "  ");
			}
			

		}
	
	*/
}
