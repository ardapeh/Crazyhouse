package de.tuberlin.sese.swtpp.gameserver.test.crazyhouse;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.tuberlin.sese.swtpp.gameserver.control.GameController;
import de.tuberlin.sese.swtpp.gameserver.model.Game;
import de.tuberlin.sese.swtpp.gameserver.model.Player;
import de.tuberlin.sese.swtpp.gameserver.model.User;

public class TryMoveIntegrationTest {

	User user1 = new User("Alice", "alice");
	User user2 = new User("Bob", "bob");
	
	Player whitePlayer = null;
	Player blackPlayer = null;
	Game game = null;
	GameController controller;
	
	@Before
	public void setUp() throws Exception { 
		controller = GameController.getInstance();
		controller.clear();
		
		int gameID = controller.startGame(user1, "", "crazyhouse");
		
		game =  controller.getGame(gameID);
		whitePlayer = game.getPlayer(user1);

	}
	
	public void startGame() {
		controller.joinGame(user2, "crazyhouse");		
		blackPlayer = game.getPlayer(user2);
	}
	
	public void startGame(String initialBoard, boolean whiteNext) {
		startGame();
		
		game.setBoard(initialBoard);
		game.setNextPlayer(whiteNext? whitePlayer:blackPlayer);
	}
	
	public void assertMove(String move, boolean white, boolean expectedResult) {
		if (white)
			assertEquals(expectedResult, game.tryMove(move, whitePlayer));
		else 
			assertEquals(expectedResult,game.tryMove(move, blackPlayer));
	}
	
	public void assertGameState(String expectedBoard, boolean whiteNext, boolean finished, boolean whiteWon) {
		String board = game.getBoard().replaceAll("e", "");
		
		assertEquals(expectedBoard,board);
		assertEquals(finished, game.isFinished());

		if (!game.isFinished()) {
			assertEquals(whiteNext, game.getNextPlayer() == whitePlayer);
		} else {
			assertEquals(whiteWon, whitePlayer.isWinner());
			assertEquals(!whiteWon, blackPlayer.isWinner());
		}
	}
	

	/*******************************************
	 * !!!!!!!!! To be implemented !!!!!!!!!!!!
	 *******************************************/
	
	@Test
	public void exampleTest() {
		startGame("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/",true);
		assertMove("b2-b7",true,false);
		assertGameState("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/",true,false,false);
	}

	//TODO: implement test cases of same kind as example here
	@Test
	public void  pawnTest() {
		//Basic pawn moves
		startGame("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/PPQbnr",true);
		assertMove("P-a4",true,true);
		assertGameState("rnbqkbnr/pppppppp/8/8/P7/8/PPPPPPPP/RNBQKBNR/PQbnr",false,false,false);
		
		startGame("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/PQbnr",true);
		assertMove("P-a4",true,true);
		assertGameState("rnbqkbnr/pppppppp/8/8/P7/8/PPPPPPPP/RNBQKBNR/Qbnr",false,false,false);
		
		startGame("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/",true);
		assertMove("a2-a3",true,true);
		assertGameState("rnbqkbnr/pppppppp/8/8/8/P7/1PPPPPPP/RNBQKBNR/",false,false,false);
		
		startGame("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/",true);
		assertMove("a2-a4",true,true);
		assertGameState("rnbqkbnr/pppppppp/8/8/P7/8/1PPPPPPP/RNBQKBNR/",false,false,false);
		
		startGame("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/",false);
		assertMove("a7-a6",false,true);
		assertGameState("rnbqkbnr/1ppppppp/p7/8/8/8/PPPPPPPP/RNBQKBNR/",true,false,false);
		
		startGame("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/",false);
		assertMove("a7-a5",false,true);
		assertGameState("rnbqkbnr/1ppppppp/8/p7/8/8/PPPPPPPP/RNBQKBNR/",true,false,false);
		
		startGame("rnbqkbnr/pppppppp/8/3P4/8/8/PPPPPPPP/RNBQKBNR/",true);
		assertMove("d5-d4",true,false);
		assertGameState("rnbqkbnr/pppppppp/8/3P4/8/8/PPPPPPPP/RNBQKBNR/",true,false,false);
		
		startGame("rnbqkbnr/pppppppp/8/8/3p4/8/PPPPPPPP/RNBQKBNR/",false);
		assertMove("d4-d5",false,false);
		assertGameState("rnbqkbnr/pppppppp/8/8/3p4/8/PPPPPPPP/RNBQKBNR/",false,false,false);
		
		startGame("rnbqkbnr/pppppppp/8/8/8/P7/PPPPPPPP/RNBQKBNR/",true);
		assertMove("a2-a3",true,false);
		assertGameState("rnbqkbnr/pppppppp/8/8/8/P7/PPPPPPPP/RNBQKBNR/",true,false,false);
		
		startGame("rnbqkbnr/pppppppp/8/8/P7/8/PPPPPPPP/RNBQKBNR/",true);
		assertMove("a2-a4",true,false);
		assertGameState("rnbqkbnr/pppppppp/8/8/P7/8/PPPPPPPP/RNBQKBNR/",true,false,false);
		
		startGame("rnbqkbnr/pppppppp/p7/8/8/8/PPPPPPPP/RNBQKBNR/",false);
		assertMove("a7-a6",false,false);
		assertGameState("rnbqkbnr/pppppppp/p7/8/8/8/PPPPPPPP/RNBQKBNR/",false,false,false);
		
		startGame("rnbqkbnr/pppppppp/8/p7/8/8/PPPPPPPP/RNBQKBNR/",false);
		assertMove("a7-a5",false,false);
		assertGameState("rnbqkbnr/pppppppp/8/p7/8/8/PPPPPPPP/RNBQKBNR/",false,false,false);
		
		//Pawn takes tests
		startGame("rnbqkbnr/pppppppp/8/8/8/3p4/PPPPPPPP/RNBQKBNR/",true);
		assertMove("c2-d3",true,true);
		assertGameState("rnbqkbnr/pppppppp/8/8/8/3P4/PP1PPPPP/RNBQKBNR/P",false,false,false);
		
		startGame("rnbqkbnr/pppppppp/8/8/8/3p4/PPPPPPPP/RNBQKBNR/",true);
		assertMove("e2-d3",true,true);
		assertGameState("rnbqkbnr/pppppppp/8/8/8/3P4/PPPP1PPP/RNBQKBNR/P",false,false,false);
		
		startGame("rnbqkbnr/pppppppp/8/8/8/3p4/PPPPPPPP/RNBQKBNR/",true);
		assertMove("d2-d3",true,false);
		assertGameState("rnbqkbnr/pppppppp/8/8/8/3p4/PPPPPPPP/RNBQKBNR/",true,false,false);
		
		startGame("rnbqkbnr/pppppppp/3P4/8/8/8/PPP1PPPP/RNBQKBNR/",false);
		assertMove("c7-d6",false,true);
		assertGameState("rnbqkbnr/pp1ppppp/3p4/8/8/8/PPP1PPPP/RNBQKBNR/p",true,false,false);
		
		startGame("rnbqkbnr/pppppppp/3P4/8/8/8/PPP1PPPP/RNBQKBNR/",false);
		assertMove("e7-d6",false,true);
		assertGameState("rnbqkbnr/pppp1ppp/3p4/8/8/8/PPP1PPPP/RNBQKBNR/p",true,false,false);
		
		startGame("rnbqkbnr/pppppppp/3P4/8/8/8/PPP1PPPP/RNBQKBNR/",false);
		assertMove("d7-d6",false,false);
		assertGameState("rnbqkbnr/pppppppp/3P4/8/8/8/PPP1PPPP/RNBQKBNR/",false,false,false);
		
		//Pawn drop tests
		startGame("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/P",true);
		assertMove("P-d4",true,true);
		assertGameState("rnbqkbnr/pppppppp/8/8/3P4/8/PPPPPPPP/RNBQKBNR/",false,false,false);
		
		startGame("1nbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/1NBQKBNR/P",true);
		assertMove("P-a8",true,false);
		assertGameState("1nbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/1NBQKBNR/P",true,false,false);
		
		startGame("1nbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/1NBQKBNR/P",true);
		assertMove("P-a1",true,false);
		assertGameState("1nbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/1NBQKBNR/P",true,false,false);
		
		startGame("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/P",true);
		assertMove("P-a1",true,false);
		assertGameState("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/P",true,false,false);
		
		startGame("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/p",false);
		assertMove("p-d4",false,true);
		assertGameState("rnbqkbnr/pppppppp/8/8/3p4/8/PPPPPPPP/RNBQKBNR/",true,false,false);
		
		startGame("1nbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/1NBQKBNR/p",false);
		assertMove("p-a8",false,false);
		assertGameState("1nbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/1NBQKBNR/p",false,false,false);
		
		startGame("1nbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/1NBQKBNR/p",false);
		assertMove("p-a1",false,false);
		assertGameState("1nbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/1NBQKBNR/p",false,false,false);
		
		startGame("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/p",false);
		assertMove("p-a1",false,false);
		assertGameState("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/p",false,false,false);
		
		//Pawn promotion tests
		startGame("1nbqkbnr/Pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/",true);
		assertMove("a7-a8",true,true);
		assertGameState("Qnbqkbnr/1ppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/",false,false,false);
		
		startGame("rnbqkbnr/Pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/",true);
		assertMove("a7-b8",true,true);
		assertGameState("rQbqkbnr/1ppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/N",false,false,false);
		
		startGame("rnbqkbnr/pppppppp/8/8/8/8/pPPPPPPP/1NBQKBNR/",false);
		assertMove("a2-a1",false,true);
		assertGameState("rnbqkbnr/pppppppp/8/8/8/8/1PPPPPPP/qNBQKBNR/",true,false,false);
		
		startGame("rnbqkbnr/pppppppp/8/8/8/8/pPPPPPPP/RNBQKBNR/",false);
		assertMove("a2-b1",false,true);
		assertGameState("rnbqkbnr/pppppppp/8/8/8/8/1PPPPPPP/RqBQKBNR/n",true,false,false);
		
	}
	
