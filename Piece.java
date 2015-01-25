/*---------------------------------------------------------------
 *  Piece
 *---------------------------------------------------------------*/
public class Piece{

	/*--< クラス変数 >--*/
	public static final boolean WHITE = true;				/* 白色 */
	public static final boolean BLACK = false;				/* 黒色 */
	public static final Piece White = new Piece(WHITE);		/* 白色のピース */
	public static final Piece Black = new Piece(BLACK);		/* 黒色のピース */

	/*--< 属性 >--*/
	private boolean _state;									/* 現在の状態（色）*/

	/**
	 *  <コンストラクタ1>
	 *  初期状態を指定してピースを作る
	 *
	 * @param	boolean		init_stata			初期状態の色
	 */
	public Piece(boolean init_state){
		_state = init_state;
	}

	/**
	 *  <コンストラクタ2>
	 *  あるピースを指定して同じ色のピースを作る
	 *
	 * @param	Piece		piece				ピース
	 */
	public Piece(Piece piece){
		_state = piece.getColor();
	}

	/**
	 *  ピースをひっくり返す。
	 */
	public void reverse(){
		_state = !_state;
	}

	/**
	 *  ピースの色を返す。白ならPiece.WHITE、黒ならPiece.BLACKを返す
	 */
	public boolean getColor(){
		return(_state);
	}

	/**
	 *  指定されたピースと同色か調べる
	 *
	 * @param	Piece		piece		比較対象のピース
	 */
	public boolean equals(Piece piece){
		if(piece != null){
			if(this.getColor() == piece.getColor()){
				return(true);
			}
		}
		return(false);
	}
}
