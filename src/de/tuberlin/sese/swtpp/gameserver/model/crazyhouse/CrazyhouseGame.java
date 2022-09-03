package de.tuberlin.sese.swtpp.gameserver.model.crazyhouse;

import java.io.Serializable;
import java.util.ArrayList;

import de.tuberlin.sese.swtpp.gameserver.model.Game;
import de.tuberlin.sese.swtpp.gameserver.model.Move;
import de.tuberlin.sese.swtpp.gameserver.model.Player;
import de.tuberlin.sese.swtpp.gameserver.model.User;

public class CrazyhouseGame extends Game implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 5424778147226994452L;

	/************************
	 * member
	 ***********************/

	// just for better comprehensibility of the code: assign white and black player
	private Player blackPlayer;
	private Player whitePlayer;
	
	// internal representation of the game state
	// TODO: insert additional game data here
	private Board Board;
	private ArrayList<String> validMovesWhite = new ArrayList<>();
	private ArrayList<String> validMovesBlack = new ArrayList<>();

	String columns ="abcdefgh";
	Piece parcaYenilen; // temporarily store the piece that gets eaten(?)
	Piece parcaYiyen;  // temporarily store the piece to be moved

	
	/************************
	 * constructors
	 ***********************/

	public CrazyhouseGame() {
		super();

		// TODO: initialize internal model if necessary 
		Board = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/");
		parcaYenilen = null;
		parcaYiyen = null;
		
	}

	public String getType() {
		return "crazyhouse";
	}

	/*******************************************
	 * Game class functions already implemented
	 ******************************************/

	@Override
	public boolean addPlayer(Player player) {
		if (!started) {
			players.add(player);

			// game starts with two players
			if (players.size() == 2) {
				started = true;
				this.whitePlayer = players.get(0);
				this.blackPlayer= players.get(1);
				nextPlayer = whitePlayer;
			}
			return true;
		}

		return false;
	}

	@Override
	public String getStatus() {
		if (error)
			return "Error";
		if (!started)
			return "Wait";
		if (!finished)
			return "Started";
		if (surrendered)
			return "Surrendered";
		if (draw)
			return "Draw";

		return "Finished";
	}

	@Override
	public String gameInfo() {
		String gameInfo = "";

		if (started) {
			if (blackGaveUp())
				gameInfo = "black gave up";
			else if (whiteGaveUp())
				gameInfo = "white gave up";
			else if (didWhiteDraw() && !didBlackDraw())
				gameInfo = "white called draw";
			else if (!didWhiteDraw() && didBlackDraw())
				gameInfo = "black called draw";
			else if (draw)
				gameInfo = "draw game";
			else if (finished)
				gameInfo = blackPlayer.isWinner() ? "black won" : "white won";
		}

		return gameInfo;
	}

	@Override
	public String nextPlayerString() {
		return isWhiteNext() ? "w" : "b";
	}

	@Override
	public int getMinPlayers() {
		return 2;
	}

	@Override
	public int getMaxPlayers() {
		return 2;
	}

	@Override
	public boolean callDraw(Player player) {

		// save to status: player wants to call draw
		if (this.started && !this.finished) {
			player.requestDraw();
		} else {
			return false;
		}

		// if both agreed on draw:
		// game is over
		if (players.stream().allMatch(Player::requestedDraw)) {
			this.draw = true;
			finish();
		}
		return true;
	}

	@Override
	public boolean giveUp(Player player) {
		if (started && !finished) {
			if (this.whitePlayer == player) {
				whitePlayer.surrender();
				blackPlayer.setWinner();
			}
			if (this.blackPlayer == player) {
				blackPlayer.surrender();
				whitePlayer.setWinner();
			}
			surrendered = true;
			finish();

			return true;
		}

		return false;
	}

	/* ******************************************
	 * Helpful stuff
	 ***************************************** */

	/**
	 *
	 * @return True if it's white player's turn
	 */
	public boolean isWhiteNext() {
		return nextPlayer == whitePlayer;
	}

	/**
	 * Ends game after regular move (save winner, finish up game state,
	 * histories...)
	 *
	 * @param winner player who won the game
	 * @return true if game was indeed finished
	 */
	public boolean regularGameEnd(Player winner) {
		// public for tests
		if (finish()) {
			winner.setWinner();
			winner.getUser().updateStatistics();
			return true;
		}
		return false;
	}

	public boolean didWhiteDraw() {
		return whitePlayer.requestedDraw();
	}

	public boolean didBlackDraw() {
		return blackPlayer.requestedDraw();
	}

	public boolean whiteGaveUp() {
		return whitePlayer.surrendered();
	}

	public boolean blackGaveUp() {
		return blackPlayer.surrendered();
	}

	/*******************************************
	 * !!!!!!!!! To be implemented !!!!!!!!!!!!
	 ******************************************/

	@Override
	public void setBoard(String state) {
		// Note: This method is for automatic testing. A regular game would not start at some artificial state.
		//       It can be assumed that the state supplied is a regular board that can be reached during a game.
		// TODO: implement
		Board.setBoardFEN(state);
	}

	@Override
	public String getBoard() {
		// TODO: implement
		
		// replace with real implementation
		return Board.getBoardFEN();
	}

	@Override
	public boolean tryMove(String moveString, Player player) {
		// TODO: implement
		fillValidMoves();
		
		if(!finished && getNextPlayer().equals(player) ) {			
			if(isWhiteNext()) {		
				if(this.validMovesWhite.contains(moveString)) {
					this.history.add(new Move(moveString, this.getBoard(), player));
					doMove(moveString, this.Board);
					fillValidMoves();
					if(checkIfInCheckBlack(this.Board) && this.validMovesBlack.isEmpty()) 
							regularGameEnd(whitePlayer);				
					setNextPlayer(blackPlayer);	
					return true;
				}
			
			}else {
				if(this.validMovesBlack.contains(moveString)) {
					this.history.add(new Move(moveString, this.getBoard(), player));
					doMove(moveString, this.Board);
					fillValidMoves();	
					if(checkIfInCheckWhite(this.Board) && this.validMovesWhite.isEmpty())
						regularGameEnd(blackPlayer);
					setNextPlayer(whitePlayer);
					return true;
				}
			}	
					
		}
			
		// replace with real implementation
		return false;
	}
		
	//Important methods
	
	public boolean checkIfInCheckWhite(Board board) {//true if white in check, false if it isnt
		Piece temp = null;
		
		for(Piece p: board.pieces) {
			if(p.getType() != 'K') 
				continue;
			temp = p;//pos: d5	
		
		}
		if(temp.inReserve) {
			return false;
		}
		if(wKingCheckDiagonals(temp, board)) 
			return true;	
		if(wKingCheckVerticals(temp, board)) 
			return true;			
		if(wKingCheckHorizontals(temp, board)) 
			return true;					
		if(wKingCheckKnights(temp, board)) 
			return true;	
	
		return false;
	}
	
	public boolean checkIfInCheckBlack(Board board) {//true if black in check, false if it isnt
		Piece temp = null;
		for(Piece p: board.pieces) {
			if(p.getType() != 'k') 
				continue;
			temp = p;//pos: d5	
		
		}
		
		if(temp.inReserve) {
			return false;
		}
		
		
		if(bKingCheckDiagonals(temp, board)) 
			return true;
		if(bKingCheckVerticals(temp, board)) 
			return true;	
		if(bKingCheckHorizontals(temp, board)) 
			return true;			
		if(bKingCheckKnights(temp, board)) 
			return true;	
			
		return false;
	}
	
	
	
	public void fillValidMoves() {
		validMovesWhite.removeAll(this.validMovesWhite);
		validMovesBlack.removeAll(this.validMovesBlack);
		fillValidWhite();
		fillValidBlack();
	}
	
	public void fillValidWhite() {
		ArrayList<String> movesToRemove = new ArrayList<>();
		for(Piece p: Board.pieces) {//Valid moves from Reserve for White
			if(!p.inReserve || p.getFarbe() == 'b')
				continue;
			for(String empty: Board.emptySpots) {
				if(p.getType() == 'P' && (empty.charAt(1) == '1' || empty.charAt(1) == '8' ))//pawns cant be dropped into rows 1-8
					continue;
				validMovesWhite.add(p.getType() + "-" + empty);
			}
		}
		
		//Valid White Pawn moves on the Board
		validWhitePawns(this.Board);
		//Valid White Rook moves on the Board
		validWhiteRooks(this.Board);
		//Valid White Bishop moves on the Board
		validWhiteBishops(this.Board);
		//Valid White Knight moves on the Board
		validWhiteKnights(this.Board);
		//Valid White Queen moves on the Board
		validWhiteQueens(this.Board);
		//Valid White King moves on the Board
		validWhiteKing(this.Board);
		
		
		for(String s: this.validMovesWhite) {
			Board tempBoard = new Board(this.getBoard());
			doMove(s, tempBoard);
			if(checkIfInCheckWhite(tempBoard) ) {
				movesToRemove.add(s);	
			}
		}
		
		this.validMovesWhite.removeAll(movesToRemove);
		
		
	}
	
	public void fillValidBlack() {
		ArrayList<String> movesToRemove = new ArrayList<>();
		for(Piece p: Board.pieces) {//Valid Moves from reserve for Black
			if(!p.inReserve || p.getFarbe() == 'w')
				continue;
			for(String empty: Board.emptySpots) {
				if(p.getType() == 'p' && (empty.charAt(1) == '1' || empty.charAt(1) == '8' ))//pawns cant be dropped into rows 1-8
					continue;
				validMovesBlack.add(p.getType() + "-" + empty);
			}
		}
		
		
		//Valid Black Pawn moves on the Board
		validBlackPawns(this.Board);		
		//Valid Black Rook moves on the Board
		validBlackRooks(this.Board);		
		//Valid Black Bishop moves on the Board
		validBlackBishops(this.Board);		
		//Valid Black Knight moves on the Board
		validBlackKnights(this.Board);		
		//Valid Black Queen moves on the Board		
		validBlackQueens(this.Board);		
		//Valid Black King moves on the Board
		validBlackKing(this.Board);
		
		
		for (String s: this.validMovesBlack) {
			Board tempBoard = new Board(this.getBoard());
			doMove(s, tempBoard);
			if(checkIfInCheckBlack(tempBoard)) {
				movesToRemove.add(s);	
			}
		}
		
		this.validMovesBlack.removeAll(movesToRemove);
		
		
	}
	
	public void doMove(String moveString, Board board) {
		StringBuilder to = new StringBuilder(moveString.substring(moveString.length()-2, moveString.length()));//a4
		StringBuilder from = new StringBuilder(moveString.substring(0, 2));//a2
		boolean promotionW = false;
		boolean promotionB = false;
		
		if(moveString.length() == 5) {
			this.parcaYiyen = board.getPiece(from.toString());
			if(checkPromotion(this.parcaYiyen, moveString) == 1)
				promotionW = true;
			if(checkPromotion(this.parcaYiyen, moveString) == -1)
				promotionB = true;
		}else {
			this.parcaYiyen = new Piece(moveString.charAt(0), "r");
		}
			
		this.parcaYenilen = board.getPiece(to.toString());
		
		
		
		if(moveString.length() == 4)
			dropPiece(moveString, board);
		else if(promotionW)
			movePieceOnBoard2(moveString, to.toString(), from.toString(), board);
		else if(promotionB)
			movePieceOnBoard2(moveString, to.toString(), from.toString(), board);
		else
			movePieceOnBoard1(moveString, to.toString(), from.toString(), board);
		
		board.updateBoardReserve();
		board.saveEmptySpots();
	}
	
		
	public String checkVertical(Piece p, int amount) {//a2-a3
		StringBuilder output = new StringBuilder();
		int row = Character.getNumericValue(p.getPosition().charAt(1)) + amount;
	
		output.append(p.getPosition().charAt(0));
		output.append(row);
		
		return output.toString();
	}
	
	
	
	public String checkHorizontal(Piece p, int amount) {//d3-e3
		int counter = 0;
		StringBuilder output = new StringBuilder();
		
		for(int i = 0; i < columns.toCharArray().length; i++) {
			if(columns.toCharArray()[i] != p.getPosition().charAt(0))
				continue;
			
			counter = i;			
		}
			
		output.append(columns.charAt(counter+amount));
		output.append(p.getPosition().charAt(1));
					
		return output.toString();
	}
	
	
	
	public String checkDiagonal(Piece p, int amount, char direction) {//f5-h3 
		StringBuilder output = new StringBuilder();
		int row = Character.getNumericValue(p.getPosition().charAt(1))+amount;
		int counter = this.columns.indexOf(p.getPosition().charAt(0));
		
		if(amount > 0 && direction == 'l') {//up-left
				output.append(columns.charAt(counter-amount));
				output.append(row);	
		}else if(amount < 0 && direction == 'l') {//down-left
			output.append(columns.charAt(counter+amount));
			output.append(row);			
		}else if(amount > 0 && direction == 'r') {//up-right
			output.append(columns.charAt(counter+amount));
			output.append(row);			
		}else {//down-right
			output.append(columns.charAt(counter-amount));
			output.append(row);			
		}	
		
				
		return output.toString();
		
	}
	
	
	//EOF Important methods, scroll down at your own risk!!!
	
	public void validWhitePawns(Board board) {
		validWhitePawnV(board);
		validWhitePawnD(board);
	}
	
	public void validBlackPawns(Board board) {
		validBlackPawnV(board);
		validBlackPawnD(board);
	}
	
	public void validWhiteRooks(Board board) {
		validWhiteRookUp(board, 'R');
		validWhiteRookDown(board, 'R');
		validWhiteRookLeft(board, 'R');
		validWhiteRookRight(board, 'R');
	}
	
	public void validBlackRooks(Board board) {
		validBlackRookUp(board, 'r');
		validBlackRookDown(board, 'r');
		validBlackRookLeft(board, 'r');
		validBlackRookRight(board, 'r');
	}
	
	public void validWhiteBishops(Board board) {
		validWhiteBishopNW(board, 'B');
		validWhiteBishopNE(board, 'B');
		validWhiteBishopSW(board, 'B');
		validWhiteBishopSE(board, 'B');
	}
	
	public void validBlackBishops(Board board) {
		validBlackBishopNW(board, 'b');
		validBlackBishopNE(board, 'b');
		validBlackBishopSW(board, 'b');
		validBlackBishopSE(board, 'b');
	}
	
	public void validWhiteKnights(Board board) {
		validWhiteKnight1(board);
		validWhiteKnight2(board);
		validWhiteKnight3(board);
		validWhiteKnight4(board);
		validWhiteKnight5(board);
		validWhiteKnight6(board);
		validWhiteKnight7(board);
		validWhiteKnight8(board);
	}
	
	public void validBlackKnights(Board board) {
		validBlackKnight1(board);
		validBlackKnight2(board);
		validBlackKnight3(board);
		validBlackKnight4(board);
		validBlackKnight5(board);
		validBlackKnight6(board);
		validBlackKnight7(board);
		validBlackKnight8(board);
	}
	
	public void validWhiteQueens(Board board) {
		validWhiteQueenD(board);
		validWhiteQueenVH(board);
	}
	
	public void validBlackQueens(Board board) {
		validBlackQueenD(board);
		validBlackQueenVH(board);
	}
	
	public void validWhiteKing(Board board) {
		validWhiteKingH(board);
		validWhiteKingV(board);
		validWhiteKingD1(board);
		validWhiteKingD2(board);
		validWhiteKingD3(board);
		validWhiteKingD4(board);
	}
	
	public void validBlackKing(Board board) {
		validBlackKingH(board);
		validBlackKingV(board);
		validBlackKingD1(board);
		validBlackKingD2(board);
		validBlackKingD3(board);
		validBlackKingD4(board);
	}
	
	public void validWhitePawnV(Board board) {
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != 'P')
				continue;
			if(board.getPiece(checkVertical(p, 1)).getType() != '-') {
				continue;
			}
			validMovesWhite.add(p.getPosition() + "-" + checkVertical(p, 1));
			if(p.getPosition().charAt(1) == '2' && board.getPiece(checkVertical(p, 2)).getType() == '-')
				validMovesWhite.add(p.getPosition() + "-" + checkVertical(p, 2));
			}	
		}
	
	public void validBlackPawnV(Board board) {
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != 'p')
				continue;
			if(board.getPiece(checkVertical(p, -1)).getType() != '-') {
				continue;
			}
			validMovesBlack.add(p.getPosition() + "-" + checkVertical(p, -1));
			if(p.getPosition().charAt(1) == '7' && board.getPiece((checkVertical(p, -2))).getType() == '-')
				validMovesBlack.add(p.getPosition() + "-" + checkVertical(p, -2));
			
			}		
		}
	
	public void validWhitePawnD(Board board) {
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != 'P')
				continue;
			if(p.getPosition().charAt(0) != 'a' && board.getPiece(checkDiagonal(p, 1, 'l')) != null) {
				if(board.getPiece(checkDiagonal(p, 1, 'l')).getFarbe() == 'b' ) {
					validMovesWhite.add(p.getPosition() + "-" + checkDiagonal(p, 1, 'l'));
			}
		}
		
		if(p.getPosition().charAt(0) != 'h' && board.getPiece(checkDiagonal(p, 1, 'r')) != null) {
			if(board.getPiece(checkDiagonal(p, 1, 'r')).getFarbe() == 'b') {
				validMovesWhite.add(p.getPosition() + "-" + checkDiagonal(p, 1, 'r'));
				}
			}
		}
			
	}
	
	public void validBlackPawnD(Board board) {
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != 'p') 
				continue;
			
			if(p.getPosition().charAt(0) != 'a' && board.getPiece(checkDiagonal(p, -1, 'l')) != null) {
				if(board.getPiece(checkDiagonal(p, -1, 'l')).getFarbe() == 'w' ) {
					validMovesBlack.add(p.getPosition() + "-" + checkDiagonal(p, -1, 'l'));
				}
			}
		
			if(p.getPosition().charAt(0) != 'h' && board.getPiece(checkDiagonal(p, -1, 'r')) != null) {
				if(board.getPiece(checkDiagonal(p, -1, 'r')).getFarbe() == 'w') {
					validMovesBlack.add(p.getPosition() + "-" + checkDiagonal(p, -1, 'r'));
					}
				}
			}
		
		}
	
	public void validWhiteRookUp(Board board, char type) {
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != type)
				continue;
			int counter = 0;
			for(int i = 1; i < 8; i++) {
				if(board.getPiece(checkVertical(p, i)) == null) {
					counter = i-1;
					break;
				}
				if(board.getPiece(checkVertical(p, i)).getFarbe() == 'w') {
					counter = i-1;
					break;
				}
				if(board.getPiece(checkVertical(p, i)).getFarbe() == 'b') {
					counter = i;
					break;
				}
				if(board.getPiece(checkVertical(p, i)).getType() == '-' )
					counter = i;
					continue;	
			}
			for(int j = 1; j < counter+1; j++) {//counter = 4, a3-a4, a3-a5, a3-a6, a3-a7
				this.validMovesWhite.add(p.getPosition() + "-" + checkVertical(p, j));
			}
			
		}
		
	}
	
	public void validWhiteRookDown(Board board, char type) {
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != type)
				continue;
			int counter = 0;
			for(int i = 1; i < 8; i++) {
				if(board.getPiece(checkVertical(p, -i)) == null) {
					counter = i-1;
					break;
				}
				if(board.getPiece(checkVertical(p, -i)).getFarbe() == 'w') {
					counter = i-1;
					break;
				}
				if(board.getPiece(checkVertical(p, -i)).getFarbe() == 'b') {
					counter = i;
					break;
				}
				if(board.getPiece(checkVertical(p, -i)).getType() == '-' )
					counter = i;
					continue;	
			}
			for(int j = 1; j < counter+1; j++) {
				this.validMovesWhite.add(p.getPosition() + "-" + checkVertical(p, -j));
			}
			
		}
	}
	
	public void validWhiteRookLeft(Board board, char type) {
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != type)
				continue;
			int counter = 0;
			int loop = this.columns.indexOf(p.getPosition().charAt(0));		
			for(int i = 1; i < loop+1 ; i++) {
				if(board.getPiece(checkHorizontal(p, -i)).getFarbe() == 'w') {
					counter = i-1;
					break;
				}
				if(board.getPiece(checkHorizontal(p, -i)).getFarbe() == 'b') {
					counter = i;
					break;
				}
				if(board.getPiece(checkHorizontal(p, -i)).getType() == '-' )
					counter = i;
					continue;	
			}
			for(int j = 1; j < counter+1; j++) {
				this.validMovesWhite.add(p.getPosition() + "-" + checkHorizontal(p, -j));
			}
			
		}
		
	}
	
	public void validWhiteRookRight(Board board, char type) {
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != type)
				continue;
			int counter = 0;
			int loop = 7-this.columns.indexOf(p.getPosition().charAt(0));		
			for(int i = 1; i < loop+1 ; i++) {
				if(board.getPiece(checkHorizontal(p, i)).getFarbe() == 'w') {
					counter = i-1;
					break;
				}
				if(board.getPiece(checkHorizontal(p, i)).getFarbe() == 'b') {
					counter = i;
					break;
				}
				if(board.getPiece(checkHorizontal(p, i)).getType() == '-' )
					counter = i;
					continue;	
			}
			for(int j = 1; j < counter+1; j++) {
				this.validMovesWhite.add(p.getPosition() + "-" + checkHorizontal(p, j));
			}
			
		}
	}
	
	public void validBlackRookUp(Board board, char type) {
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != type)
				continue;
			int counter = 0;
			for(int i = 1; i < 8; i++) {
				if(board.getPiece(checkVertical(p, i)) == null) {
					counter = i-1;
					break;
				}
				if(board.getPiece(checkVertical(p, i)).getFarbe() == 'b') {
					counter = i-1;
					break;
				}
				if(board.getPiece(checkVertical(p, i)).getFarbe() == 'w') {
					counter = i;
					break;
				}
				if(board.getPiece(checkVertical(p, i)).getType() == '-' )
					counter = i;
					continue;	
			}
			for(int j = 1; j < counter+1; j++) {
				this.validMovesBlack.add(p.getPosition() + "-" + checkVertical(p, j));
			}
			
		}
		
	}
	
	public void validBlackRookDown(Board board, char type) {
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != type)
				continue;
			int counter = 0;
			for(int i = 1; i < 8; i++) {
				if(board.getPiece(checkVertical(p, -i)) == null) {
					counter = i-1;
					break;
				}
				if(board.getPiece(checkVertical(p, -i)).getFarbe() == 'b') {
					counter = i-1;
					break;
				}
				if(board.getPiece(checkVertical(p, -i)).getFarbe() == 'w') {
					counter = i;
					break;
				}
				if(board.getPiece(checkVertical(p, -i)).getType() == '-' )
					counter = i;
					continue;	
			}
			for(int j = 1; j < counter+1; j++) {
				this.validMovesBlack.add(p.getPosition() + "-" + checkVertical(p, -j));
			}
			
		}
		
	}
	
	public void validBlackRookLeft(Board board, char type) {
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != type)
				continue;
			int counter = 0;
			int loop = this.columns.indexOf(p.getPosition().charAt(0));		
			for(int i = 1; i < loop+1 ; i++) {
				if(board.getPiece(checkHorizontal(p, -i)).getFarbe() == 'b') {
					counter = i-1;
					break;
				}
				if(board.getPiece(checkHorizontal(p, -i)).getFarbe() == 'w') {
					counter = i;
					break;
				}
				if(board.getPiece(checkHorizontal(p, -i)).getType() == '-' )
					counter = i;
					continue;	
			}
			for(int j = 1; j < counter+1; j++) {
				this.validMovesBlack.add(p.getPosition() + "-" + checkHorizontal(p, -j));
			}
			
		}
	}
	
	public void validBlackRookRight(Board board, char type) {
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != type)
				continue;
			int counter = 0;
			int loop = 7-this.columns.indexOf(p.getPosition().charAt(0));		
			for(int i = 1; i < loop+1 ; i++) {
				if(board.getPiece(checkHorizontal(p, i)).getFarbe() == 'b') {
					counter = i-1;
					break;
				}
				if(board.getPiece(checkHorizontal(p, i)).getFarbe() == 'w') {
					counter = i;
					break;
				}
				if(board.getPiece(checkHorizontal(p, i)).getType() == '-' )
					counter = i;
					continue;	
			}
			for(int j = 1; j < counter+1; j++) {
				this.validMovesBlack.add(p.getPosition() + "-" + checkHorizontal(p, j));
			}
			
		}
		
	}
	
	
	public void validWhiteBishopNW(Board board, char type) {//up-left
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != type)
				continue;
			int counter = 0;
			for(int i = 1; i < 8; i++) {	
				if(bishopHelper1(p, i, 'l', board)) {
					counter = i-1;
					break;
				}
				if(board.getPiece(checkDiagonal(p, i, 'l')).getFarbe() == 'w') {
					counter = i-1;
					break;
				}
				if(board.getPiece(checkDiagonal(p, i, 'l')).getFarbe() == 'b') {
					counter = i;
					break;
				}
				if(board.getPiece(checkDiagonal(p, i, 'l')).getType() == '-') {
					counter = i;
				}
			}
			for(int j = 1; j < counter+1; j++) {
				this.validMovesWhite.add(p.getPosition() + "-" + checkDiagonal(p, j, 'l'));
			}
		}
		
	}
	
	public void validBlackBishopNW(Board board, char type) {//up-left
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != type)
				continue;
			int counter = 0;
			for(int i = 1; i < 8; i++) {	
				if(bishopHelper1(p, i, 'l', board)) {
					counter = i-1;
					break;
				}
				if(board.getPiece(checkDiagonal(p, i, 'l')).getFarbe() == 'b') {
					counter = i-1;
					break;
				}
				if(board.getPiece(checkDiagonal(p, i, 'l')).getFarbe() == 'w') {
					counter = i;
					break;
				}
				if(board.getPiece(checkDiagonal(p, i, 'l')).getType() == '-') {
					counter = i;
				}
			}
			for(int j = 1; j < counter+1; j++) {
				this.validMovesBlack.add(p.getPosition() + "-" + checkDiagonal(p, j, 'l'));
			}
		}
		
	}
	
	public boolean bishopHelper1(Piece p, int i, char c, Board board) {
		if(board.getPiece(checkDiagonal(p, i-1, c)).getPosition().charAt(0) == 'a' || board.getPiece(checkDiagonal(p, i-1, c)).getPosition().charAt(1) == '8')
			return true;
		
		return false;
			
	}
	
	public void validWhiteBishopNE(Board board, char type) {//up-right
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != type)
				continue;
			int counter = 0;
			for(int i = 1; i < 8; i++) {	
				if(bishopHelper2(p, i, 'r', board)) {
					counter = i-1;
					break;
				}
				if(board.getPiece(checkDiagonal(p, i, 'r')).getFarbe() == 'w') {
					counter = i-1;
					break;
				}
				if(board.getPiece(checkDiagonal(p, i, 'r')).getFarbe() == 'b') {
					counter = i;
					break;
				}
				if(board.getPiece(checkDiagonal(p, i, 'r')).getType() == '-') {
					counter = i;
				}
			}
			for(int j = 1; j < counter+1; j++) {
				this.validMovesWhite.add(p.getPosition() + "-" + checkDiagonal(p, j, 'r'));
			}
		}
	}
	
	public void validBlackBishopNE(Board board, char type) {//up-right
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != type)
				continue;
			int counter = 0;
			for(int i = 1; i < 8; i++) {	
				if(bishopHelper2(p, i, 'r', board)) {
					counter = i-1;
					break;
				}
				if(board.getPiece(checkDiagonal(p, i, 'r')).getFarbe() == 'b') {
					counter = i-1;
					break;
				}
				if(board.getPiece(checkDiagonal(p, i, 'r')).getFarbe() == 'w') {
					counter = i;
					break;
				}
				if(board.getPiece(checkDiagonal(p, i, 'r')).getType() == '-') {
					counter = i;
				}
			}
			for(int j = 1; j < counter+1; j++) {
				this.validMovesBlack.add(p.getPosition() + "-" + checkDiagonal(p, j, 'r'));
			}
		}
	}
	
	public boolean bishopHelper2(Piece p, int i, char c, Board board) {
		if(board.getPiece(checkDiagonal(p, i-1, c)).getPosition().charAt(0) == 'h' || board.getPiece(checkDiagonal(p, i-1, c)).getPosition().charAt(1) == '8')
			return true;
		
		return false;
			
	}
		
	public void validWhiteBishopSW(Board board, char type) {//down-left
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != type)
				continue;
			int counter = 0;
			for(int i = 1; i < 8; i++) {	
				if(bishopHelper3(p, i, 'l', board)) {
					counter = i-1;
					break;
				}
				if(board.getPiece(checkDiagonal(p, -i, 'l')).getFarbe() == 'w') {
					counter = i-1;
					break;
				}
				if(board.getPiece(checkDiagonal(p, -i, 'l')).getFarbe() == 'b') {
					counter = i;
					break;
				}
				if(board.getPiece(checkDiagonal(p, -i, 'l')).getType() == '-') {
					counter = i;
				}
			}
			for(int j = 1; j < counter+1; j++) {
				this.validMovesWhite.add(p.getPosition() + "-" + checkDiagonal(p, -j, 'l'));
			}
		}
	}
	
	public void validBlackBishopSW(Board board, char type) {//down-left
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != type)
				continue;
			int counter = 0;
			for(int i = 1; i < 8; i++) {	
				if(bishopHelper3(p, i, 'l', board)) {
					counter = i-1;
					break;
				}
				if(board.getPiece(checkDiagonal(p, -i, 'l')).getFarbe() == 'b') {
					counter = i-1;
					break;
				}
				if(board.getPiece(checkDiagonal(p, -i, 'l')).getFarbe() == 'w') {
					counter = i;
					break;
				}
				if(board.getPiece(checkDiagonal(p, -i, 'l')).getType() == '-') {
					counter = i;
				}
			}
			for(int j = 1; j < counter+1; j++) {
				this.validMovesBlack.add(p.getPosition() + "-" + checkDiagonal(p, -j, 'l'));
			}
		}
	}
	
	public boolean bishopHelper3(Piece p, int i, char c, Board board) {
		if(board.getPiece(checkDiagonal(p, -(i-1), c)).getPosition().charAt(0) == 'a' || board.getPiece(checkDiagonal(p, -(i-1), c)).getPosition().charAt(1) == '1')
			return true;
		
		return false;
			
	}
	
	
	public void validWhiteBishopSE(Board board, char type) {//down-right
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != type)
				continue;
			int counter = 0;
			for(int i = 1; i < 8; i++) {	
				if(bishopHelper4(p, i, 'r', board)) {
					counter = i-1;
					break;
				}
				if(board.getPiece(checkDiagonal(p, -i, 'r')).getFarbe() == 'w') {
					counter = i-1;
					break;
				}
				if(board.getPiece(checkDiagonal(p, -i, 'r')).getFarbe() == 'b') {
					counter = i;
					break;
				}
				if(board.getPiece(checkDiagonal(p, -i, 'r')).getType() == '-') {
					counter = i;
				}
			}
			for(int j = 1; j < counter+1; j++) {
				this.validMovesWhite.add(p.getPosition() + "-" + checkDiagonal(p, -j, 'r'));
			}
		}
	}
	
	public void validBlackBishopSE(Board board, char type) {//down-right
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != type)
				continue;
			int counter = 0;
			for(int i = 1; i < 8; i++) {	
				if(bishopHelper4(p, i, 'r', board)) {
					counter = i-1;
					break;
				}
				if(board.getPiece(checkDiagonal(p, -i, 'r')).getFarbe() == 'b') {
					counter = i-1;
					break;
				}
				if(board.getPiece(checkDiagonal(p, -i, 'r')).getFarbe() == 'w') {
					counter = i;
					break;
				}
				if(board.getPiece(checkDiagonal(p, -i, 'r')).getType() == '-') {
					counter = i;
				}
			}
			for(int j = 1; j < counter+1; j++) {
				this.validMovesBlack.add(p.getPosition() + "-" + checkDiagonal(p, -j, 'r'));
			}
		}
	}
	
	public boolean bishopHelper4(Piece p, int i, char c, Board board) {
		if(board.getPiece(checkDiagonal(p, -(i-1), c)).getPosition().charAt(0) == 'h' || board.getPiece(checkDiagonal(p, -(i-1), c)).getPosition().charAt(1) == '1')
			return true;
		
		return false;
			
	}
	
	public void validWhiteKnight1(Board board) {//2 Left - 1 Up
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != 'N')
				continue;
			if(p.getPosition().charAt(0) == 'a' || p.getPosition().charAt(0) == 'b' || p.getPosition().charAt(1) == '8' )
				continue;
			StringBuilder dest = new StringBuilder();
			dest.append(columns.charAt(columns.indexOf(p.getPosition().charAt(0))-2));//2 columns shift left
			dest.append(Character.getNumericValue(p.getPosition().charAt(1))+1);//1 row up
			
			if(board.getPiece(dest.toString()).getFarbe() == 'b' || board.getPiece(dest.toString()).getType() == '-')
				validMovesWhite.add(p.getPosition() + "-" + dest.toString());
			
		}
			
	}
	
	public void validWhiteKnight2(Board board) {//2 Up - 1 Left
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != 'N')
				continue;
			if(p.getPosition().charAt(0) == 'a' || p.getPosition().charAt(1) == '7' || p.getPosition().charAt(1) == '8' )
				continue;
			StringBuilder dest = new StringBuilder();
			dest.append(columns.charAt(columns.indexOf(p.getPosition().charAt(0))-1));//1 columns shift left
			dest.append(Character.getNumericValue(p.getPosition().charAt(1))+2);//2 row up
			
			if(board.getPiece(dest.toString()).getFarbe() == 'b' || board.getPiece(dest.toString()).getType() == '-')
				validMovesWhite.add(p.getPosition() + "-" + dest.toString());
			
		}
		
	}
	
	public void validWhiteKnight3(Board board) {//2 Up - 1 Right
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != 'N')
				continue;
			if(p.getPosition().charAt(0) == 'h' || p.getPosition().charAt(1) == '7' || p.getPosition().charAt(1) == '8' )
				continue;
			StringBuilder dest = new StringBuilder();
			dest.append(columns.charAt(columns.indexOf(p.getPosition().charAt(0))+1));//1 columns shift right
			dest.append(Character.getNumericValue(p.getPosition().charAt(1))+2);//2 rows up
			
			if(board.getPiece(dest.toString()).getFarbe() == 'b' || board.getPiece(dest.toString()).getType() == '-')
				validMovesWhite.add(p.getPosition() + "-" + dest.toString());
			
		}	
		
	}
	
	public void validWhiteKnight4(Board board) {//1 Up - 2 Right
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != 'N')
				continue;
			if(p.getPosition().charAt(0) == 'g' || p.getPosition().charAt(0) == 'h' || p.getPosition().charAt(1) == '8' )
				continue;
			StringBuilder dest = new StringBuilder();
			dest.append(columns.charAt(columns.indexOf(p.getPosition().charAt(0))+2));//2 columns shift right
			dest.append(Character.getNumericValue(p.getPosition().charAt(1))+1);//1 row up
			
			if(board.getPiece(dest.toString()).getFarbe() == 'b' || board.getPiece(dest.toString()).getType() == '-')
				validMovesWhite.add(p.getPosition() + "-" + dest.toString());
			
		}
			
	}
	
	public void validWhiteKnight5(Board board) {//1 Down - 2 Left
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != 'N')
				continue;
			if(p.getPosition().charAt(0) == 'a' || p.getPosition().charAt(0) == 'b' || p.getPosition().charAt(1) == '1' )
				continue;
			StringBuilder dest = new StringBuilder();
			dest.append(columns.charAt(columns.indexOf(p.getPosition().charAt(0))-2));//2 columns shift left
			dest.append(Character.getNumericValue(p.getPosition().charAt(1))-1);//1 row down
			
			if(board.getPiece(dest.toString()).getFarbe() == 'b' || board.getPiece(dest.toString()).getType() == '-')
				validMovesWhite.add(p.getPosition() + "-" + dest.toString());
			
		}
		
	}
	
	public void validWhiteKnight6(Board board) {//2 Down - 1 Left
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != 'N')
				continue;
			if(p.getPosition().charAt(0) == 'a' || p.getPosition().charAt(1) == '1' || p.getPosition().charAt(1) == '2' )
				continue;
			StringBuilder dest = new StringBuilder();
			dest.append(columns.charAt(columns.indexOf(p.getPosition().charAt(0))-1));//1 columns shift left
			dest.append(Character.getNumericValue(p.getPosition().charAt(1))-2);//2 row down
			
			if(board.getPiece(dest.toString()).getFarbe() == 'b' || board.getPiece(dest.toString()).getType() == '-')
				validMovesWhite.add(p.getPosition() + "-" + dest.toString());
			
		}
			
	}
	
	public void validWhiteKnight7(Board board) {//2 Down - 1 Right
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != 'N')
				continue;
			if(p.getPosition().charAt(0) == 'h' || p.getPosition().charAt(1) == '1' || p.getPosition().charAt(1) == '2' )
				continue;
			StringBuilder dest = new StringBuilder();
			dest.append(columns.charAt(columns.indexOf(p.getPosition().charAt(0))+1));//1 columns shift right
			dest.append(Character.getNumericValue(p.getPosition().charAt(1))-2);// 2 rows down
			
			if(board.getPiece(dest.toString()).getFarbe() == 'b' || board.getPiece(dest.toString()).getType() == '-')
				validMovesWhite.add(p.getPosition() + "-" + dest.toString());
			
		}
				
	}
	
	public void validWhiteKnight8(Board board) {//1 Down - 2 Right
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != 'N')
				continue;
			if(p.getPosition().charAt(0) == 'g' || p.getPosition().charAt(0) == 'h' || p.getPosition().charAt(1) == '1' )
				continue;
			StringBuilder dest = new StringBuilder();
			dest.append(columns.charAt(columns.indexOf(p.getPosition().charAt(0))+2));//2 columns shift right
			dest.append(Character.getNumericValue(p.getPosition().charAt(1))-1);//1 row down
			
			if(board.getPiece(dest.toString()).getFarbe() == 'b' || board.getPiece(dest.toString()).getType() == '-')
				validMovesWhite.add(p.getPosition() + "-" + dest.toString());
			
		}
			
	}
	
	public void validBlackKnight1(Board board) {//2 Left - 1 Up
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != 'n')
				continue;
			if(p.getPosition().charAt(0) == 'a' || p.getPosition().charAt(0) == 'b' || p.getPosition().charAt(1) == '8' )
				continue;
			StringBuilder dest = new StringBuilder();
			dest.append(columns.charAt(columns.indexOf(p.getPosition().charAt(0))-2));//2 columns shift left
			dest.append(Character.getNumericValue(p.getPosition().charAt(1))+1);//1 row up
			
			if(board.getPiece(dest.toString()).getFarbe() == 'w' || board.getPiece(dest.toString()).getType() == '-')
				validMovesBlack.add(p.getPosition() + "-" + dest.toString());
			
		}
	}
	
	public void validBlackKnight2(Board board) {//2 Up - 1 Left
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != 'n')
				continue;
			if(p.getPosition().charAt(0) == 'a' || p.getPosition().charAt(1) == '7' || p.getPosition().charAt(1) == '8' )
				continue;
			StringBuilder dest = new StringBuilder();
			dest.append(columns.charAt(columns.indexOf(p.getPosition().charAt(0))-1));//1 columns shift left
			dest.append(Character.getNumericValue(p.getPosition().charAt(1))+2);//2 row up
			
			if(board.getPiece(dest.toString()).getFarbe() == 'w' || board.getPiece(dest.toString()).getType() == '-')
				validMovesBlack.add(p.getPosition() + "-" + dest.toString());
			
		}
	}
	
	public void validBlackKnight3(Board board) {//2 Up - 1 Right
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != 'n')
				continue;
			if(p.getPosition().charAt(0) == 'h' || p.getPosition().charAt(1) == '7' || p.getPosition().charAt(1) == '8' )
				continue;
			StringBuilder dest = new StringBuilder();
			dest.append(columns.charAt(columns.indexOf(p.getPosition().charAt(0))+1));//1 columns shift right
			dest.append(Character.getNumericValue(p.getPosition().charAt(1))+2);//2 rows up
			
			if(board.getPiece(dest.toString()).getFarbe() == 'w' || board.getPiece(dest.toString()).getType() == '-')
				validMovesBlack.add(p.getPosition() + "-" + dest.toString());
			
		}	
		
	}
	
	public void validBlackKnight4(Board board) {//1 Up - 2 Right
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != 'n')
				continue;
			if(p.getPosition().charAt(0) == 'g' || p.getPosition().charAt(0) == 'h' || p.getPosition().charAt(1) == '8' )
				continue;
			StringBuilder dest = new StringBuilder();
			dest.append(columns.charAt(columns.indexOf(p.getPosition().charAt(0))+2));//2 columns shift right
			dest.append(Character.getNumericValue(p.getPosition().charAt(1))+1);//1 row up
			
			if(board.getPiece(dest.toString()).getFarbe() == 'w' || board.getPiece(dest.toString()).getType() == '-')
				validMovesBlack.add(p.getPosition() + "-" + dest.toString());
			
		}
			
	}
	
	public void validBlackKnight5(Board board) {//1 Down - 2 Left
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != 'n')
				continue;
			if(p.getPosition().charAt(0) == 'a' || p.getPosition().charAt(0) == 'b' || p.getPosition().charAt(1) == '1' )
				continue;
			StringBuilder dest = new StringBuilder();
			dest.append(columns.charAt(columns.indexOf(p.getPosition().charAt(0))-2));//2 columns shift left
			dest.append(Character.getNumericValue(p.getPosition().charAt(1))-1);//1 row down
			
			if(board.getPiece(dest.toString()).getFarbe() == 'w' || board.getPiece(dest.toString()).getType() == '-')
				validMovesBlack.add(p.getPosition() + "-" + dest.toString());
			
		}
		
	}
	
	public void validBlackKnight6(Board board) {//2 Down - 1 Left
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != 'n')
				continue;
			if(p.getPosition().charAt(0) == 'a' || p.getPosition().charAt(1) == '1' || p.getPosition().charAt(1) == '2' )
				continue;
			StringBuilder dest = new StringBuilder();
			dest.append(columns.charAt(columns.indexOf(p.getPosition().charAt(0))-1));//1 columns shift left
			dest.append(Character.getNumericValue(p.getPosition().charAt(1))-2);//2 row down
			
			if(board.getPiece(dest.toString()).getFarbe() == 'w' || board.getPiece(dest.toString()).getType() == '-')
				validMovesBlack.add(p.getPosition() + "-" + dest.toString());
			
		}
			
	}
	
	public void validBlackKnight7(Board board) {//2 Down - 1 Right
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != 'n')
				continue;
			if(p.getPosition().charAt(0) == 'h' || p.getPosition().charAt(1) == '1' || p.getPosition().charAt(1) == '2' )
				continue;
			StringBuilder dest = new StringBuilder();
			dest.append(columns.charAt(columns.indexOf(p.getPosition().charAt(0))+1));//1 columns shift right
			dest.append(Character.getNumericValue(p.getPosition().charAt(1))-2);// 2 rows down
			
			if(board.getPiece(dest.toString()).getFarbe() == 'w' || board.getPiece(dest.toString()).getType() == '-')
				validMovesBlack.add(p.getPosition() + "-" + dest.toString());
			
		}
				
	}
	
	public void validBlackKnight8(Board board) {//1 Down - 2 Right
		for(Piece p: board.pieces) {
			if(p.inReserve || p.getType() != 'n')
				continue;
			if(p.getPosition().charAt(0) == 'g' || p.getPosition().charAt(0) == 'h' || p.getPosition().charAt(1) == '1' )
				continue;
			StringBuilder dest = new StringBuilder();
			dest.append(columns.charAt(columns.indexOf(p.getPosition().charAt(0))+2));//2 columns shift right
			dest.append(Character.getNumericValue(p.getPosition().charAt(1))-1);//1 row down
			
			if(board.getPiece(dest.toString()).getFarbe() == 'w' || board.getPiece(dest.toString()).getType() == '-')
				validMovesBlack.add(p.getPosition() + "-" + dest.toString());
			
		}
			
	}
	
	public void validWhiteQueenD(Board board) {//White Queen all Diagonals
		validWhiteBishopNW(board, 'Q');
		validWhiteBishopNE(board, 'Q');
		validWhiteBishopSW(board, 'Q');
		validWhiteBishopSE(board, 'Q');
		
	}
	
	public void validWhiteQueenVH(Board board) {//White Queen Verticals and Horizontals
		validWhiteRookUp(board, 'Q');
		validWhiteRookDown(board, 'Q');
		validWhiteRookLeft(board, 'Q');
		validWhiteRookRight(board, 'Q');
	}
	
	public void validBlackQueenD(Board board) {//Black Queen all Diagonals
		validBlackBishopNW(board, 'q');
		validBlackBishopNE(board, 'q');
		validBlackBishopSW(board, 'q');
		validBlackBishopSE(board, 'q');
	}
	
	public void validBlackQueenVH(Board board) {//Black Queen Verticals and Horizontals
		validBlackRookUp(board, 'q');
		validBlackRookDown(board, 'q');
		validBlackRookLeft(board, 'q');
		validBlackRookRight(board, 'q');
	}
	
	public void validWhiteKingH(Board board) {//Valid White king moves Horizontal
		for(Piece p : board.pieces) {
			if(p.inReserve || p.getType() != 'K')
				continue;
			if(p.getPosition().charAt(0) != 'a') {//check if 1 left possible  b3-a3
				if(board.getPiece(checkHorizontal(p, -1)).getFarbe() == 'b' || board.getPiece(checkHorizontal(p, -1)).getType() == '-')
					validMovesWhite.add(p.getPosition() + "-" + checkHorizontal(p, -1));
			}
			if(p.getPosition().charAt(0) != 'h') {//check if 1 right possible
				if(board.getPiece(checkHorizontal(p, 1)).getFarbe() == 'b' || board.getPiece(checkHorizontal(p, 1)).getType() == '-')
					validMovesWhite.add(p.getPosition() + "-" + checkHorizontal(p, 1));
			}
			
		}
		
		
	}
	
	public void validWhiteKingV(Board board) {//Valid White king moves vertical
		for(Piece p : board.pieces) {
			if(p.inReserve || p.getType() != 'K')
				continue;
			if(p.getPosition().charAt(1) != '8') {//check if 1 up possible  
				if(board.getPiece(checkVertical(p, 1)).getFarbe() == 'b' || board.getPiece(checkVertical(p, 1)).getType() == '-')
					validMovesWhite.add(p.getPosition() + "-" + checkVertical(p, 1));
			}
			if(p.getPosition().charAt(1) != '1') {//check if 1 down possible
				if(board.getPiece(checkVertical(p, -1)).getFarbe() == 'b' || board.getPiece(checkVertical(p, -1)).getType() == '-')
					validMovesWhite.add(p.getPosition() + "-" + checkVertical(p, -1));
			}
			
		}
	}
	
	public void validWhiteKingD1(Board board) {//Valid White king moves diagonal up left
		for(Piece p : board.pieces) {
			if(p.inReserve || p.getType() != 'K')
				continue;
			if(p.getPosition().charAt(0) != 'a' && p.getPosition().charAt(1) != '8')//check if possible
				if(board.getPiece(checkDiagonal(p, 1, 'l')).getFarbe() == 'b' || board.getPiece(checkDiagonal(p, 1, 'l')).getType() == '-')
					validMovesWhite.add(p.getPosition() + "-" + checkDiagonal(p, 1, 'l'));
			
		}
	}
	
	public void validWhiteKingD2(Board board) {//Valid White king moves diagonal up right
		for(Piece p : board.pieces) {
			if(p.inReserve || p.getType() != 'K')
				continue;
			if(p.getPosition().charAt(0) != 'h' && p.getPosition().charAt(1) != '8')//check if possible
				if(board.getPiece(checkDiagonal(p, 1, 'r')).getFarbe() == 'b' || board.getPiece(checkDiagonal(p, 1, 'r')).getType() == '-')
					validMovesWhite.add(p.getPosition() + "-" + checkDiagonal(p, 1, 'r'));
			
		}
	}
	
	public void validWhiteKingD3(Board board) {//Valid White king moves diagonal down left
		for(Piece p : board.pieces) {
			if(p.inReserve || p.getType() != 'K')
				continue;
			if(p.getPosition().charAt(0) != 'a' && p.getPosition().charAt(1) != '1')//check if possible
				if(board.getPiece(checkDiagonal(p, -1, 'l')).getFarbe() == 'b' || board.getPiece(checkDiagonal(p, -1, 'l')).getType() == '-')
					validMovesWhite.add(p.getPosition() + "-" + checkDiagonal(p, -1, 'l'));
			
		}
	}
	
	public void validWhiteKingD4(Board board) {//Valid White king moves diagonal down right
		for(Piece p : board.pieces) {
			if(p.inReserve || p.getType() != 'K')
				continue;
			if(p.getPosition().charAt(0) != 'h' && p.getPosition().charAt(1) != '1')//check if possible
				if(board.getPiece(checkDiagonal(p, -1, 'r')).getFarbe() == 'b' || board.getPiece(checkDiagonal(p, -1, 'r')).getType() == '-')
					validMovesWhite.add(p.getPosition() + "-" + checkDiagonal(p, -1, 'r'));
			
		}
	}
	
	public void validBlackKingH(Board board) {//Valid Black king moves Horizontal
		for(Piece p : board.pieces) {
			if(p.inReserve || p.getType() != 'k')
				continue;
			if(p.getPosition().charAt(0) != 'a') {//check if 1 left possible 
				if(board.getPiece(checkHorizontal(p, -1)).getFarbe() == 'w' || board.getPiece(checkHorizontal(p, -1)).getType() == '-')
					validMovesBlack.add(p.getPosition() + "-" + checkHorizontal(p, -1));
			}
			if(p.getPosition().charAt(0) != 'h') {//check if 1 right possible
				if(board.getPiece(checkHorizontal(p, 1)).getFarbe() == 'w' || board.getPiece(checkHorizontal(p, 1)).getType() == '-')
					validMovesBlack.add(p.getPosition() + "-" + checkHorizontal(p, 1));
			}
			
		}
	}
	
	public void validBlackKingV(Board board) {// Valid Black king moves vertical
		for(Piece p : board.pieces) {
			if(p.inReserve || p.getType() != 'k')
				continue;
			if(p.getPosition().charAt(1) != '8') {//check if 1 up possible  
				if(board.getPiece(checkVertical(p, 1)).getFarbe() == 'w' || board.getPiece(checkVertical(p, 1)).getType() == '-')
					validMovesBlack.add(p.getPosition() + "-" + checkVertical(p, 1));
			}
			if(p.getPosition().charAt(1) != '1') {//check if 1 down possible
				if(board.getPiece(checkVertical(p, -1)).getFarbe() == 'w' || board.getPiece(checkVertical(p, -1)).getType() == '-')
					validMovesBlack.add(p.getPosition() + "-" + checkVertical(p, -1));
			}
			
		}
	}
	
	public void validBlackKingD1(Board board) {//Valid Black king moves diagonal up left
		for(Piece p : board.pieces) {
			if(p.inReserve || p.getType() != 'k')
				continue;
			if(p.getPosition().charAt(0) != 'a' && p.getPosition().charAt(1) != '8')//check if possible
				if(board.getPiece(checkDiagonal(p, 1, 'l')).getFarbe() == 'w' || board.getPiece(checkDiagonal(p, 1, 'l')).getType() == '-')
					validMovesBlack.add(p.getPosition() + "-" + checkDiagonal(p, 1, 'l'));
			
		}
	}
	
	public void validBlackKingD2(Board board) {//Valid Black king moves diagonal up right
		for(Piece p : board.pieces) {
			if(p.inReserve || p.getType() != 'k')
				continue;
			if(p.getPosition().charAt(0) != 'h' && p.getPosition().charAt(1) != '8')//check if possible
				if(board.getPiece(checkDiagonal(p, 1, 'r')).getFarbe() == 'w' || board.getPiece(checkDiagonal(p, 1, 'r')).getType() == '-')
					validMovesBlack.add(p.getPosition() + "-" + checkDiagonal(p, 1, 'r'));
			
		}
	}
	
	public void validBlackKingD3(Board board) {//Valid Black king moves diagonal down left
		for(Piece p : board.pieces) {
			if(p.inReserve || p.getType() != 'k')
				continue;
			if(p.getPosition().charAt(0) != 'a' && p.getPosition().charAt(1) != '1')//check if possible
				if(board.getPiece(checkDiagonal(p, -1, 'l')).getFarbe() == 'w' || board.getPiece(checkDiagonal(p, -1, 'l')).getType() == '-')
					validMovesBlack.add(p.getPosition() + "-" + checkDiagonal(p, -1, 'l'));
			
		}
	}
	
	public void validBlackKingD4(Board board) {//Valid Black king moves diagonal down right
		for(Piece p : board.pieces) {
			if(p.inReserve || p.getType() != 'k')
				continue;
			if(p.getPosition().charAt(0) != 'h' && p.getPosition().charAt(1) != '1')//check if possible
				if(board.getPiece(checkDiagonal(p, -1, 'r')).getFarbe() == 'w' || board.getPiece(checkDiagonal(p, -1, 'r')).getType() == '-')
					validMovesBlack.add(p.getPosition() + "-" + checkDiagonal(p, -1, 'r'));
			
		}
	}

	
	public void movePieceOnBoard1(String moveString, String to, String from, Board board) {//a2-a4, excl. promotions
		int hedefRow = Character.getNumericValue(moveString.charAt(moveString.length()-1));//4	
		int row = Character.getNumericValue(from.charAt(1));
		for(Piece p : board.pieces) {
			if(p.equals(this.parcaYiyen)) 
				p.setPosition(to);
			
		} 
		if(this.parcaYenilen.getType() != '-') {
			
			for(Piece p : board.pieces) {
				if(p.equals(this.parcaYenilen)) {
					p.setInReserve(true);
					p.setFarbe(Character.isUpperCase(this.parcaYenilen.getType()) ? 'b' : 'w');
					p.setPosition("r");
					p.setType(p.getFarbe() == 'w' ? Character.toUpperCase(p.getType()) : Character.toLowerCase(p.getType()));
				}
			} 
			
			
			board.Tahta[8-hedefRow][columns.indexOf(to.charAt(0))] = this.parcaYiyen.getType();
					
		}else if(this.parcaYenilen.getType() == '-') {
			board.Tahta[8-hedefRow][columns.indexOf(to.charAt(0))] = this.parcaYiyen.getType();
			for(Piece p : board.pieces) {
				if(p.equals(this.parcaYiyen)) 
					p.setPosition(to);
				
			} 
		}
		board.Tahta[8-row][columns.indexOf(moveString.charAt(0))] = '-';
		
	}
	
	public void movePieceOnBoard2(String moveString, String to , String from, Board board) {//only when promotion
		int hedefRow = Character.getNumericValue(moveString.charAt(moveString.length()-1));//4	
		int row = Character.getNumericValue(from.charAt(1));
		if(board.getPiece(from).getFarbe() == 'w') {
			board.Tahta[8-hedefRow][columns.indexOf(to.charAt(0))] = 'Q';
			for(Piece p : board.pieces) {
				if(p.equals(this.parcaYiyen)) {
					p.setPosition(to);
					p.setType('Q');
				}
			} 
		}else if(board.getPiece(from).getFarbe() == 'b') {
			board.Tahta[8-hedefRow][columns.indexOf(to.charAt(0))] = 'q';
			for(Piece p : board.pieces) {
				if(p.equals(this.parcaYiyen)) {
					p.setPosition(to);
					p.setType('q');
				}
			} 
		}
		
		
		if(this.parcaYenilen.getType() != '-') {
			helppls(board);
		}
		
		board.Tahta[8-row][columns.indexOf(moveString.charAt(0))] = '-';
	}
	public void helppls(Board board) {
		for(Piece p : board.pieces) {
			if(p.equals(this.parcaYenilen)) {
				p.setPosition("r");
				p.setInReserve(true);
				p.setFarbe(Character.isUpperCase(this.parcaYenilen.getType()) ? 'b' : 'w');
				p.setType((p.farbe == 'w') ? Character.toUpperCase(p.getType()) : Character.toLowerCase(p.getType()));
			}
		}
		
	}
	
	public void dropPiece(String moveString, Board board) {
		int hedefRow = Character.getNumericValue(moveString.charAt(moveString.length()-1));//4	
		StringBuilder to = new StringBuilder(moveString.substring(moveString.length()-2, moveString.length()));
		
		for(Piece p: board.pieces) {
			if(p.inReserve && p.getType() == moveString.charAt(0)) {
				p.setInReserve(false);
				p.setPosition(to.toString());
				break;
			}
		}
		//this.Board.updatePieces(this.Board.pieces.get(this.Board.indexFinder(moveString.charAt(0), true, Character.isUpperCase(moveString.charAt(0)) ? 'w' : 'b', "r")), "out", to.toString());           
		board.Tahta[8-hedefRow][columns.indexOf(moveString.charAt(moveString.length()-2))] = moveString.charAt(0);
		
		
			
	}

	public int checkPromotion(Piece p, String moveString) {
		if(p.getType() == 'P' && p.getPosition().charAt(1) == '7' && moveString.charAt(moveString.length()-1) == '8')
			return 1;//promotionW = true;
		
		if(p.getType() == 'p' && p.getPosition().charAt(1) == '2' && moveString.charAt(moveString.length()-1) == '1')
			return -1;//promotionB = true;
		
		return 0;
	}
	

	public boolean wKingCheckVerticals(Piece p, Board board) {
		if(wkingCheckVertical1(p, board))
			return true;
		if(wkingCheckVertical2(p, board))
			return true;
		
		return false;
	}
	
	
	public boolean wKingCheckHorizontals(Piece p, Board board) {
		if(wkingCheckHorizontal1(p, board))
			return true;
		if(wkingCheckHorizontal2(p, board))
			return true;
		
		return false;
	}
	public boolean wKingCheckDiagonals(Piece p, Board board) {
		if(wkingCheckDiagonal1(p, board))
			return true;
		if(wkingCheckDiagonal2(p, board))
			return true;
		if(wkingCheckDiagonal3(p, board))
			return true;
		if(wkingCheckDiagonal4(p, board))
			return true;
		if(wkingCheckDiagonal5(p, board))
			return true;
		return false;
	}
	
	public boolean wKingCheckKnights(Piece p, Board board) {
		if(wkingCheckKnight1(p, board))
			return true;
		if(wkingCheckKnight2(p, board))
			return true;
		if(wkingCheckKnight3(p, board))
			return true;
		if(wkingCheckKnight4(p, board))
			return true;
		if(wkingCheckKnight5(p, board))
			return true;
		if(wkingCheckKnight6(p, board))
			return true;
		if(wkingCheckKnight7(p, board))
			return true;
		if(wkingCheckKnight8(p, board))
			return true;
		return false;
	}
	
	
	
	public boolean bkingCheckDiagonal1(Piece p, Board board) {
		for(int i = 1; i < 8; i++) {	
			if(bishopHelper1(p, i, 'l', board)) {
				break;
			}
			if(board.getPiece(checkDiagonal(p, i, 'l')).getType() == '-')
				continue;
			if(i == 1 && board.getPiece(checkDiagonal(p, i, 'l')).getType() == 'K')
				return true;
			if(board.getPiece(checkDiagonal(p, i, 'l')).getFarbe() == 'b')
				return false;
			if(board.getPiece(checkDiagonal(p, i, 'l')).getType() == 'B' || board.getPiece(checkDiagonal(p, i, 'l')).getType() == 'Q' ) 
				return true;
			//if(board.getPiece(checkDiagonal(p, i, 'l')).getType() != 'B' || board.getPiece(checkDiagonal(p, i, 'l')).getType() != 'Q' ) 
				//return false;
			
				
		}

		return false;
	}
	
	public boolean bkingCheckDiagonal2(Piece p, Board board) {
		for(int i = 1; i < 8; i++) {	
			if(bishopHelper2(p, i, 'r', board)) {
				break;
			}
			if(board.getPiece(checkDiagonal(p, i, 'r')).getType() == '-') 
				continue;
			if(i == 1 && board.getPiece(checkDiagonal(p, i, 'r')).getType() == 'K')
				return true;
			if(board.getPiece(checkDiagonal(p, i, 'r')).getFarbe() == 'b')
				return false;
			if(board.getPiece(checkDiagonal(p, i, 'r')).getType() == 'B' || board.getPiece(checkDiagonal(p, i, 'r')).getType() == 'Q' ) 
				return true;
			//if(board.getPiece(checkDiagonal(p, i, 'r')).getType() != 'B' || board.getPiece(checkDiagonal(p, i, 'r')).getType() != 'Q' ) 
				//return false;
				
		
		}

		return false;
	}
	
	public boolean bkingCheckDiagonal3(Piece p, Board board) {
		for(int i = 1; i < 8; i++) {	
			if(bishopHelper3(p, i, 'l', board)) {
				break;
			}
			if(board.getPiece(checkDiagonal(p, -i, 'l')).getType() == '-')
				continue;
			if(i == 1 && board.getPiece(checkDiagonal(p, -i, 'l')).getType() == 'K')
				return true;
			if(board.getPiece(checkDiagonal(p, -i, 'l')).getFarbe() == 'b')
				return false;
			if(board.getPiece(checkDiagonal(p, -i, 'l')).getType() == 'B' || board.getPiece(checkDiagonal(p, -i, 'l')).getType() == 'Q' ) 
				return true;
			//if(board.getPiece(checkDiagonal(p, -i, 'l')).getType() != 'B' || board.getPiece(checkDiagonal(p, -i, 'l')).getType() != 'Q' ) 
				//return false;
			
			
		}

		return false;
	}
	
	public boolean bkingCheckDiagonal4(Piece p, Board board) {
		for(int i = 1; i < 8; i++) {	
			if(bishopHelper4(p, i, 'r', board)) {
				break;
			}
			if(board.getPiece(checkDiagonal(p, -i, 'r')).getType() == '-')
				continue;
			if(i == 1 && board.getPiece(checkDiagonal(p, -i, 'r')).getType() == 'K')
				return true;
			if(board.getPiece(checkDiagonal(p, -i, 'r')).getFarbe() == 'b')
				return false;
			if(board.getPiece(checkDiagonal(p, -i, 'r')).getType() == 'B' || board.getPiece(checkDiagonal(p, -i, 'r')).getType() == 'Q' ) 
				return true;
			//if(board.getPiece(checkDiagonal(p, -i, 'r')).getType() != 'B' || board.getPiece(checkDiagonal(p, -i, 'r')).getType() != 'Q' ) 
				//return false;
					
		}

		return false;
	}
	
	public boolean bkingCheckDiagonal5(Piece p, Board board) {
		if(p.getPosition().charAt(0) != 'a' && board.getPiece(checkDiagonal(p, -1, 'l')) != null) {
			if(board.getPiece(checkDiagonal(p, -1, 'l')).getType() == 'P') {
				return true;
			}
		}
		
		if(p.getPosition().charAt(0) != 'h' && board.getPiece(checkDiagonal(p, -1, 'r')) != null) {
			if(board.getPiece(checkDiagonal(p, -1, 'r')).getType() == 'P') 
				return true;
					
			}
			
		return false;
	}

	
	
	public boolean bKingCheckDiagonals(Piece p, Board board) {
		if(bkingCheckDiagonal1(p, board))
			return true;
		if(bkingCheckDiagonal2(p, board))
			return true;
		if(bkingCheckDiagonal3(p, board))
			return true;
		if(bkingCheckDiagonal4(p, board))
			return true;
		if(bkingCheckDiagonal5(p, board))
			return true;
		return false;
	}
	
	
	public boolean bkingCheckVertical1(Piece p, Board board) {
		int row = Character.getNumericValue(p.getPosition().charAt(1));
		for(int i = 1; i < 9-row; i++) {
			if(board.getPiece(checkVertical(p, i)).getType() == '-')
				continue;
			if(i == 1 && board.getPiece(checkVertical(p, i)).getType() == 'K')
				return true;
			if(board.getPiece(checkVertical(p, i)).getFarbe() == 'b')
				return false;
			if(board.getPiece(checkVertical(p, i)).getType() == 'R' || board.getPiece(checkVertical(p, i)).getType() == 'Q')
				return true;
			//if(board.getPiece(checkVertical(p, i)).getType() != 'R' || board.getPiece(checkVertical(p, i)).getType() != 'R')
				return false;
			
		}
		
		return false;
	}
	
	public boolean bkingCheckVertical2(Piece p, Board board) {
		int row = Character.getNumericValue(p.getPosition().charAt(1));
		for(int i = 1; i < row; i++) {
			if(board.getPiece(checkVertical(p, -i)).getType() == '-')
				continue;
			if(i == 1 && board.getPiece(checkVertical(p, -i)).getType() == 'K')
				return true;
			if(board.getPiece(checkVertical(p, -i)).getFarbe() == 'b')
				return false;
			if(board.getPiece(checkVertical(p, -i)).getType() == 'R' || board.getPiece(checkVertical(p, -i)).getType() == 'Q')
				return true;
			//if(board.getPiece(checkVertical(p, -i)).getType() != 'R' || board.getPiece(checkVertical(p, -i)).getType() != 'Q')
				return false;
		}
		return false;
	}
	
	
	public boolean bKingCheckVerticals(Piece p, Board board) {
		if(bkingCheckVertical1(p, board))
			return true;
		if(bkingCheckVertical2(p, board))
			return true;
		
		return false;
	}
	
	public boolean bkingCheckHorizontal1(Piece p, Board board) {
		int row = Character.getNumericValue(p.getPosition().charAt(1));
		for(int i = 1; i < columns.indexOf(p.getPosition().charAt(0))+1; i++) {
			if(board.getPiece(checkHorizontal(p, -i)).getType() == '-')
				continue;
			if(i == 1 && board.getPiece(checkHorizontal(p, -i)).getType() == 'K')
				return true;
			if(board.getPiece(checkHorizontal(p, -i)).getFarbe() == 'b')
				return false;
			if(board.getPiece(checkHorizontal(p, -i)).getType() == 'R' || board.getPiece(checkHorizontal(p, -i)).getType() == 'Q')
				return true;
			//if(board.getPiece(checkHorizontal(p, -i)).getType() != 'R' || board.getPiece(checkHorizontal(p, -i)).getType() != 'Q')
				return false;
			
		}	
		
		return false;
	}
	
	public boolean bkingCheckHorizontal2(Piece p, Board board) {
		int row = Character.getNumericValue(p.getPosition().charAt(1));
		for(int i = 1; i < 8-(columns.indexOf(p.getPosition().charAt(0))); i++) {//check right horizontal for rooks or queens
			if(board.getPiece(checkHorizontal(p, i)).getType() == '-')
				continue;
			if(i == 1 && board.getPiece(checkHorizontal(p, i)).getType() == 'K')
				return true;
			if(board.getPiece(checkHorizontal(p, i)).getFarbe() == 'b')
				return false;
			if(board.getPiece(checkHorizontal(p, i)).getType() == 'R' || board.getPiece(checkHorizontal(p, i)).getType() == 'Q')
				return true;
			//if(board.getPiece(checkHorizontal(p, i)).getType() != 'R' || board.getPiece(checkHorizontal(p, i)).getType() != 'Q')
				return false;
			
		}
		return false;
	}
	
	public boolean bKingCheckHorizontals(Piece p, Board board) {
		if(bkingCheckHorizontal1(p, board))
			return true;
		if(bkingCheckHorizontal2(p, board))
			return true;
		
		return false;
	}
	
	
	public boolean bkingCheckKnight1(Piece p, Board board) {
		if(p.getPosition().charAt(0) == 'a' || p.getPosition().charAt(0) == 'b' || p.getPosition().charAt(1) == '8' )
			return false;
		StringBuilder dest = new StringBuilder();
		dest.append(columns.charAt(columns.indexOf(p.getPosition().charAt(0))-2));//2 columns shift left
		dest.append(Character.getNumericValue(p.getPosition().charAt(1))+1);//1 row up
			
		if(board.getPiece(dest.toString()).getType() == 'N')
			return true;
			
		return false;
	}
	
	public boolean bkingCheckKnight2(Piece p, Board board) {
		if(p.getPosition().charAt(0) == 'a' || p.getPosition().charAt(1) == '7' || p.getPosition().charAt(1) == '8')
			return false;
		StringBuilder dest = new StringBuilder();
		dest.append(columns.charAt(columns.indexOf(p.getPosition().charAt(0))-1));//1 columns shift left
		dest.append(Character.getNumericValue(p.getPosition().charAt(1))+2);//2 row up
			
		if(board.getPiece(dest.toString()).getType() == 'N')
			return true;
			
		return false;
	}
	
	public boolean bkingCheckKnight3(Piece p, Board board) {
		if(p.getPosition().charAt(0) == 'h' || p.getPosition().charAt(1) == '7' || p.getPosition().charAt(1) == '8')
			return false;
		StringBuilder dest = new StringBuilder();
		dest.append(columns.charAt(columns.indexOf(p.getPosition().charAt(0))+1));//1 columns shift right
		dest.append(Character.getNumericValue(p.getPosition().charAt(1))+2);//2 rows up
			
		if(board.getPiece(dest.toString()).getType() == 'N')
			return true;
			
		return false;
	}
	
	public boolean bkingCheckKnight4(Piece p, Board board) {
		if(p.getPosition().charAt(0) == 'g' || p.getPosition().charAt(0) == 'h' || p.getPosition().charAt(1) == '8')
			return false;
		StringBuilder dest = new StringBuilder();
		dest.append(columns.charAt(columns.indexOf(p.getPosition().charAt(0))+2));//2 columns shift right
		dest.append(Character.getNumericValue(p.getPosition().charAt(1))+1);//1 row up
			
		if(board.getPiece(dest.toString()).getType() == 'N')
			return true;
			
		return false;
	}
	
	public boolean bkingCheckKnight5(Piece p, Board board) {
		if(p.getPosition().charAt(0) == 'a' || p.getPosition().charAt(0) == 'b' || p.getPosition().charAt(1) == '1')
			return false;
		StringBuilder dest = new StringBuilder();
		dest.append(columns.charAt(columns.indexOf(p.getPosition().charAt(0))-2));//2 columns shift left
		dest.append(Character.getNumericValue(p.getPosition().charAt(1))-1);//1 row down
			
		if(board.getPiece(dest.toString()).getType() == 'N')
			return true;
			
		return false;
	}
	
	public boolean bkingCheckKnight6(Piece p, Board board) {
		if(p.getPosition().charAt(0) == 'a' || p.getPosition().charAt(1) == '1' || p.getPosition().charAt(1) == '2')
			return false;
		StringBuilder dest = new StringBuilder();
		dest.append(columns.charAt(columns.indexOf(p.getPosition().charAt(0))-1));//1 columns shift left
		dest.append(Character.getNumericValue(p.getPosition().charAt(1))-2);//2 row down
			
		if(board.getPiece(dest.toString()).getType() == 'N')
			return true;
			
		return false;
	}
	
	public boolean bkingCheckKnight7(Piece p, Board board) {
		if(p.getPosition().charAt(0) == 'h' || p.getPosition().charAt(1) == '1' || p.getPosition().charAt(1) == '2')
			return false;
		StringBuilder dest = new StringBuilder();
		dest.append(columns.charAt(columns.indexOf(p.getPosition().charAt(0))+1));//1 columns shift right
		dest.append(Character.getNumericValue(p.getPosition().charAt(1))-2);// 2 rows down
			
		if(board.getPiece(dest.toString()).getType() == 'N')
			return true;
			
		return false;
	}
	
	public boolean bkingCheckKnight8(Piece p, Board board) {
		if(p.getPosition().charAt(0) == 'g' || p.getPosition().charAt(0) == 'h' || p.getPosition().charAt(1) == '1')
			return false;
		StringBuilder dest = new StringBuilder();
		dest.append(columns.charAt(columns.indexOf(p.getPosition().charAt(0))+2));//2 columns shift right
		dest.append(Character.getNumericValue(p.getPosition().charAt(1))-1);//1 row down
			
		if(board.getPiece(dest.toString()).getType() == 'N')
			return true;
			
		return false;
	}
	
	public boolean bKingCheckKnights(Piece p, Board board) {
		if(bkingCheckKnight1(p, board))
			return true;
		if(bkingCheckKnight2(p, board))
			return true;
		if(bkingCheckKnight3(p, board))
			return true;
		if(bkingCheckKnight4(p, board))
			return true;
		if(bkingCheckKnight5(p, board))
			return true;
		if(bkingCheckKnight6(p, board))
			return true;
		if(bkingCheckKnight7(p, board))
			return true;
		if(bkingCheckKnight8(p, board))
			return true;
		return false;
	}
	
	public boolean wkingCheckVertical1(Piece p, Board board) {//check plus vertical for rooks or queens
		int row = Character.getNumericValue(p.getPosition().charAt(1));
		for(int i = 1; i < 9-row; i++) {
			if(board.getPiece(checkVertical(p, i)).getType() == '-')
				continue;
			if(i == 1 && board.getPiece(checkVertical(p, i)).getType() == 'k')
				return true;
			if(board.getPiece(checkVertical(p, i)).getFarbe() == 'w')
				return false;
			if(board.getPiece(checkVertical(p, i)).getType() == 'r' || board.getPiece(checkVertical(p, i)).getType() == 'q')
				return true;
			//if(board.getPiece(checkVertical(p, i)).getType() != 'r' || board.getPiece(checkVertical(p, i)).getType() != 'q')
				return false;
			
		}
		
		return false;
	}
	public boolean wkingCheckVertical2(Piece p, Board board) {//check minus horizontal for rooks or queens
		int row = Character.getNumericValue(p.getPosition().charAt(1));
		for(int i = 1; i < row; i++) {
			if(board.getPiece(checkVertical(p, -i)).getType() == '-')
				continue;
			if(i == 1 && board.getPiece(checkVertical(p, -i)).getType() == 'k')
				return true;
			if(board.getPiece(checkVertical(p, -i)).getFarbe() == 'w')
				return false;
			if(board.getPiece(checkVertical(p, -i)).getType() == 'r' || board.getPiece(checkVertical(p, -i)).getType() == 'q')
				return true;
			//if(board.getPiece(checkVertical(p, -i)).getType() != 'r' || board.getPiece(checkVertical(p, -i)).getType() != 'q')
				return false;
		}
		return false;
	}
	
	public boolean wkingCheckHorizontal1(Piece p, Board board){//check left horizontal for rooks or queens
		int row = Character.getNumericValue(p.getPosition().charAt(1));
		for(int i = 1; i < columns.indexOf(p.getPosition().charAt(0))+1; i++) {
			if(board.getPiece(checkHorizontal(p, -i)).getType() == '-')
				continue;
			if(i == 1 && board.getPiece(checkHorizontal(p, -i)).getType() == 'k')
				return true;
			if(board.getPiece(checkHorizontal(p, -i)).getFarbe() == 'w')
				return false;
			if(board.getPiece(checkHorizontal(p, -i)).getType() == 'r' ||board.getPiece(checkHorizontal(p, -i)).getType() == 'q')
				return true;
			//if(board.getPiece(checkHorizontal(p, -i)).getType() != 'r' || board.getPiece(checkHorizontal(p, -i)).getType() != 'q')
				return false;
			
		}	
		
		return false;
	}
	
	public boolean wkingCheckHorizontal2(Piece p, Board board) {
		int row = Character.getNumericValue(p.getPosition().charAt(1));
		for(int i = 1; i < 8-(columns.indexOf(p.getPosition().charAt(0))); i++) {//check right horizontal for rooks or queens
			if(board.getPiece(checkHorizontal(p, i)).getType() == '-')
				continue;
			if(i == 1 && board.getPiece(checkHorizontal(p, i)).getType() == 'k')
				return true;
			if(board.getPiece(checkHorizontal(p, i)).getFarbe() == 'w')
				return false;
			if(board.getPiece(checkHorizontal(p, i)).getType() == 'r' || board.getPiece(checkHorizontal(p, i)).getType() == 'q')
				return true;
			//if(board.getPiece(checkHorizontal(p, i)).getType() != 'r' || board.getPiece(checkHorizontal(p, i)).getType() != 'q')
				return false;
			
		}
		return false;
	}
	
	public boolean wkingCheckDiagonal1(Piece p, Board board) {//up left		
		for(int i = 1; i < 8; i++) {	
			if(bishopHelper1(p, i, 'l', board)) {
				break;
			}
			if(board.getPiece(checkDiagonal(p, i, 'l')).getType() == '-')
				continue;
			if(i == 1 && board.getPiece(checkDiagonal(p, i, 'l')).getType() == 'k')
				return true;
			if(board.getPiece(checkDiagonal(p, i, 'l')).getFarbe() == 'w')
				return false;
			if(board.getPiece(checkDiagonal(p, i, 'l')).getType() == 'b' || board.getPiece(checkDiagonal(p, i, 'l')).getType() == 'q' ) 
				return true;
			//if(board.getPiece(checkDiagonal(p, i, 'l')).getType() != 'b' || board.getPiece(checkDiagonal(p, i, 'l')).getType() != 'q' ) 
				//return false;
				
		
		}

		return false;
		
	}
	
	
	public boolean wkingCheckDiagonal2(Piece p, Board board) {// up right
		for(int i = 1; i < 8; i++) {	
				if(bishopHelper2(p, i, 'r', board)) {
					break;
				}
				if(board.getPiece(checkDiagonal(p, i, 'r')).getType() == '-')
					continue;
				if(i == 1 && board.getPiece(checkDiagonal(p, i, 'r')).getType() == 'k')
					return true;
				if(board.getPiece(checkDiagonal(p, i, 'r')).getFarbe() == 'w')
					return false;
				if(board.getPiece(checkDiagonal(p, i, 'r')).getType() == 'b' || board.getPiece(checkDiagonal(p, i, 'r')).getType() == 'q' ) 
					return true;
				//if(board.getPiece(checkDiagonal(p, i, 'r')).getType() != 'b' || board.getPiece(checkDiagonal(p, i, 'r')).getType() != 'q' ) 
					//return false;
						
			}
		
		return false;
		
	}
	public boolean wkingCheckDiagonal3(Piece p, Board board) {//down left
		for(int i = 1; i < 8; i++) {	
			if(bishopHelper3(p, i, 'l', board)) {
				break;
			}
			if(board.getPiece(checkDiagonal(p, -i, 'l')).getType() == '-')
				continue;
			if(i == 1 && board.getPiece(checkDiagonal(p, -i, 'l')).getType() == 'k')
				return true;
			if(board.getPiece(checkDiagonal(p, -i, 'l')).getFarbe() == 'w')
				return false;
			if(board.getPiece(checkDiagonal(p, -i, 'l')).getType() == 'b' || board.getPiece(checkDiagonal(p, -i, 'l')).getType() == 'q' ) 
				return true;
			//if(board.getPiece(checkDiagonal(p, -i, 'l')).getType() != 'b' || board.getPiece(checkDiagonal(p, -i, 'l')).getType() != 'q' ) 
				//return false;
		
		}

		return false;
	
	}
	public boolean wkingCheckDiagonal4(Piece p, Board board) {//down right
		for(int i = 1; i < 8; i++) {	
			if(bishopHelper4(p, i, 'r', board)) {
				break;
			}
			if(board.getPiece(checkDiagonal(p, -i, 'r')).getType() == '-')
				continue;
			if(i == 1 && board.getPiece(checkDiagonal(p, -i, 'r')).getType() == 'k')
				return true;
			if(board.getPiece(checkDiagonal(p, -i, 'r')).getFarbe() == 'w')
				return false;
			if(board.getPiece(checkDiagonal(p, -i, 'r')).getType() == 'b' || board.getPiece(checkDiagonal(p, -i, 'r')).getType() == 'q' ) 
				return true;
			//if(board.getPiece(checkDiagonal(p, -i, 'r')).getType() != 'b' || board.getPiece(checkDiagonal(p, -i, 'r')).getType() != 'q' ) 
				//return false;
		}

		return false;
	}
	
	public boolean wkingCheckDiagonal5(Piece p, Board board) {//check for pawn checks
		if(p.getPosition().charAt(0) != 'a' && board.getPiece(checkDiagonal(p, 1, 'l')) != null) {
			if(board.getPiece(checkDiagonal(p, 1, 'l')).getType() == 'p') {
				return true;
			}
		}
		
		if(p.getPosition().charAt(0) != 'h' && board.getPiece(checkDiagonal(p, 1, 'r')) != null) {
			if(board.getPiece(checkDiagonal(p, 1, 'r')).getType() == 'p') 
				return true;
					
			}
			
		return false;
	}
	
	public boolean wkingCheckKnight1(Piece p, Board board) {//2 Left-1 up
		
		if(p.getPosition().charAt(0) == 'a' || p.getPosition().charAt(0) == 'b' || p.getPosition().charAt(1) == '8' )
			return false;
		StringBuilder dest = new StringBuilder();
		dest.append(columns.charAt(columns.indexOf(p.getPosition().charAt(0))-2));//2 columns shift left
		dest.append(Character.getNumericValue(p.getPosition().charAt(1))+1);//1 row up
			
		if(board.getPiece(dest.toString()).getType() == 'n')
			return true;
			
		return false;
		
	}
	public boolean wkingCheckKnight2(Piece p, Board board) {

		if(p.getPosition().charAt(0) == 'a' || p.getPosition().charAt(1) == '7' || p.getPosition().charAt(1) == '8')
			return false;
		StringBuilder dest = new StringBuilder();
		dest.append(columns.charAt(columns.indexOf(p.getPosition().charAt(0))-1));//1 columns shift left
		dest.append(Character.getNumericValue(p.getPosition().charAt(1))+2);//2 row up
			
		if(board.getPiece(dest.toString()).getType() == 'n')
			return true;
			
		return false;
	}
	public boolean wkingCheckKnight3(Piece p, Board board) {

		if(p.getPosition().charAt(0) == 'h' || p.getPosition().charAt(1) == '7' || p.getPosition().charAt(1) == '8')
			return false;
		StringBuilder dest = new StringBuilder();
		dest.append(columns.charAt(columns.indexOf(p.getPosition().charAt(0))+1));//1 columns shift right
		dest.append(Character.getNumericValue(p.getPosition().charAt(1))+2);//2 rows up
			
		if(board.getPiece(dest.toString()).getType() == 'n')
			return true;
			
		return false;
	}
	public boolean wkingCheckKnight4(Piece p, Board board) {

		if(p.getPosition().charAt(0) == 'g' || p.getPosition().charAt(0) == 'h' || p.getPosition().charAt(1) == '8')
			return false;
		StringBuilder dest = new StringBuilder();
		dest.append(columns.charAt(columns.indexOf(p.getPosition().charAt(0))+2));//2 columns shift right
		dest.append(Character.getNumericValue(p.getPosition().charAt(1))+1);//1 row up
			
		if(board.getPiece(dest.toString()).getType() == 'n')
			return true;
			
		return false;
	}	
	public boolean wkingCheckKnight5(Piece p, Board board) {

		if(p.getPosition().charAt(0) == 'a' || p.getPosition().charAt(0) == 'b' || p.getPosition().charAt(1) == '1')
			return false;
		StringBuilder dest = new StringBuilder();
		dest.append(columns.charAt(columns.indexOf(p.getPosition().charAt(0))-2));//2 columns shift left
		dest.append(Character.getNumericValue(p.getPosition().charAt(1))-1);//1 row down
			
		if(board.getPiece(dest.toString()).getType() == 'n')
			return true;
			
		return false;
	}
	public boolean wkingCheckKnight6(Piece p, Board board) {

		if(p.getPosition().charAt(0) == 'a' || p.getPosition().charAt(1) == '1' || p.getPosition().charAt(1) == '2')
			return false;
		StringBuilder dest = new StringBuilder();
		dest.append(columns.charAt(columns.indexOf(p.getPosition().charAt(0))-1));//1 columns shift left
		dest.append(Character.getNumericValue(p.getPosition().charAt(1))-2);//2 row down
			
		if(board.getPiece(dest.toString()).getType() == 'n')
			return true;
			
		return false;
	}
	public boolean wkingCheckKnight7(Piece p, Board board) {

		if(p.getPosition().charAt(0) == 'h' || p.getPosition().charAt(1) == '1' || p.getPosition().charAt(1) == '2')
			return false;
		StringBuilder dest = new StringBuilder();
		dest.append(columns.charAt(columns.indexOf(p.getPosition().charAt(0))+1));//1 columns shift right
		dest.append(Character.getNumericValue(p.getPosition().charAt(1))-2);// 2 rows down
			
		if(board.getPiece(dest.toString()).getType() == 'n')
			return true;
			
		return false;
	}
	public boolean wkingCheckKnight8(Piece p, Board board) {

		if(p.getPosition().charAt(0) == 'g' || p.getPosition().charAt(0) == 'h' || p.getPosition().charAt(1) == '1')
			return false;
		StringBuilder dest = new StringBuilder();
		dest.append(columns.charAt(columns.indexOf(p.getPosition().charAt(0))+2));//2 columns shift right
		dest.append(Character.getNumericValue(p.getPosition().charAt(1))-1);//1 row down
			
		if(board.getPiece(dest.toString()).getType() == 'n')
			return true;
			
		return false;
	}
	
	/*
	public static void main(String args[]) {
		CrazyhouseGame game = new CrazyhouseGame();
		game.setBoard("rnbqkbnr/pppppppp/3R4/8/8/8/PPPPPPPP/RNBQKBNR/");
		User xqc = new User("xqc", "xqc");
		User moist = new User("moist", "moist");
		Player whitePlayer = new Player(xqc, game);
		Player blackPlayer = new Player(moist, game);
		game.addPlayer(whitePlayer);
		game.addPlayer(blackPlayer);
		
		
		System.out.println(game.getBoard());
		game.Board.print();
		System.out.println();
		System.out.print("Pieces: ");
		System.out.println("pieces size: " + game.Board.pieces.size());
		for(Piece p : game.Board.pieces)
			System.out.println(p + " (index: " + game.Board.pieces.indexOf(p) + ")");
		System.out.println();
		
		
		//game.tryMove("d6-d7", whitePlayer);
		game.doMove("a2-a7", game.Board);
		System.out.println(game.getBoard());
		game.Board.print();
		System.out.println();
		System.out.print("Pieces: ");	
		System.out.println("pieces size: " + game.Board.pieces.size());
		for(Piece p : game.Board.pieces)
			System.out.println(p + " (index: " + game.Board.pieces.indexOf(p) + ")");
		System.out.println();
		
		//game.tryMove("d8-d7", blackPlayer);
		game.doMove("a7-a8", game.Board);
		System.out.println(game.getBoard());
		game.Board.print();
		System.out.println();
		System.out.print("Pieces: ");
		System.out.println("pieces size: " + game.Board.pieces.size());
		for(Piece p : game.Board.pieces)
			System.out.println(p + " (index: " + game.Board.pieces.indexOf(p) + ")");
		
		game.doMove("b2-b7", game.Board);
		System.out.println(game.getBoard());
		game.Board.print();
		System.out.println();
		System.out.print("Pieces: ");
		System.out.println("pieces size: " + game.Board.pieces.size());
		for(Piece p : game.Board.pieces)
			System.out.println(p + " (index: " + game.Board.pieces.indexOf(p) + ")");
		
		game.doMove("b7-b8", game.Board);
		System.out.println(game.getBoard());
		game.Board.print();
		System.out.println();
		System.out.print("Pieces: ");
		System.out.println("pieces size: " + game.Board.pieces.size());
		for(Piece p : game.Board.pieces)
			System.out.println(p + " (index: " + game.Board.pieces.indexOf(p) + ")");
		
		game.doMove("c7-c2", game.Board);
		System.out.println(game.getBoard());
		game.Board.print();
		System.out.println();
		System.out.print("Pieces: ");
		System.out.println("pieces size: " + game.Board.pieces.size());
		for(Piece p : game.Board.pieces)
			System.out.println(p + " (index: " + game.Board.pieces.indexOf(p) + ")");
		
		game.doMove("c2-c1", game.Board);
		System.out.println(game.getBoard());
		game.Board.print();
		System.out.println();
		System.out.print("Pieces: ");
		System.out.println("pieces size: " + game.Board.pieces.size());
		for(Piece p : game.Board.pieces)
			System.out.println(p + " (index: " + game.Board.pieces.indexOf(p) + ")");
	
	
		
		CrazyhouseGame game = new CrazyhouseGame();
		System.out.println(game.getBoard());
		game.setBoard("rnbq1bkr/pppppprp/8/8/8/8/PPPPPPPP/RNBQKBNR/");
		game.Board.print();
		System.out.println();
		User xqc = new User("xqc", "xqc");
		User moist = new User("moist", "moist");
		Player whitePlayer = new Player(xqc, game);
		Player blackPlayer = new Player(moist, game);
		game.addPlayer(whitePlayer);
		game.addPlayer(blackPlayer);
		
		
		
		
		if(game.tryMove("g4-h6", whitePlayer))
			System.out.println("yarak");
		
		game.doMove("b1-h6");
		game.Board.print();
		System.out.println();
		
		game.fillValidMoves();
		
		if(game.checkIfInCheckBlack())
			System.out.println("black in check");
		
		System.out.println("Valid moves black: ");
		for(String s: game.validMovesBlack) {
			System.out.print(s + ", "); 
		}
		
		
		
		
		
		
		
		//below is a classic checkmate scenario example
		//------------------------------------------------------------------------------------------------------------------------------------------------------
		if(game.tryMove("e2-e4", whitePlayer))
			System.out.println("valid move");	
		//											just copy and paste this part of the code with alternating players and change moveStrings for quick testing
		game.Board.print();
		System.out.println();
		//------------------------------------------------------------------------------------------------------------------------------------------------------
		
		if(game.tryMove("e7-e5", blackPlayer))
			System.out.println("valid move");
		game.Board.print();
		System.out.println();
		
		if(game.tryMove("g1-f3", whitePlayer))
			System.out.println("valid move");	
		game.Board.print();
		System.out.println();
		
		if(game.tryMove("b8-c6", blackPlayer))
			System.out.println("valid move");
		game.Board.print();
		System.out.println();
		
		if(game.tryMove("d2-d4", whitePlayer))
			System.out.println("valid move");	
		game.Board.print();
		System.out.println();
		
		if(game.tryMove("e5-d4", blackPlayer))
			System.out.println("valid move");
		game.Board.print();
		System.out.println();
		
		if(game.tryMove("f3-d4", whitePlayer))
			System.out.println("valid move");	
		game.Board.print();
		System.out.println();
		
		if(game.tryMove("f8-c5", blackPlayer))
			System.out.println("valid move");
		game.Board.print();
		System.out.println();
		
		if(game.tryMove("c2-c3", whitePlayer))
			System.out.println("valid move");	
		game.Board.print();
		System.out.println();
		
		if(game.tryMove("d8-f6", blackPlayer))
			System.out.println("valid move");
		game.Board.print();
		System.out.println();
		
		if(game.tryMove("d4-c6", whitePlayer))
			System.out.println("valid move");	
		game.Board.print();
		System.out.println();
		
		
		if(game.tryMove("f6-f2", blackPlayer))
			System.out.println("valid move");
		game.Board.print();
		System.out.println();
		
		
		
		
	}
	*/
	

}