	@Test
	public void rookTest() {
		//basic rook moves
		startGame("rnbqkbnr/pppppppp/8/R7/8/8/PPPPPPPP/RNBQKBNR/",true);
		assertMove("a5-h5",true,true);
		assertGameState("rnbqkbnr/pppppppp/8/7R/8/8/PPPPPPPP/RNBQKBNR/",false,false,false);
		
		startGame("rnbqkbnr/pppppppp/8/r7/8/8/PPPPPPPP/RNBQKBNR/",false);
		assertMove("a5-h5",false,true);
		assertGameState("rnbqkbnr/pppppppp/8/7r/8/8/PPPPPPPP/RNBQKBNR/",true,false,false);
		
		startGame("rnbqkbnr/pppppppp/8/R3P3/8/8/PPPPPPPP/RNBQKBNR/",true);
		assertMove("a5-h5",true,false);
		assertGameState("rnbqkbnr/pppppppp/8/R3P3/8/8/PPPPPPPP/RNBQKBNR/",true,false,false);
		
		startGame("rnbqkbnr/pppppppp/8/r3p3/8/8/PPPPPPPP/RNBQKBNR/",false);
		assertMove("a5-h5",false,false);
		assertGameState("rnbqkbnr/pppppppp/8/r3p3/8/8/PPPPPPPP/RNBQKBNR/",false,false,false);
		
		startGame("rnbqkbnr/pppppppp/8/7R/8/8/PPPPPPPP/RNBQKBNR/",true);
		assertMove("h5-a5",true,true);
		assertGameState("rnbqkbnr/pppppppp/8/R7/8/8/PPPPPPPP/RNBQKBNR/",false,false,false);
		
		startGame("rnbqkbnr/pppppppp/8/3P3R/8/8/PPPPPPPP/RNBQKBNR/",true);
		assertMove("h5-a5",true,false);
		assertGameState("rnbqkbnr/pppppppp/8/3P3R/8/8/PPPPPPPP/RNBQKBNR/",true,false,false);
		
		startGame("rnbqkbnr/pppppppp/8/7r/8/8/PPPPPPPP/RNBQKBNR/",false);
		assertMove("h5-a5",false,true);
		assertGameState("rnbqkbnr/pppppppp/8/r7/8/8/PPPPPPPP/RNBQKBNR/",true,false,false);
		
		startGame("rnbqkbnr/pppppppp/8/3p3r/8/8/PPPPPPPP/RNBQKBNR/",false);
		assertMove("h5-a5",false,false);
		assertGameState("rnbqkbnr/pppppppp/8/3p3r/8/8/PPPPPPPP/RNBQKBNR/",false,false,false);
		
		startGame("1nbqkbnr/1ppppppp/8/8/8/R7/1PPPPPPP/1NBQKBNR/",true);
		assertMove("a3-a8",true,true);
		assertGameState("Rnbqkbnr/1ppppppp/8/8/8/8/1PPPPPPP/1NBQKBNR/",false,false,false);
		
		startGame("1nbqkbnr/1ppppppp/8/P7/8/R7/PPPPPPPP/RNBQKBNR/",true);
		assertMove("a3-a8",true,false);
		assertGameState("1nbqkbnr/1ppppppp/8/P7/8/R7/PPPPPPPP/RNBQKBNR/",true,false,false);
		
		startGame("1nbqkbnr/1ppppppp/8/8/8/r7/PPPPPPPP/RNBQKBNR/",false);
		assertMove("a3-a8",false,true);
		assertGameState("rnbqkbnr/1ppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/",true,false,false);
		
		startGame("1nbqkbnr/1ppppppp/8/p7/8/r7/PPPPPPPP/RNBQKBNR/",false);
		assertMove("a3-a8",false,false);
		assertGameState("1nbqkbnr/1ppppppp/8/p7/8/r7/PPPPPPPP/RNBQKBNR/",false,false,false);
		
		startGame("rnbqkbnr/pppppppp/R7/8/8/8/1PPPPPPP/1NBQKBNR/",true);
		assertMove("a6-a1",true,true);
		assertGameState("rnbqkbnr/pppppppp/8/8/8/8/1PPPPPPP/RNBQKBNR/",false,false,false);
		
		startGame("rnbqkbnr/pppppppp/R7/8/P7/8/1PPPPPPP/1NBQKBNR/",true);
		assertMove("a6-a1",true,false);
		assertGameState("rnbqkbnr/pppppppp/R7/8/P7/8/1PPPPPPP/1NBQKBNR/",true,false,false);
				
		startGame("rnbqkbnr/pppppppp/r7/8/8/8/1PPPPPPP/1NBQKBNR/",false);
		assertMove("a6-a1",false,true);
		assertGameState("rnbqkbnr/pppppppp/8/8/8/8/1PPPPPPP/rNBQKBNR/",true,false,false);
		
		startGame("rnbqkbnr/pppppppp/r7/8/p7/8/1PPPPPPP/1NBQKBNR/",false);
		assertMove("a6-a1",false,false);
		assertGameState("rnbqkbnr/pppppppp/r7/8/p7/8/1PPPPPPP/1NBQKBNR/",false,false,false);
		
		
		//rook takes tests
		
		startGame("rnbqkbnr/pppppppp/8/R3p3/8/8/PPPPPPPP/RNBQKBNR/",true);
		assertMove("a5-e5",true,true);
		assertGameState("rnbqkbnr/pppppppp/8/4R3/8/8/PPPPPPPP/RNBQKBNR/P",false,false,false);
		
		startGame("rnbqkbnr/pppppppp/8/r3P3/8/8/PPPPPPPP/RNBQKBNR/",false);
		assertMove("a5-e5",false,true);
		assertGameState("rnbqkbnr/pppppppp/8/4r3/8/8/PPPPPPPP/RNBQKBNR/p",true,false,false);
		
		startGame("rnbqkbnr/pppppppp/8/3p3R/8/8/PPPPPPPP/RNBQKBNR/",true);
		assertMove("h5-d5",true,true);
		assertGameState("rnbqkbnr/pppppppp/8/3R4/8/8/PPPPPPPP/RNBQKBNR/P",false,false,false);
		
		startGame("rnbqkbnr/pppppppp/8/3P3r/8/8/PPPPPPPP/RNBQKBNR/",false);
		assertMove("h5-d5",false,true);
		assertGameState("rnbqkbnr/pppppppp/8/3r4/8/8/PPPPPPPP/RNBQKBNR/p",true,false,false);
		
		startGame("1nbqkbnr/1ppppppp/8/p7/8/R7/PPPPPPPP/RNBQKBNR/",true);
		assertMove("a3-a5",true,true);
		assertGameState("1nbqkbnr/1ppppppp/8/R7/8/8/PPPPPPPP/RNBQKBNR/P",false,false,false);
		
		startGame("1nbqkbnr/1ppppppp/8/P7/8/r7/PPPPPPPP/RNBQKBNR/",false);
		assertMove("a3-a5",false,true);
		assertGameState("1nbqkbnr/1ppppppp/8/r7/8/8/PPPPPPPP/RNBQKBNR/p",true,false,false);
		
		startGame("rnbqkbnr/pppppppp/R7/8/p7/8/1PPPPPPP/1NBQKBNR/",true);
		assertMove("a6-a4",true,true);
		assertGameState("rnbqkbnr/pppppppp/8/8/R7/8/1PPPPPPP/1NBQKBNR/P",false,false,false);
		
		startGame("rnbqkbnr/pppppppp/r7/8/P7/8/1PPPPPPP/1NBQKBNR/",false);
		assertMove("a6-a4",false,true);
		assertGameState("rnbqkbnr/pppppppp/8/8/r7/8/1PPPPPPP/1NBQKBNR/p",true,false,false);
		
		//rook drop test 
		startGame("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/R",true);
		assertMove("R-d4",true,true);
		assertGameState("rnbqkbnr/pppppppp/8/8/3R4/8/PPPPPPPP/RNBQKBNR/",false,false,false);
		
		startGame("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/r",false);
		assertMove("r-d4",false,true);
		assertGameState("rnbqkbnr/pppppppp/8/8/3r4/8/PPPPPPPP/RNBQKBNR/",true,false,false);
		
		startGame("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/R",true);
		assertMove("R-a2",true,false);
		assertGameState("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/R",true,false,false);
		
		startGame("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/r",false);
		assertMove("r-a2",false,false);
		assertGameState("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/r",false,false,false);
	}
	
	
	@Test
	public void knightTest() {
		//Basic knight moves
		startGame("n1bqkbn1/8/1P6/8/8/8/PPPPPPPP/K7/",false);
		assertMove("a8-b6",false,true);
		assertGameState("2bqkbn1/8/1n6/8/8/8/PPPPPPPP/K7/p",true,false,false);
		
		startGame("n1bqkbn1/8/1P6/8/8/8/PPPPPPPP/K6n/",false);
		assertMove("h1-g3",false,true);
		assertGameState("n1bqkbn1/8/1P6/8/8/6n1/PPPPPPPP/K7/",true,false,false);
		
		startGame("n1bqkbn1/8/1P6/8/8/6P1/PPPPPPPP/K6n/",false);
		assertMove("h1-g3",false,true);
		assertGameState("n1bqkbn1/8/1P6/8/8/6n1/PPPPPPPP/K7/p",true,false,false);
		
		startGame("n1bqkbn1/8/8/8/8/1P6/PPPPPPPP/nK6/",false);
		assertMove("a1-b3",false,true);
		assertGameState("n1bqkbn1/8/8/8/8/1n6/PPPPPPPP/1K6/p",true,false,false);
		
		startGame("2bqkb2/8/8/8/8/8/nnPPPPPP/nK6/",false);
		assertMove("a2-a4",false,false);
		assertGameState("2bqkb2/8/8/8/8/8/nnPPPPPP/nK6/",false,false,false);
		
		startGame("n1bqkbn1/8/1p6/8/8/8/PPPPPPPP/K7/",false);
		assertMove("a8-b6",false,false);
		assertGameState("n1bqkbn1/8/1p6/8/8/8/PPPPPPPP/K7/",false,false,false);
		
		startGame("n1bqkbn1/8/8/8/8/8/PPPPPPPP/K7/",false);
		assertMove("a8-b6",false,true);
		assertGameState("2bqkbn1/8/1n6/8/8/8/PPPPPPPP/K7/",true,false,false);
		
		startGame("Nnbqkbn1/8/1p6/8/8/8/PPPPPPPP/K7/",true);
		assertMove("a8-b6",true,true);
		assertGameState("1nbqkbn1/8/1N6/8/8/8/PPPPPPPP/K7/P",false,false,false);
		
		startGame("Nnbqkbn1/2p5/8/8/8/8/PPPPPPPP/K7/",true);
		assertMove("a8-c7",true,true);
		assertGameState("1nbqkbn1/2N5/8/8/8/8/PPPPPPPP/K7/P",false,false,false);
		
		startGame("rnbqkbn1/8/8/8/8/8/PNPPPPPP/K7/",true);
		assertMove("b2-a4",true,true);
		assertGameState("rnbqkbn1/8/8/8/N7/8/P1PPPPPP/K7/",false,false,false);
		
		startGame("rnbqkbnN/8/6p1/8/8/8/PPPPPPPP/K7/",true);
		assertMove("h8-g6",true,true);
		assertGameState("rnbqkbn1/8/6N1/8/8/8/PPPPPPPP/K7/P",false,false,false);
		
		startGame("rnbqkbnN/8/8/8/8/8/PPPPPPPP/K7/",true);
		assertMove("h8-a1",true,false);
		assertGameState("rnbqkbnN/8/8/8/8/8/PPPPPPPP/K7/",true,false,false);
		
		startGame("rnbqkbnN/5p2/8/8/8/8/PPPPPPPP/K7/",true);
		assertMove("h8-f7",true,true);
		assertGameState("rnbqkbn1/5N2/8/8/8/8/PPPPPPPP/K7/P",false,false,false);
		
		startGame("rnbqkbnr/8/8/8/N7/8/PPPPPPPP/RNBQKBNR/",true);
		assertMove("a4-a2",true,false);
		assertGameState("rnbqkbnr/8/8/8/N7/8/PPPPPPPP/RNBQKBNR/",true,false,false);
		
		startGame("rnbqkbnr/8/8/8/7N/8/PPPPPPPP/RNBQKBNR/",true);
		assertMove("h4-h2",true,false);
		assertGameState("rnbqkbnr/8/8/8/7N/8/PPPPPPPP/RNBQKBNR/",true,false,false);
		
		startGame("rnbqkbnr/8/8/8/N7/8/PPPPPPPP/RNBQKBNN/",true);
		assertMove("h1-g3",true,true);
		assertGameState("rnbqkbnr/8/8/8/N7/6N1/PPPPPPPP/RNBQKBN1/",false,false,false);
		
		startGame("Nnbqkbnr/8/8/8/8/8/PPPPPPPP/RNBQKBNR/",true);
		assertMove("a8-b6",true,true);
		assertGameState("1nbqkbnr/8/1N6/8/8/8/PPPPPPPP/RNBQKBNR/",false,false,false);
		
		startGame("rnbqkbnr/8/8/3N4/8/8/PPPPPPPP/RNBQKBNR/",true);
		assertMove("d5-a3",true,false);
		assertGameState("rnbqkbnr/8/8/3N4/8/8/PPPPPPPP/RNBQKBNR/",true,false,false);
		
		startGame("rnbqkbnr/8/8/3n4/8/8/PPPPPPPP/RNBQKBNR/",false);
		assertMove("d5-a3",false,false);
		assertGameState("rnbqkbnr/8/8/3n4/8/8/PPPPPPPP/RNBQKBNR/",false,false,false);
		
		startGame("rnbqkbnr/8/8/3N4/8/8/PPPPPPPP/RNBQKBNR/",true);
		assertMove("d5-b6",true,true);
		assertGameState("rnbqkbnr/8/1N6/8/8/8/PPPPPPPP/RNBQKBNR/",false,false,false);
		
		startGame("rnbqkbnr/8/8/3n4/8/8/PPPPPPPP/RNBQKBNR/",false);
		assertMove("d5-b6",false,true);
		assertGameState("rnbqkbnr/8/1n6/8/8/8/PPPPPPPP/RNBQKBNR/",true,false,false);
		
		startGame("rnbqkbnr/8/8/3N4/8/8/PPPPPPPP/RNBQKBNR/",true);
		assertMove("d5-c7",true,true);
		assertGameState("rnbqkbnr/2N5/8/8/8/8/PPPPPPPP/RNBQKBNR/",false,false,false);
		
		startGame("rnbqkbnr/8/8/3n4/8/8/PPPPPPPP/RNBQKBNR/",false);
		assertMove("d5-c7",false,true);
		assertGameState("rnbqkbnr/2n5/8/8/8/8/PPPPPPPP/RNBQKBNR/",true,false,false);
		
		startGame("rnbqkbnr/8/8/3N4/8/8/PPPPPPPP/RNBQKBNR/",true);
		assertMove("d5-e7",true,true);
		assertGameState("rnbqkbnr/4N3/8/8/8/8/PPPPPPPP/RNBQKBNR/",false,false,false);
		
		startGame("rnbqkbnr/8/8/3n4/8/8/PPPPPPPP/RNBQKBNR/",false);
		assertMove("d5-e7",false,true);
		assertGameState("rnbqkbnr/4n3/8/8/8/8/PPPPPPPP/RNBQKBNR/",true,false,false);
		
		startGame("rnbqkbnr/8/8/3N4/8/8/PPPPPPPP/RNBQKBNR/",true);
		assertMove("d5-f6",true,true);
		assertGameState("rnbqkbnr/8/5N2/8/8/8/PPPPPPPP/RNBQKBNR/",false,false,false);
		
		startGame("rnbqkbnr/8/8/3n4/8/8/PPPPPPPP/RNBQKBNR/",false);
		assertMove("d5-f6",false,true);
		assertGameState("rnbqkbnr/8/5n2/8/8/8/PPPPPPPP/RNBQKBNR/",true,false,false);
		
		startGame("rnbqkbnr/8/8/3N4/8/8/PPPPPPPP/RNBQKBNR/",true);
		assertMove("d5-f4",true,true);
		assertGameState("rnbqkbnr/8/8/8/5N2/8/PPPPPPPP/RNBQKBNR/",false,false,false);
		
		startGame("rnbqkbnr/8/8/3n4/8/8/PPPPPPPP/RNBQKBNR/",false);
		assertMove("d5-f4",false,true);
		
		startGame("rnbqkbnr/8/8/3N4/8/8/PPPPPPPP/RNBQKBNR/",true);
		assertMove("d5-e3",true,true);
		assertGameState("rnbqkbnr/8/8/8/8/4N3/PPPPPPPP/RNBQKBNR/",false,false,false);
		
		startGame("rnbqkbnr/8/8/3n4/8/8/PPPPPPPP/RNBQKBNR/",false);
		assertMove("d5-e3",false,true);
		assertGameState("rnbqkbnr/8/8/8/8/4n3/PPPPPPPP/RNBQKBNR/",true,false,false);
		
		startGame("rnbqkbnr/8/8/3N4/8/8/PPPPPPPP/RNBQKBNR/",true);
		assertMove("d5-c3",true,true);
		assertGameState("rnbqkbnr/8/8/8/8/2N5/PPPPPPPP/RNBQKBNR/",false,false,false);
		
		startGame("rnbqkbnr/8/8/3n4/8/8/PPPPPPPP/RNBQKBNR/",false);
		assertMove("d5-c3",false,true);
		assertGameState("rnbqkbnr/8/8/8/8/2n5/PPPPPPPP/RNBQKBNR/",true,false,false);
		
		startGame("rnbqkbnr/8/8/3N4/8/8/PPPPPPPP/RNBQKBNR/",true);
		assertMove("d5-b4",true,true);
		assertGameState("rnbqkbnr/8/8/8/1N6/8/PPPPPPPP/RNBQKBNR/",false,false,false);
		
		startGame("rnbqkbnr/8/8/3n4/8/8/PPPPPPPP/RNBQKBNR/",false);
		assertMove("d5-b4",false,true);
		assertGameState("rnbqkbnr/8/8/8/1n6/8/PPPPPPPP/RNBQKBNR/",true,false,false);
		
		startGame("1n1k4/8/4/8/3K4/8/8/8/",false);
		assertMove("b8-c6",false,true);
		assertGameState("3k4/8/2n5/8/3K4/8/8/8/",true,false,false);
		
		//Knight drop test
		startGame("rnbqkbnr/8/8/8/8/8/PPPPPPPP/RNBQKBNR/N",true);
		assertMove("N-d4",true,true);
		assertGameState("rnbqkbnr/8/8/8/3N4/8/PPPPPPPP/RNBQKBNR/",false,false,false);
		
		startGame("rnbqkbnr/8/8/8/8/8/PPPPPPPP/RNBQKBNR/N",true);
		assertMove("N-a2",true,false);
		assertGameState("rnbqkbnr/8/8/8/8/8/PPPPPPPP/RNBQKBNR/N",true,false,false);
		
		startGame("rnbqkbnr/8/8/8/8/8/PPPPPPPP/RNBQKBNR/n",false);
		assertMove("n-d4",false,true);
		assertGameState("rnbqkbnr/8/8/8/3n4/8/PPPPPPPP/RNBQKBNR/",true,false,false);
		
		startGame("rnbqkbnr/8/8/8/8/8/PPPPPPPP/RNBQKBNR/n",false);
		assertMove("n-a2",false,false);
		assertGameState("rnbqkbnr/8/8/8/8/8/PPPPPPPP/RNBQKBNR/n",false,false,false);
	}
	
