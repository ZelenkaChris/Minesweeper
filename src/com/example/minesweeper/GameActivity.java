package com.example.minesweeper;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GameActivity extends Activity {

	private static int ROW = 9;
	private static int COL = 9;
	
	private static int BLOCKSIZE = 40;

	private int mines = 10;

	int total;

	int time = 0;

	Block block[][];
	TextView tgrid[][];

	boolean inUse[][];
	boolean isFlagged[][];
	int surrounding[][] = new int[COL][ROW];

	boolean isHappy = true;
	boolean gameOver = false;
	boolean isFlag = false;

	int count = 0;

	Timer timer = new Timer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		total = ROW * COL - mines;
		System.out.print(total);
		FillGame();
		PrintBoard();

		timer.schedule(new GameTimerTask(), 0, 1000);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		newBoard();
		
	}

	class GameTimerTask extends TimerTask {
		@Override
		public void run() {
			if (!gameOver)
				time++;
		}
	}
	
	public void faceClick(View view)
	{
		clearBoard();
		gameOver = false;
		((Button)findViewById(R.id.Face)).setText("H");
	}
	
	public void clearBoard()
	{
		FillGame();
		
		for (int i = 0; i < COL; i++) {
			for (int j = 0; j < ROW; j++){
				block[i][j].setVisibility(View.VISIBLE);
				tgrid[i][j].setVisibility(View.INVISIBLE);
				tgrid[i][j].setText(Integer.toString(surrounding[i][j]));				
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}

	public void FillGame() {
		FillBoard();
		FillSurround();
	}

	public void newBoard() {
		Block b;
		TextView tv;

		int xmargin = 0, ymargin = 0;
		
		RelativeLayout gv = (RelativeLayout) findViewById(R.id.grid);
		RelativeLayout.LayoutParams rel_b;
		
		block = new Block[COL][ROW];
		tgrid = new TextView[COL][ROW];
		
		for (int i = 0; i < COL; i++) {
			for (int j = 0; j < ROW; j++) {
								
				rel_b = new RelativeLayout.LayoutParams(BLOCKSIZE, BLOCKSIZE);
				rel_b.leftMargin = xmargin;
				rel_b.topMargin = ymargin;

				b = new Block(this);
				b.setId(count);
				b.setLayoutParams(rel_b);
				b.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Block b = (Block) v;
						
						if(gameOver || isFlagged[b.getyPos()][b.getxPos()])
							return;
						
						if(inUse[b.getyPos()][b.getxPos()])
						{
							System.out.println("Boom");
							gameOver = true;
							Button b1 = (Button) (findViewById(R.id.Face));
							b1.setText("S");
							return;							
						}
						block[b.getyPos()][b.getxPos()].setVisibility(View.INVISIBLE);
						tgrid[b.getyPos()][b.getxPos()].setVisibility(View.VISIBLE);
						
						count++;
						
						if(count == total)
						{
							//Win
							gameOver=true;
						}
						
					}
				});

				gv.addView(b);				

				tv = new TextView(this);
				tv.setLayoutParams(rel_b);
				tv.setVisibility(4);
				
				tv.setText(Integer.toString(surrounding[i][j]));
				tv.setGravity(Gravity.CENTER);
				gv.addView(tv);
				
				b.setxPos(j);
				b.setyPos(i);
				
				block[i][j] = b;
				tgrid[i][j] = tv;
				
				xmargin += BLOCKSIZE;
			}
			ymargin += BLOCKSIZE;
			xmargin = 0;
		}
		
		int bSize = gv.getMeasuredWidth() / COL;
		
		System.out.println(bSize);
		
		
	}

	public void FillBoard() {
		int i = 0;
		int colRand, rowRand;
		Random rand = new Random();
		
		inUse = new boolean[COL][ROW];
		isFlagged = new boolean[COL][ROW];
		
		count = 0;

		while (i < mines) {
			colRand = rand.nextInt(COL);
			rowRand = rand.nextInt(ROW);

			if (!inUse[colRand][rowRand])
				inUse[colRand][rowRand] = true;
			else
				continue;

			i++;
		}

	}

	public int CheckSurround(int r, int c) {
		int count = 0;

		if (r - 1 >= 0) {
			if (inUse[c][r - 1])
				count++;

			if (c - 1 >= 0)
				if (inUse[c - 1][r - 1])
					count++;
			if (c + 1 < COL)
				if (inUse[c + 1][r - 1])
					count++;
		}

		if (r + 1 < ROW) {
			if (inUse[c][r + 1])
				count++;

			if (c - 1 >= 0)
				if (inUse[c - 1][r + 1])
					count++;
			if (c + 1 < COL)
				if (inUse[c + 1][r + 1])
					count++;
		}

		if (c - 1 >= 0)
			if (inUse[c - 1][r])
				count++;

		if (c + 1 < COL)
			if (inUse[c + 1][r])
				count++;

		return count;
	}

	public void FillSurround() {

		for (int i = 0; i < COL; i++) {
			for (int j = 0; j < ROW; j++) {
				surrounding[i][j] = CheckSurround(j, i);
			}
		}
	}

	public void PrintBoard() {
		for (int i = 0; i < COL; i++) {
			for (int j = 0; j < ROW; j++) {
				System.out.print(surrounding[j][i] + " ");
			}
			System.out.println();
		}

		int test;
		System.out.println();

		for (int i = 0; i < COL; i++) {
			for (int j = 0; j < ROW; j++) {
				if (inUse[j][i])
					test = 1;
				else
					test = 0;
				System.out.print(test + " ");
			}
			System.out.println();
		}
	}
}
