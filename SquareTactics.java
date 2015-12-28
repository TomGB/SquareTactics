import java.io.PrintWriter;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

class SquareTactics {

	UserInteraction myGUI;

	int boardWidth = 8, boardHeight = 8;
	Board board = new Board(boardWidth,boardHeight);

	boolean whiteTurn=true, rules=false, checkMate = false;
	boolean debug=false, moveDebug = false;
	String current_stage = "main menu";
	// ArtificialPlayer ai;

	ArrayList<BoardMoves> pinningKing = new ArrayList<BoardMoves>();
	ArrayList<BoardMoves> pinningTemp = new ArrayList<BoardMoves>();
	ArrayList<BoardMoves> possibleMoves = new ArrayList<BoardMoves>();

	Piece selectedPiece = null;
	Piece hoverPiece = null;
	int selX, selY;

	public SquareTactics(){
		// ai = new ArtificialPlayer(this);
		board.clear();
		board.setUp();
		myGUI = new UserInteraction(this);
	}

	public void reset(){
		board.clear();
		selectedPiece = null;
		pinningKing.clear();
		possibleMoves = null;
		board.setUp();
		whiteTurn=true;
		myGUI.repaint();
	}

	/*
	This method is called whenever the mouse is clicked,
	If no piece is selected then allow the selection of a piece that is the color of the current person's turn.
	If a piece is selected then the destination click is checked using the validMove method.
	Then a check for check and checkMate is carried out.
	Finally the GUI is re drawn.
	 */
	public void update(int posX, int posY){ //if left button clicked
		if(rules){
			rules=false;
		}else{
			if(checkMate){ //reset if game has ended
				whiteTurn=true;
				checkMate=false;
				reset();
			}
			Piece tempPiece = board.get(posX,posY);
			if(tempPiece!=null&&(tempPiece.color==(whiteTurn?'w':'b')||debug)){
				selX=posX;
				selY=posY;
				selectedPiece=tempPiece;
				possibleMoves = calculatePieceMoves(selectedPiece, selX, selY, false);
				simulatePieceMoves(whiteTurn?'b':'w');
			}else if(selectedPiece != null){
				if(validMove(posX, posY)||debug){ //move piece
					selectedPiece.hasMoved = true;
					if(board.get(posX,posY)!=null){
						myGUI.playSound();
					}
					board.turnNum++;
					board.saveHistory();
					board.set(posX,posY,selectedPiece);
					board.set(selX,selY,null);
					if(checkCheck(false,whiteTurn?'w':'b')){
						p("king in check");
						if(checkCheckMate(whiteTurn?'b':'w')){
							p("Check Mate!");
							checkMate=true;
						}
					}
					selectedPiece = null;
					whiteTurn = !whiteTurn;
					// if(!whiteTurn){
					// 	ai.takeTurn();
					// }
				}
			}
		}
		myGUI.repaint();
	}

	/*
	This method uses the possibleMoves array list.
	It simulates moving the selected piece to each space it can usually move to.
	Then checkCheck is run to see if the movement of this piece has cleared a path
	for the King to be taken. If a path has been cleared then this is an invalid move
	and it is removed from the possibleMoves array.
	 */
	public void simulatePieceMoves(char color){
		for (int i=possibleMoves.size()-1; i>=0; i--) {
			board.turnNum++;
			board.saveHistory();
			board.set(possibleMoves.get(i).x,possibleMoves.get(i).y,selectedPiece);
			board.set(selX,selY,null);
			if(checkCheck(true,color)){
				possibleMoves.remove(i);
				// p("invalid move as the king could be captured");
			}
			board.loadHistory();
		}
	}

	/*
	Input a position x and y that you wish to move your piece to.
	This method returns if this move is in the possibleMoves array list.
	 */
	public boolean validMove(int x, int y){
		for (BoardMoves move : possibleMoves) {
			if(move.x==x&&move.y==y){
				return true;
			}
		}
		return false;
	}

	/*
	This method uses the pinningTemp and pinningKing array lists.
	For each piece calculate it's possible moves using calculatePieceMoves method.
	If there are no pieces pinning the king return false.
	 */
	public boolean checkCheck(boolean simulated, char color){
		if(simulated){
			pinningTemp.clear();
		}else{
			pinningKing.clear();
		}

		for (Piece piece : board.getByColor(color)){
			calculatePieceMoves(piece, piece.locX, piece.locY, simulated);
		}

		if(simulated){
			return pinningTemp.size()!=0;
		}else{
			return pinningKing.size()!=0;
		}
	}