	@Test
	public void kingTest() {
		//King basic moves
		startGame("6K1/8/8/8/8/8/8/b6k/",true);
		assertMove("g8-h8",true,false);
		assertGameState("6K1/8/8/8/8/8/8/b6k/",true,false,false);
		
		startGame("K7/3n4/8/8/8/8/8/7k/",true);
		assertMove("a8-b8",true,false);
		assertGameState("K7/3n4/8/8/8/8/8/7k/",true,false,false);
		
		startGame("p7/8/1K6/8/8/8/8/7k/",true);
		assertMove("b6-b7",true,false);
		assertGameState("p7/8/1K6/8/8/8/8/7k/",true,false,false);
		
		startGame("2p5/8/1K6/8/8/8/8/7k/",true);
		assertMove("b6-b7",true,false);
		assertGameState("2p5/8/1K6/8/8/8/8/7k/",true,false,false);
		
		startGame("1K6/3k4/8/8/8/8/8/8/",true);
		assertMove("b8-c8",true,false);
		assertGameState("1K6/3k4/8/8/8/8/8/8/",true,false,false);
		
		startGame("1K6/3b4/8/8/8/8/8/k7/",true);
		assertMove("b8-c8",true,false);
		assertGameState("1K6/3b4/8/8/8/8/8/k7/",true,false,false);
		
		startGame("rnbqkbnr/8/8/8/3K4/8/PPPPPPPP/RNBQ1BNR/",true);
		assertMove("d4-d3",true,false);
		assertGameState("rnbqkbnr/8/8/8/3K4/8/PPPPPPPP/RNBQ1BNR/",true,false,false);
		
		startGame("rnbq1bnr/8/8/8/3k4/8/PPPPPPPP/RNBQ1BNR/K",false);
		assertMove("d4-d3",false,false);
		assertGameState("rnbq1bnr/8/8/8/3k4/8/PPPPPPPP/RNBQ1BNR/K",false,false,false);
		
		startGame("rnbqkbnr/8/8/8/3K4/8/PPPPPPPP/RNBQKBNR/",true);
		assertMove("d4-c3",true,true);
		assertGameState("rnbqkbnr/8/8/8/8/2K5/PPPPPPPP/RNBQKBNR/",false,false,false);
				
		startGame("rnbqkbnr/8/8/8/3K4/8/PPPPPPPP/RNBQKBNR/",true);
		assertMove("d4-e3",true,true);
		assertGameState("rnbqkbnr/8/8/8/8/4K3/PPPPPPPP/RNBQKBNR/",false,false,false);
		
		startGame("rnbqkbnr/8/8/8/3K4/8/PPPPPPPP/RNBQKBNR/",true);
		assertMove("d4-c4",true,true);
		assertGameState("rnbqkbnr/8/8/8/2K5/8/PPPPPPPP/RNBQKBNR/",false,false,false);
		
		startGame("rnbqkbnr/8/8/8/3K4/8/PPPPPPPP/RNBQKBNR/",true);
		assertMove("d4-e4",true,true);
		assertGameState("rnbqkbnr/8/8/8/4K3/8/PPPPPPPP/RNBQKBNR/",false,false,false);
		
		startGame("rnbqkbnr/8/8/8/3K4/8/PPPPPPPP/RNBQ1BNR/",true);
		assertMove("d4-c5",true,false);
		assertGameState("rnbqkbnr/8/8/8/3K4/8/PPPPPPPP/RNBQ1BNR/",true,false,false);
		
		startGame("rnbqkbnr/8/8/8/3K4/8/PPPPPPPP/RNBQ1BNR/",true);
		assertMove("d4-d5",true,false);
		assertGameState("rnbqkbnr/8/8/8/3K4/8/PPPPPPPP/RNBQ1BNR/",true,false,false);
		
		startGame("rnbqkbnr/8/8/8/3K4/8/PPPPPPPP/RNBQKBNR/",true);
		assertMove("d4-e5",true,true);
		assertGameState("rnbqkbnr/8/8/4K3/8/8/PPPPPPPP/RNBQKBNR/",false,false,false);
		
		startGame("rnbq1bnr/8/8/8/3k4/8/8/RNBQKBNR/",false);
		assertMove("d4-c3",false,false);
		assertGameState("rnbq1bnr/8/8/8/3k4/8/8/RNBQKBNR/",false,false,false);
		
		startGame("rnbq1bnr/8/8/8/3k4/8/8/RNBQKBNR/",false);
		assertMove("d4-d3",false,false);
		assertGameState("rnbq1bnr/8/8/8/3k4/8/8/RNBQKBNR/",false,false,false);
		
		startGame("rnbq1bnr/8/8/8/3k4/8/8/RNBQKBNR/",false);
		assertMove("d4-e3",false,false);
		assertGameState("rnbq1bnr/8/8/8/3k4/8/8/RNBQKBNR/",false,false,false);
		 
		startGame("rnbq1bnr/8/8/8/3k4/8/8/RNBQKBNR/",false);
		assertMove("d4-c4",false,false);
		assertGameState("rnbq1bnr/8/8/8/3k4/8/8/RNBQKBNR/",false,false,false);
		
		startGame("rnbq1bnr/8/8/8/3k4/8/8/RNBQKBNR/",false);
		assertMove("d4-e4",false,true);
		assertGameState("rnbq1bnr/8/8/8/4k3/8/8/RNBQKBNR/",true,false,false);
		
		startGame("rnbq1bnr/8/8/8/3k4/8/8/RNBQKBNR/",false);
		assertMove("d4-c5",false,true);
		assertGameState("rnbq1bnr/8/8/2k5/8/8/8/RNBQKBNR/",true,false,false);
		
		startGame("rnbq1bnr/8/8/8/3k4/8/8/RNBQKBNR/",false);
		assertMove("d4-d5",false,false);
		assertGameState("rnbq1bnr/8/8/8/3k4/8/8/RNBQKBNR/",false,false,false);
		
		startGame("rnbq1bnr/8/8/8/3k4/8/8/RNBQKBNR/",false);
		assertMove("d4-e5",false,true);
		assertGameState("rnbq1bnr/8/8/4k3/8/8/8/RNBQKBNR/",true,false,false);
		
	}
	
