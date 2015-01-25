import java.awt.*;
import java.awt.event.*;
/*---------------------------------------------------------------
 *  Monitor
 *---------------------------------------------------------------*/
public class Monitor extends Frame
						implements Runnable{

	private static final int		WIDTH = 630;			/* デフォルトのX方向の大きさ */
	private static final int		HEIGHT = 450;			/* デフォルトのY方向の大きさ */

	private volatile Thread			_blinker = null;	/* Monitorオブジェクトのスレッド */

	/*
	 *  AWTオブジェクト
	 */
	private	Board					_board;				/* ゲーム盤 */
	private BoardGUI				_graphicBoard;		/* ゲーム盤のGUI */
	private Label					_whitePieces;		/* 白ピース数の表示用 */
	private Label					_blackPieces;		/* 黒ピース数の表示用 */
	private Label					_Message;			/* メッセージの表示用 */

	private boolean					_lock = false;

	/*
	 * コンストラクタ
	 */
	public Monitor(Board board){
		super("オセロ  Monitor");

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
			System.exit(0);
			}
		});

		_board = board;

		this.setBackground(Color.lightGray);
		this.setResizable(false);
		this.setLayout(null);
		this.setSize(WIDTH,HEIGHT);

		_graphicBoard = new BoardGUI(board);
		_graphicBoard.setLocation(5,25);
		this.add(_graphicBoard);

		_blackPieces = new Label("先手：黒  2個",Label.CENTER);
		_blackPieces.setFont(new Font("Dialog",Font.PLAIN,16));
		_blackPieces.setSize(200,20);
		_blackPieces.setLocation(425,70);
		this.add(_blackPieces);

		// 後手：白のピース数
		_whitePieces = new Label("後手：白  2個",Label.CENTER);
		_whitePieces.setFont(new Font("Dialog",Font.PLAIN,16));
		_whitePieces.setSize(200,20);
		_whitePieces.setLocation(425,90);
		this.add(_whitePieces);

		_Message = new Label("This is test ....",Label.CENTER);
		_Message.setFont(new Font("Dialog",Font.PLAIN,16));
		_Message.setSize(200,20);
		_Message.setLocation(425,120);
		this.add(_Message);

		this.repaint();
		this.setVisible(true);
	}

	/**
	 *  再描画
	 */
	public void paint(Graphics g){
		_graphicBoard.repaint();

		int blackPieces = _board.getPoints(Piece.Black);
		if(blackPieces < 10){
			_blackPieces.setText("先手：黒  "+ Integer.toString(blackPieces) +"個");
		}else{
			_blackPieces.setText("先手：黒 "+ Integer.toString(blackPieces) +"個");
		}
		int whitePieces = _board.getPoints(Piece.White);
		if(whitePieces < 10){
			_whitePieces.setText("後手：白  "+ Integer.toString(whitePieces) +"個");
		}else{
			_whitePieces.setText("後手：白 "+ Integer.toString(whitePieces) +"個");
		}
	}

	/**
	 *  再描画
	 */
	public void update(Graphics g){
		paint(g);
	}

	/**
	 *  スレッド起動
	 */
	public void start(){
		if(_blinker == null){
			_blinker = new Thread(this);
			_blinker.start();
		}
	}

	/**
	 *  スレッド停止
	 */
	public void stop() {
		_blinker = null;
	}

	/**
	 *  ダブルバッファを用いて0.5秒毎に画面更新
	 */
	public void run(){
		Thread thisThread = Thread.currentThread();
		while(_blinker == thisThread){
			try{
				thisThread.sleep(500);
			}catch(InterruptedException e){}
			repaint();
		}
	}

	/**
	 *  マウスイベントのリスナーを設定
	 */
	public void addMouseListener(MouseListener mouseListener){
		_graphicBoard.addMouseListener(mouseListener);
	}

	/**
	 *  メッセージを変更
	 */
	public void setMessage(String message){
		if(_lock == false){
			_Message.setText(message);
		}
	}

	/**
	 *  メッセージをロック
	 */
	public void lock(){
		_lock = true;
	}

}
