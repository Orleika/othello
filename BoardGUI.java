import java.awt.*;
/*---------------------------------------------------------------
 *  BoardGUI
 *---------------------------------------------------------------*/
class BoardGUI extends Canvas{

	/*
	 *  defaultデータ
	 */
	private static final Dimension	BOARD_SIZE ;		/* ゲーム盤の大きさ */
	private static final int		MARGIN;				/* マージン */
	private static final Dimension	PIECE_SIZE;			/* 1マスの幅 */
	private static final Color		BOARD_COLOR;		/* ゲーム盤の色 */
	private static final Color		LINE_COLOR;			/* 線分の色 */
	private static final Color		WHITE_PIECE_COLOR;	/* 白ピースの色 */
	private static final Color		BLACK_PIECE_COLOR;	/* 黒ピースの色 */

	static{
		BOARD_SIZE = new Dimension(420,420);
		MARGIN = 10;
		PIECE_SIZE = new Dimension(50,50);
		BOARD_COLOR = new Color(38,173,38);
		LINE_COLOR  = new Color(0,0,0);
		WHITE_PIECE_COLOR = new Color(255,255,255);
		BLACK_PIECE_COLOR = new Color(0,0,0);
	}

	/*
	 *  BoardGUI オブジェクトの属性
	 */
	private Dimension	_boardSize = BOARD_SIZE;				/* ゲーム盤の大きさ */
	private int			_margin = MARGIN;						/* マージン */
	private Dimension	_pieceSize = PIECE_SIZE;				/* 1マスの大きさ */
	private Color		_boardColor = BOARD_COLOR;				/* ゲーム盤の色 */
	private Color		_lineColor  = LINE_COLOR;				/* 線分の色 */
	private Color		_whitePieceColor = WHITE_PIECE_COLOR;	/* 白ピースの色 */
	private Color		_blackPieceColor = BLACK_PIECE_COLOR;	/* 黒ピースの色 */

	private Board		_board = null;							/* 対応するゲーム盤オブジェクト */

	private Image		_bufferImage;							/* ダブルバッファ用Image */
	private Graphics	_bufferGraphics;						/* ダブルバッファ用Graphics */


	/**
	 *  <コンストラクタ>
	 *  ゲーム盤GUIオブジェクトを作成する
	 */
	public BoardGUI(Board board){
		_board = board;
		this.setSize(_boardSize.width,_boardSize.height);
		this.repaint();
	}

	/**
	 *  <コンストラクタ>
	 *  ゲーム盤GUIオブジェクトを作成する
	 */
	public BoardGUI(Board board,Dimension boardSize,int margin,Dimension pieceSize,
					Color boardColor,Color lineColor,Color whitePieceColor,Color blackPieceColor){
		_boardSize = boardSize;
		_margin = margin;
		_pieceSize = pieceSize;
		_boardColor = boardColor;
		_lineColor  = lineColor;
		_whitePieceColor = whitePieceColor;
		_blackPieceColor = blackPieceColor;
		_board = board;
		this.setSize(_boardSize.width,_boardSize.height);
		this.repaint();
	}

	/*
	 *  ゲーム盤の大きさを返す
	 */
	public Dimension getBoardSize(){
		return(_boardSize);
	}

	/*
	 *  マージンを返す
	 */
	public int getMargin(){
		return(_margin);
	}

	/*
	 *  1マスの大きさを返す
	 */
	public Dimension getPieceSize(){
		return(_pieceSize);
	}

	/*
	 *  ゲーム盤の色を返す
	 */
	public Color getBoardColor(){
		return(_boardColor);
	}

	/*
	 *  線分の色を返す
	 */
	public Color getLineColor(){
		return(_lineColor);
	}

	/*
	 *  白ピースの色を返す
	 */
	public Color getWhitePieceColor(){
		return(_whitePieceColor);
	}

	/*
	 *  黒ピースの色を返す
	 */
	public Color getBlackPieceColor(){
		return(_blackPieceColor);
	}

	/**
	 *  GUI上にゲーム盤、ピースのデータを書込む
	 */
	public void paint(Graphics g){
		if(_bufferImage == null){
			_bufferImage = createImage(_boardSize.width,_boardSize.height);
			_bufferGraphics = _bufferImage.getGraphics();
		}else{
			_bufferGraphics.setColor(_boardColor);
			_bufferGraphics.fillRect(0,0,8*_pieceSize.width+2*_margin,8*_pieceSize.height+2*_margin);
			_bufferGraphics.setColor(_lineColor);

			for(int i=0;i <= 8;i++){
				_bufferGraphics.drawLine(_margin, _pieceSize.height*i+_margin, 8*_pieceSize.width+_margin, _pieceSize.height*i+_margin);
				_bufferGraphics.drawLine(_pieceSize.width*i+_margin, _margin,_pieceSize.width*i+_margin, 8*_pieceSize.height+_margin);
			}

			for(int i=0;i < 8;i++){
				for(int j=0;j < 8;j++){
					Piece piece = _board.getPiece(i,j);
					if(piece != null){
						if( piece.getColor() == Piece.BLACK){
							_bufferGraphics.setColor(_blackPieceColor);
						}else{
							_bufferGraphics.setColor(_whitePieceColor);
						}
						_bufferGraphics.fillOval(_pieceSize.width*i+(int)(1.5*_margin),_pieceSize.height*j+(int)(1.5*_margin),_pieceSize.width-_margin+1,_pieceSize.height-_margin+1);
					}
				}
			}
		}
		g.drawImage(_bufferImage,0,0,this);
	}

	/*
	 *  再描画
	 */
	public void update(Graphics g){
		paint(g);
	}
}