	@Test
	public void bishopTest() {
		
		//basic moves up left
		startGame("1nbqkbnr/8/8/8/8/8/8/RNBQKBNB/",true);
		assertMove("h1-a8",true,true);
		assertGameState("Bnbqkbnr/8/8/8/8/8/8/RNBQKBN1/",false,false,false);
		
		startGame("1nbqkbnr/1P6/8/8/8/8/8/RNBQKBNB/",true);
		assertMove("h1-a8",true,false);
		assertGameState("1nbqkbnr/1P6/8/8/8/8/8/RNBQKBNB/",true,false,false);
		
		startGame("rnbqkbnr/8/8/8/8/8/8/RNBQKBNB/",true);
		assertMove("h1-a8",true,true);
		assertGameState("Bnbqkbnr/8/8/8/8/8/8/RNBQKBN1/R",false,false,false);
		
		startGame("1nbqkbnr/8/8/8/8/8/8/RNBQKBNb/",false);
		assertMove("h1-a8",false,true);
		assertGameState("bnbqkbnr/8/8/8/8/8/8/RNBQKBN1/",true,false,false);
		
		startGame("1nbqkbnr/1P6/8/8/8/8/8/RNBQKBNb/",false);
		assertMove("h1-a8",false,false);
		assertGameState("1nbqkbnr/1P6/8/8/8/8/8/RNBQKBNb/",false,false,false);
		
		startGame("Rnbqkbnr/8/8/8/8/8/8/RNBQKBNb/",false);
		assertMove("h1-a8",false,true);
		assertGameState("bnbqkbnr/8/8/8/8/8/8/RNBQKBN1/r",true,false,false);
		
		//basic moves up right
		startGame("1nbqkbn1/8/8/8/8/8/8/BNBQKBNB/",true);
		assertMove("a1-h8",true,true);
		assertGameState("1nbqkbnB/8/8/8/8/8/8/1NBQKBNB/",false,false,false);
		
		startGame("1nbqkbn1/6P1/8/8/8/8/8/BNBQKBNB/",true);
		assertMove("a1-h8",true,false);
		assertGameState("1nbqkbn1/6P1/8/8/8/8/8/BNBQKBNB/",true,false,false);
		
		startGame("1nbqkbnr/8/8/8/8/8/8/BNBQKBNB/",true);
		assertMove("a1-h8",true,true);
		assertGameState("1nbqkbnB/8/8/8/8/8/8/1NBQKBNB/R",false,false,false);
		
		startGame("1nbqkbn1/8/8/8/8/8/8/bNBQKBNB/",false);
		assertMove("a1-h8",false,true);
		assertGameState("1nbqkbnb/8/8/8/8/8/8/1NBQKBNB/",true,false,false);
		
		startGame("1nbqkbn1/6P1/8/8/8/8/8/bNBQKBNB/",false);
		assertMove("a1-h8",false,false);
		assertGameState("1nbqkbn1/6P1/8/8/8/8/8/bNBQKBNB/",false,false,false);
		
		startGame("1nbqkbnR/8/8/8/8/8/8/bNBQKBNB/",false);
		assertMove("a1-h8",false,true);
		assertGameState("1nbqkbnb/8/8/8/8/8/8/1NBQKBNB/r",true,false,false);
		
		//basic moves down left
		startGame("1nbqkbnB/8/8/8/8/8/8/1NBQKBNB/",true);
		assertMove("h8-a1",true,true);
		assertGameState("1nbqkbn1/8/8/8/8/8/8/BNBQKBNB/",false,false,false);
		
		startGame("1nbqkbnB/8/8/8/8/8/8/rNBQKBNB/",true);
		assertMove("h8-a1",true,true);
		assertGameState("1nbqkbn1/8/8/8/8/8/8/BNBQKBNB/R",false,false,false);
		
		startGame("1nbqkbnB/8/8/8/8/8/1P6/1NBQKBNB/",true);
		assertMove("h8-a1",true,false);
		assertGameState("1nbqkbnB/8/8/8/8/8/1P6/1NBQKBNB/",true,false,false);
		
		startGame("1nbqkbnb/8/8/8/8/8/8/1NBQKBNB/",false);
		assertMove("h8-a1",false,true);
		assertGameState("1nbqkbn1/8/8/8/8/8/8/bNBQKBNB/",true,false,false);
		
		startGame("1nbqkbnb/8/8/8/8/8/8/RNBQKBNB/",false);
		assertMove("h8-a1",false,true);
		assertGameState("1nbqkbn1/8/8/8/8/8/8/bNBQKBNB/r",true,false,false);
		
		startGame("1nbqkbnb/8/8/8/8/8/1P6/1NBQKBNB/",false);
		assertMove("h8-a1",false,false);
		assertGameState("1nbqkbnb/8/8/8/8/8/1P6/1NBQKBNB/",false,false,false);
		
		//basic moves down right
		startGame("Bnbqkbnr/8/8/8/8/8/8/RNBQKBN1/",true);
		assertMove("a8-h1",true,true);
		assertGameState("1nbqkbnr/8/8/8/8/8/8/RNBQKBNB/",false,false,false);
		
		startGame("Bnbqkbnr/8/8/8/8/8/8/RNBQKBNr/",true);
		assertMove("a8-h1",true,true);
		assertGameState("1nbqkbnr/8/8/8/8/8/8/RNBQKBNB/R",false,false,false);
		
		startGame("Bnbqkbnr/8/8/8/8/8/6P1/RNBQKBN1/",true);
		assertMove("a8-h1",true,false);
		assertGameState("Bnbqkbnr/8/8/8/8/8/6P1/RNBQKBN1/",true,false,false);
		
		startGame("bnbqkbnr/8/8/8/8/8/8/RNBQKBN1/",false);
		assertMove("a8-h1",false,true);
		assertGameState("1nbqkbnr/8/8/8/8/8/8/RNBQKBNb/",true,false,false);
		
		startGame("bnbqkbnr/8/8/8/8/8/8/RNBQKBNR/",false);
		assertMove("a8-h1",false,true);
		assertGameState("1nbqkbnr/8/8/8/8/8/8/RNBQKBNb/r",true,false,false);
		
		startGame("bnbqkbnr/8/8/8/8/8/6P1/RNBQKBN1/",false);
		assertMove("a8-h1",false,false);
		assertGameState("bnbqkbnr/8/8/8/8/8/6P1/RNBQKBN1/",false,false,false);
		
		
	}
	
