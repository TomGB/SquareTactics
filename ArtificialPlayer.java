import java.util.ArrayList;
import java.util.Collections;

class ArtificialPlayer {
	Tafl game;

	public ArtificialPlayer(Tafl _game){
		game = _game;
	}

	public void takeTurn(){
		ArrayList<Piece> myPieces = game.board.getByColor('b');

		Collections.shuffle(myPieces);

		for (Piece piece : myPieces) {
			game.update(piece.locX,piece.locY);
			if(game.possibleMoves.size()>0){

				Collections.shuffle(game.possibleMoves);

				BoardMoves move = game.possibleMoves.get(0);
				game.update(move.x,move.y);
			}
		}

	}
}