	/*
	This method utilizes the calcualtePieceMoves and the simulatePieceMoves methods.
	It runs each of these methods to get the list of possibeMoves for each piece.
	Then if there is no single legal move that a piece can take the game must be Checkmate.
	 */
	public boolean checkCheckMate(char color){

		for (Piece piece : board.getByColor(color)){
			selX=piece.locX;
			selY=piece.locY;
			selectedPiece=piece;
			possibleMoves = calculatePieceMoves(piece, piece.locX, piece.locY, false);
			simulatePieceMoves(whiteTurn?'w':'b');
			if(possibleMoves.size()>0){
				selectedPiece = null;
				possibleMoves = null;
				return false;
			}
		}
		return true;
	}

	/*
	This method returns an array list of the legal moves that a piece can make
	(not including moves that put their own king in check).
	For each of the moves that a piece can do follow the movement path and save valid moves to the tempMoves array list.
	If a piece could potentially capture the enemy king then add the location of this piece to the pinningKing array list.
	return the array list of possible moves the piece can do.
	 */
	public ArrayList<BoardMoves> calculatePieceMoves(Piece currentPiece, int x, int y, boolean simulated){
		ArrayList<BoardMoves> tempMoves = new ArrayList<BoardMoves>();
		for (Move move : currentPiece.moves) {

			boolean possibleSeccond = false;
			int i = 1;
			int tempX = x;
			int tempY = y;

			boolean endLoop = false;

			while(!endLoop){
				if(currentPiece.getColor()=='w'){
					tempX = tempX-move.y;
					tempY = tempY-move.x;
				}else{
					tempX = tempX+move.y;
					tempY = tempY+move.x;
				}

				if(currentPiece.wrapping){
					if(tempY >= 8){
						tempY = tempY-8;
					}else if(tempY < 0){
						tempY = tempY+8;
					}
				}

				if(move.moveType == "Jump" || move.moveType == "Step"){
					if(i>2){
						endLoop = true;
					}else if(i==2) {
						if (!possibleSeccond) {
							boolean not_moved_double_first_move_piece = (move.doubleFirst&&!currentPiece.hasMoved);
							if (!(not_moved_double_first_move_piece||currentPiece.doubleMove)) {
								endLoop = true;
							}
						}
					}
				}

				if(tempX>7||tempX<0||tempY>7||tempY<0){
					if(moveDebug){
						p("out of bounds");
					}
					endLoop=true;
				}

				if(!endLoop){

					if(board.get(tempX,tempY)==null){
						if(!move.canOnlyCapture()){
							tempMoves.add(new BoardMoves(tempX, tempY, move.moveType));
							if(moveDebug){
								p("empty space");
							}
							possibleSeccond = true;
						}
					}else if(board.get(tempX,tempY).getColor()==currentPiece.getColor()){
						endLoop=true;
						if(moveDebug){
							p("friendly");
						}
					}else if(!move.canOnlyMove()&&board.get(tempX,tempY).getColor()!=currentPiece.getColor()){
						tempMoves.add(new BoardMoves(tempX, tempY, move.moveType));
						endLoop=true;
						if(board.get(tempX,tempY).name=='k'){
							if(simulated){
								pinningTemp.add(new BoardMoves(x,y,"checkPiece"));
							}else{
								pinningKing.add(new BoardMoves(x,y,"checkPiece"));
								// p("added to pinning king array");
							}
						}
						if(moveDebug){
							p("enemy");
						}
					}
				}
				i++;
			}
		}
		return tempMoves;
	}

	public void undo(){
		if(board.turnNum>0){
			whiteTurn = !whiteTurn;
			board.loadHistory();
			selectedPiece = null;
			possibleMoves = null;
			pinningKing.clear();
			possibleMoves = null;
			myGUI.repaint();
		}
	}

	public void rules(){
		rules=!rules;
		myGUI.repaint();
	}

	public void save(){
		try{
			PrintWriter savefile= new PrintWriter(new File("Square_Tactics_save.txt"));
			savefile.println("Square Tactics save");
			savefile.println(whiteTurn?"White's Turn":"Black's Turn");
			for (int j=0; j<boardHeight; j++) {
				for (int i=0; i<boardWidth; i++) {
					savefile.print(board.get(i,j));
				}
			}
			savefile.flush();
			p("save done");
		}catch(Exception e){
			p("error saving");
		}
	}

	public void load(){
		// try(BufferedReader br = new BufferedReader(new FileReader("Square_Tactics_save.txt"))) {
		// 	br.readLine();
		// 	String turn = br.readLine();
	 //        String line = br.readLine();
	 //        for (int i=0; i<line.length(); i++) {
	 //        	board.set(i%boardWidth, i/boardHeight, line.charAt(i));
		// 	}
		// 	p(turn);
		// 	whiteTurn = (turn.equals("White's Turn"));
		// 	myGUI.repaint();
  //  		}catch(Exception e){
  //  			p("error reading file");
  //  		}
	}
	public static void p(Object o){System.out.println(o);}
	public static int r(int x){return (int)(Math.random()*x);}
	public static void main(String args[]){new SquareTactics();}
}