	@Test
	public void checkTest() {
		startGame("KR6/8/8/8/8/8/8/k7/",false);
		assertMove("a1-b1",false,false);
		assertGameState("KR6/8/8/8/8/8/8/k7/",false,false,false);
		
		startGame("8/8/8/8/5k1K/8/8/8/",false);
		assertMove("e4-f5",false,false);
		assertGameState("8/8/8/8/5k1K/8/8/8/",false,false,false);
		
		startGame("BK6/8/8/8/8/8/8/6k1/",false);
		assertMove("g1-h1",false,false);
		assertGameState("BK6/8/8/8/8/8/8/6k1/",false,false,false);
		
		startGame("QK6/8/8/8/8/8/8/6k1/",false);
		assertMove("g1-h1",false,false);
		assertGameState("QK6/8/8/8/8/8/8/6k1/",false,false,false);
		
		startGame("6KB/8/8/8/8/8/8/1k6/",false);
		assertMove("b1-a1",false,false);
		assertGameState("6KB/8/8/8/8/8/8/1k6/",false,false,false);
		
		startGame("6KQ/8/8/8/8/8/8/1k6/",false);
		assertMove("b1-a1",false,false);
		assertGameState("6KQ/8/8/8/8/8/8/1k6/",false,false,false);
		
		startGame("2K5/8/1k6/8/8/8/8/8/",false);
		assertMove("b6-b7",false,false);
		assertGameState("2K5/8/1k6/8/8/8/8/8/",false,false,false);
		
		startGame("1N6/8/1k6/8/8/8/8/7K/",false);
		assertMove("b6-c6",false,false);
		assertGameState("1N6/8/1k6/8/8/8/8/7K/",false,false,false);
		
		startGame("3n4/K7/8/8/8/8/8/7k/",true);
		assertMove("a7-b7",true,false);
		assertGameState("3n4/K7/8/8/8/8/8/7k/",true,false,false);
		
		startGame("6K1/8/8/8/8/8/8/q6k/",true);
		assertMove("g8-h8",true,false);
		assertGameState("6K1/8/8/8/8/8/8/q6k/",true,false,false);
		
		startGame("6K1/8/8/8/8/8/8/q6k/",true);
		assertMove("g8-h8",true,false);
		assertGameState("6K1/8/8/8/8/8/8/q6k/",true,false,false);
		
		startGame("1K6/8/8/8/8/8/8/k6q/",true);
		assertMove("b8-a8",true,false);
		assertGameState("1K6/8/8/8/8/8/8/k6q/",true,false,false);
		
		startGame("7k/8/8/8/8/2n5/8/6K1/",true);
		assertMove("a1-a2",true,false);
		assertGameState("7k/8/8/8/8/2n5/8/6K1/",true,false,false);
		
		startGame("k5K1/8/8/8/8/2n5/8/8/",true);
		assertMove("g8-h8",true,true);
		assertGameState("k6K/8/8/8/8/2n5/8/8/",false,false,false);
		
		startGame("k5K1/5n2/8/8/8/2n5/8/8/",true);
		assertMove("g8-h8",true,false);
		assertGameState("k5K1/5n2/8/8/8/2n5/8/8/",true,false,false);
		
		startGame("k5K1/8/6n1/8/8/2n5/8/8/",true);
		assertMove("g8-h8",true,false);
		assertGameState("k5K1/8/6n1/8/8/2n5/8/8/",true,false,false);
		
		startGame("K7/1k6/8/8/8/8/8/8/",true);
		assertMove("a8-b8",true,false);
		assertGameState("K7/1k6/8/8/8/8/8/8/",true,false,false);
		
		startGame("K7/8/8/8/8/8/8/kq6/",true);
		assertMove("a8-b8",true,false);
		assertGameState("K7/8/8/8/8/8/8/kq6/",true,false,false);
		
		startGame("K7/8/8/8/8/8/8/kr6/",true);
		assertMove("a8-b8",true,false);
		assertGameState("K7/8/8/8/8/8/8/kr6/",true,false,false);
		
		startGame("k7/8/8/8/8/8/7q/K7/",true);
		assertMove("a1-a2",true,false);
		assertGameState("k7/8/8/8/8/8/7q/K7/",true,false,false);
		
		startGame("k7/8/8/8/8/8/7r/K7/",true);
		assertMove("a1-a2",true,false);
		assertGameState("k7/8/8/8/8/8/7r/K7/",true,false,false);
		
		
	}
	//mate scenarios for testing from: https://thechessworld.com/articles/training-techniques/13-checkmates-you-must-know/
	@Test
	public void mateTest() {
		startGame("7k/1R6/R7/8/8/8/8/3K4/",false);
		assertMove("a6-a8",true,false);
		assertGameState("7k/1R6/R7/8/8/8/8/3K4/",false,false,false);
		
		startGame("7k/1R6/R7/8/8/8/8/3K4/",true);
		assertMove("a6-a8",true,true);
		assertGameState("R6k/1R6/8/8/8/8/8/3K4/",true,true,true);
		
		startGame("7k/1R6/8/8/8/8/8/3K4/R",true);
		assertMove("R-a8",true,false);
		assertGameState("7k/1R6/8/8/8/8/8/3K4/R",true,true,true);
		
				
	}
	
	@Test public void mateTest2() {
		
		startGame("7k/1R6/8/8/8/8/8/3K4/R",true);
		assertMove("R-a8",true,true);
		assertGameState("R6k/1R6/8/8/8/8/8/3K4/",true,true,true);
		
	}
	
	@Test
	public void mateTest3() {
		startGame("7K/1r6/r7/8/8/8/8/3k4/",false);
		assertMove("a6-a8",false,true);
		assertGameState("r6K/1r6/8/8/8/8/8/3k4/",true,true,false);
				
	}
	
	@Test
	public void mateTest4() {
		startGame("7K/1r6/8/8/8/8/8/3k4/r",false);
		assertMove("r-a8",false,true);
		assertGameState("r6K/1r6/8/8/8/8/8/3k4/",true,true,false);
	}
	
	@Test
	public void mateTest5() {
		startGame("4k3/4P3/3PK3/8/8/8/8/8/",true);	
		assertMove("d6-d7",true,true);
		assertGameState("4k3/3PP3/4K3/8/8/8/8/8/",true,true,true);
	}
	
	@Test
	public void mateTest6() {
		startGame("6k1/5ppp/6r1/8/8/7P/5PP1/R5K1/",true);	
		assertMove("a1-a8",true,true);
		assertGameState("R5k1/5ppp/6r1/8/8/7P/5PP1/6K1/",true,true,true);
	}
	
	@Test
	public void mateTest7() {
		startGame("r4rk1/ppp2ppp/8/8/8/1P6/PQ3PPP/B4RK1/",true);	
		assertMove("b2-g7",true,true);
		assertGameState("r4rk1/ppp2pQp/8/8/8/1P6/P4PPP/B4RK1/P",true,true,true);
	}
	
	@Test
	public void mateTest8() {
		startGame("r5rk/ppp3pp/8/4N3/8/1P6/P4PPP/5RK1/",true);	
		assertMove("e5-f7",true,true);
		assertGameState("r5rk/ppp2Npp/8/8/8/1P6/P4PPP/5RK1/",true,true,true);
	}
	
	@Test
	public void mateTest9() {
		startGame("r4rk1/ppp2p1p/5Bp1/8/6N1/1P6/P4PPP/5RK1/",true);	
		assertMove("g4-h6",true,true);
		assertGameState("r4rk1/ppp2p1p/5BpN/8/8/1P6/P4PPP/5RK1/",false,true,true);
	}
	
	@Test
	public void mateTest10() {
		startGame("r4r2/ppp1Nppk/8/3R4/8/1P6/P4PPP/6K1/",true);	
		assertMove("d5-h5",true,true);
		assertGameState("r4r2/ppp1Nppk/8/7R/8/1P6/P4PPP/6K1/",false,true,true);
	}
	
	@Test
	public void mateTest11() {
		startGame("7k/p6p/1p6/8/8/B7/B4K2/8/",true);	
		assertMove("a3-b2",true,true);
		assertGameState("7k/p6p/1p6/8/8/8/BB3K2/8/",false,true,true);
	}
	
	@Test
	public void mateTest12() {
		startGame("r4rk1/p4ppp/1p5B/8/8/6Q1/P4PPP/5RK1/",true);	
		assertMove("g3-g7",true,true);
		assertGameState("r4rk1/p4pQp/1p5B/8/8/8/P4PPP/5RK1/P",false,true,true);
	}
	
	@Test
	public void mateTest13() {
		startGame("r3r1k1/p4p1p/1p3QpB/8/8/8/P4PPP/5RK1/",true);	
		assertMove("f6-g7",true,true);
		assertGameState("r3r1k1/p4pQp/1p4pB/8/8/8/P4PPP/5RK1/",false,true,true);
	}
	
	@Test
	public void mateTest14() {
		startGame("r4rk1/p4ppp1/1p6/2q5/7Q/6P1/P4PK1/R6R/",true);	
		assertMove("h4-h8",true,true);
		assertGameState("r4rkQ/p4pp1/1p6/2q5/8/6P1/P4PK1/R6R/",false,true,true);
	}
	
	@Test
	public void mateTest15() {
		startGame("r4rk1/p4ppp1/1p6/2q5/7Q/6P1/P4PK1/R6R/",true);	
		assertMove("h4-h7",true,true);
		assertGameState("r4rk1/p4ppQ/1p6/2q5/8/6P1/P4PK1/R6R/",false,true,true);
	}
	
	@Test
	public void mateTest16() {
		startGame("7k/Q7/6K1/8/1p6/p7/8/8/",true);	
		assertMove("a7-h7",true,true);
		assertGameState("7k/7Q/6K1/8/1p6/p7/8/8/",false,true,true);
	}
	
	@Test
	public void mateTest17() {
		startGame("7k/Q7/6K1/8/1p6/p7/8/8/",true);	
		assertMove("a7-g7",true,true);
		assertGameState("7k/6Q1/6K1/8/1p6/p7/8/8/",false,true,true);
	}
	
	@Test
	public void mateTest18() {
		startGame("7k/Q7/6K1/8/1p6/p7/8/8/",true);	
		assertMove("a7-a8",true,true);
		assertGameState("Q6k/8/6K1/8/1p6/p7/8/8/",false,true,true);
	}
	
	@Test
	public void mateTest19() {
		startGame("7k/Q7/6K1/8/1p6/p7/8/8/",true);	
		assertMove("a7-b8",true,true);
		assertGameState("1Q5k/8/6K1/8/1p6/p7/8/8/",false,true,true);
	}
	
	@Test
	public void mateTest20() {
		startGame("1r5k/1p5p/p7/8/8/B7/8/4K1R1/",true);	
		assertMove("a3-b2",true,true);
		assertGameState("1r5k/1p5p/p7/8/8/8/1B6/4K1R1/",false,true,true);
	}
}
