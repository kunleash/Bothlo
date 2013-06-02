import org.newdawn.slick.Animation;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;


import java.awt.*;


import javax.swing.JOptionPane;

public class GameState extends BasicGameState {

	private TiledMap grassMap;
	private Animation Warrior , upW ,downW,leftW,rightW;
	private Enemy BothloE = new Enemy("Bothlo", 1, 120, 3, 1,10, 8, 240,192,1, 0.5,0.2);

	/*Enemy(String name, int attribute, int life, int movement, int range,
						int damage, int armor, int positionX, int positionY, int level, double lifeModifier,
						double damageModifier) */

	private Animation Bothlo ,downB;
	private Image inGameMenu;
	private boolean[][] traps;
	private boolean[][] warps;
	private boolean quit = false;	//bool for in-game menu
	private boolean attack = false; //bool for attack mode
	private boolean enemyTurn = false; //bool for enemy turn
	private boolean tookDamage = false; //bool for trap damage taken
	private boolean commitedAttack = false;
	private static final int SIZE =30;
	private int counter = 0;
	
	//for the music
	private Music music;

	private float x = 240f, y = 569f;

	private float k = 240f, l = 192f;

	private Point enemyPos = new Point((int)k,(int)l);

	private String[] maps = {"res/room2.tmx" , "res/room3.tmx" , "res/final room.tmx"};
	private int level = 0;


	private float heroMovement = 0;
	private int enemyMovement = 0;

	private StateBasedGame arg2;

	public GameState(int State){

	}
	@Override
	public void init(GameContainer gc, StateBasedGame arg1) throws SlickException {
		arg2 = arg1;

		inGameMenu = new Image("res/inGameMenu.png");

		//Game music
		music = new Music("res/Retribution.wav");
		music.setVolume(0.1f);
		music.loop();

		Image [] movementWarrior = {new Image("res/warrior.png"),new Image("res/warrior.png")};

		Image [] movementBothlo = {new Image("res/Bothlo.png"), new Image("res/Bothlo.png")};

		grassMap = new TiledMap(maps[level]);
		int [] duration = {1, 1};

		upW = new Animation(movementWarrior, duration, false);

		leftW = new Animation(movementWarrior, duration, false);

		rightW = new Animation(movementWarrior, duration, false);

		downW = new Animation(movementWarrior, duration, false);
		downB = new Animation(movementBothlo, duration, false);


		Warrior = downW;
		Bothlo = downB;

		warps = new boolean[grassMap.getWidth()][grassMap.getHeight()];
		traps = new boolean[grassMap.getWidth()][grassMap.getHeight()];

		grassMap.getLayerProperty(1, "trap", "true");

		for (int xAxis=0;xAxis<grassMap.getWidth(); xAxis++)
		{
			for (int yAxis=0;yAxis<grassMap.getHeight(); yAxis++)
			{
				int tileID = grassMap.getTileId(xAxis, yAxis,1);
				if (tileID!= 0){
					traps[xAxis][yAxis] = true;

				}

			}
		}
		for (int xAxis=0;xAxis<grassMap.getWidth(); xAxis++)
		{
			for (int yAxis=0;yAxis<grassMap.getHeight(); yAxis++)
			{
				int tileID = grassMap.getTileId(xAxis, yAxis,2);
				if (tileID!= 0){
					warps[xAxis][yAxis] = true;

				}

			}
		}



	}

	@Override
	public void render(GameContainer arg0, StateBasedGame sbg, Graphics g) throws SlickException {
		grassMap.render(0,0);
		Warrior.draw((int)x, (int)y);




		g.setColor(org.newdawn.slick.Color.cyan);
		g.drawString("HP:"+Hero.getHeroInstance().getLife(), x-7 , y-12);

		if(Hero.getHeroInstance().getLife()<=0){


			JOptionPane.showMessageDialog (null, "You failed your Quest!", "GAME OVER", JOptionPane.WARNING_MESSAGE);	
			NewGame(arg0,sbg,g);
			level=0;
			init(arg0, arg2);


		}

		if(BothloE.getLife()>0){
			Bothlo.draw((int)enemyPos.x, (int)enemyPos.y);
			g.setColor(org.newdawn.slick.Color.red);
			g.drawString("HP: "+BothloE.getLife(),enemyPos.x - 7, enemyPos.y-12);
		}

		//when they press escape
		if(quit==true){
			inGameMenu.drawCentered(250, 300);	
			if(quit==false){
				g.clear();
			}
		}
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		Input input = gc.getInput();
		float fdelta=delta*0.1f;
		float xdif= Math.abs(enemyPos.x-x);
		float ydif= Math.abs(enemyPos.y -y) ;

		//if quit is false move
		if(quit == false && attack == false && enemyTurn == false){ 						


			//checking maximum movement reached
			
			if(heroMovement < Hero.getHeroInstance().getMovement()*SIZE){
				
				if (input.isKeyPressed(Input.KEY_UP))
				{
					
					Warrior = upW;


					
					if(counter == 0 && level ==0){
						JOptionPane.showMessageDialog (null, "This once was a peacefull land but some evil has corrupted it!", "", JOptionPane.INFORMATION_MESSAGE);
						JOptionPane.showMessageDialog (null, "Oh no! \n It's the evil demon Bothlo!", "", JOptionPane.INFORMATION_MESSAGE);
						JOptionPane.showMessageDialog (null, "You have to take the demon back to it's lair where it can be damaged and defeat it!", "QUEST", JOptionPane.WARNING_MESSAGE);
						counter ++;
					}
					if(counter==1){
						attack=true;
					}
					
					if(y <0)
						y=0;

					
					if ((isTrap(x, y) || isTrap(x+SIZE-1, y)))
					{
						if(!tookDamage){
							Hero.getHeroInstance().changeLife(3);

							//System.out.println(Hero.getHeroInstance().getLife());
							tookDamage = true;
						}
					}

					if ((isWarp(x + SIZE , y) || isWarp(x + SIZE , y+SIZE-1)))
					{
						if(level==1){
							
							attack=true;
							input.clearControlPressedRecord();
							input.clearKeyPressedRecord();
							level++;
							init(gc, arg2);
							x=240f;
							y=569f;
							enemyPos.x= 240;
							enemyPos.y= 32;
							JOptionPane.showMessageDialog (null, "You made the demon follow you back in the volcano. Destroy it!", "", JOptionPane.INFORMATION_MESSAGE);
						}
						if(level==0){
							attack=true;
							level++;
							init(gc, arg2);
							x=240f;
							y=569f;
							enemyPos.x= 240;
							enemyPos.y= 32;
						}
						
					}
					
					
					// The lower the delta the slowest the sprite will animate.
					y -= SIZE;
					heroMovement += SIZE;
				}

				else if (input.isKeyPressed(Input.KEY_DOWN))
				{
					Warrior = downW;
					input.consumeEvent();
					
					//character limit on back movement
					if (y>569)
						y=569;

					

					if ((isTrap(x, y + SIZE ) || isTrap(x+SIZE-1, y + SIZE )))
					{
						if(!tookDamage){
							Hero.getHeroInstance().changeLife(3);

							tookDamage = true;
						}
					}
					if ((isWarp(x + SIZE, y) || isWarp(x + SIZE, y+SIZE-1)))
					{
						if(level==1){
							attack=true;
							level++;
							init(gc, arg2);
							x=240f;
							y=569f;
							enemyPos.x= 240;
							enemyPos.y= 32;
							JOptionPane.showMessageDialog (null, "You made the demon follow you back in the volcano. Destroy it!", "", JOptionPane.INFORMATION_MESSAGE);
						}
						if(level==0){
							attack=true;
							level++;
							init(gc, arg2);
							x=240f;
							y=569f;
							enemyPos.x= 240;
							enemyPos.y= 32;
						}
					}
					
					y += SIZE;
					heroMovement += SIZE;
				}
				else if (input.isKeyPressed(Input.KEY_LEFT))
				{
					Warrior = leftW;
					input.consumeEvent();

					//character limit on left movement
					if(x<0)
						x=0;

					

					
					

					if ((isTrap(x - fdelta, y) || isTrap(x - fdelta, y+SIZE-1)))
					{//Gia na min trws apeires fores damage apo pagida ston idio gyro
						if(!tookDamage){
							Hero.getHeroInstance().changeLife(3);

							tookDamage = true;
						}
					}
					if ((isWarp(x + SIZE, y) || isWarp(x + SIZE , y+SIZE-1)))
					{
						if(level==1){
							//gia na min kounietai oso yparxoun ta OptionPanes
							attack=true;
							//allagi pistas,ananewsi othonis, ananewsi topothesias xaraktirwn
							level++;
							init(gc, arg2);
							x=240f;
							y=569f;
							enemyPos.x= 240;
							enemyPos.y= 32;
							JOptionPane.showMessageDialog (null, "You made the demon follow you back in the volcano. Destroy it!", "", JOptionPane.INFORMATION_MESSAGE);
						}
						if(level==0){
							attack=true;
							level++;
							init(gc, arg2);
							x=240f;
							y=569f;
							enemyPos.x= 240;
							enemyPos.y= 32;
						}
					}
					x -= SIZE;
					
					heroMovement += SIZE;
				}
				else if (input.isKeyPressed(Input.KEY_RIGHT))
				{
					Warrior = rightW;

					
					//character limit on right movement
					if(x>452)
						x=452;

					

					if ((isTrap(x + SIZE , y) || isTrap(x + SIZE, y+SIZE-1)))
					{
						if(!tookDamage){
							Hero.getHeroInstance().changeLife(3);
							tookDamage = true;
						}
					}
					if ((isWarp(x + SIZE, y) || isWarp(x + SIZE, y+SIZE-1)))
					{
						if(level==1){
							attack=true;
							level++;
							init(gc, arg2);
							x=240f;
							y=569f;
							enemyPos.x= 240;
							enemyPos.y= 32;
							JOptionPane.showMessageDialog (null, "You made the demon follow you back in the volcano. Destroy it!", "", JOptionPane.INFORMATION_MESSAGE);
						}
						if(level==0){
							attack=true;
							level++;
							init(gc, arg2);
							x=240f;
							y=569f;
							enemyPos.x= 240;
							enemyPos.y= 32;
						}
					}
					

					x += SIZE;
					heroMovement += SIZE;

				}
				enemyMovement = 0;

			}
		}

		//attack mode
		if(input.isKeyDown(Input.KEY_A)){
			attack = true;
			System.out.println("Currently in attack mode");
			//if(isDead(BothloE)){
				//JOptionPane.showMessageDialog (null, "You managed to slay the demon and save the world!", "You Are Victorious!", JOptionPane.INFORMATION_MESSAGE);
				//JOptionPane.showMessageDialog (null, "...at least for now...", "", JOptionPane.WARNING_MESSAGE);
			//}

		}
		if(attack == true && commitedAttack==false){
			if(xdif<=32 && ydif<=32){
				
				if(level==2){
					
					BothloE.changeLife(Hero.getHeroInstance().getDamage());
					commitedAttack = true;
					attack=false;
				}	
				
				if(!isDead(BothloE)){
					
						
						Bothlo.draw(enemyPos.x,enemyPos.y);
				}
				if(isDead(BothloE)){
					try {
						
						Thread.sleep(100);
					} catch (InterruptedException e) {

						e.printStackTrace();
					}
					if(isDead(BothloE)){
						JOptionPane.showMessageDialog (null, "You managed to slay the demon and save the world!", "You Are Victorious!", JOptionPane.INFORMATION_MESSAGE);
						JOptionPane.showMessageDialog (null, "...at least for now...", "", JOptionPane.WARNING_MESSAGE);
						}
				}
			}

		}
		if(input.isKeyDown(Input.KEY_S)){
			attack = false;
			counter++;
			input.clearKeyPressedRecord();
		}

		//end turn mode
		if(input.isKeyPressed(Input.KEY_E)){			
			enemyTurn = true;	
			tookDamage = false;
			if(isDead(BothloE)){
				enemyTurn= false;
				input.clearKeyPressedRecord();
				System.exit(0);
			}

			heroMovement = 0;	
			System.out.println("Currently in enemy turn");

		}


		if(enemyTurn == true){
			//checking maximum movement for enemy 

			if(enemyMovement<=3*SIZE)	{
				//magic calculations!!
				xdif = Math.abs(enemyPos.x - x);
				ydif = Math.abs(enemyPos.y - y);
				if ( xdif > ydif ) {
					k = 2;
					l = 1;
					enemyMovement += k+l;
				}
				if ( ydif > xdif ) {
					l = 2;
					k = 1;
					enemyMovement += k+l;
				}
				if ( xdif == 0 ) {
					l = 3;
					k = 0;
					enemyMovement += k+l;
				}
				if ( ydif == 0 ) {
					k = 3;
					l = 0;
					enemyMovement += k+l;
				}
				if ( xdif == ydif ) {
					k = 1;
					l = 1;
					enemyMovement += k+l;
				}
				if ( enemyPos.x > x )
					k*= -1;
				if ( enemyPos.y > y )
					l*=-1;
				enemyPos.translate((int)k, (int)l);
				if(xdif<=32 && ydif<=32){
					enemyTurn = false;
					commitedAttack=false;
					Hero.getHeroInstance().changeLife(BothloE.getDamage());
					input.clearKeyPressedRecord();
				}
			}


			//signal end of enemy turn when maximum movement reached


			if(enemyMovement > 3*SIZE){
				commitedAttack=false;
				enemyTurn = false;
				input.clearKeyPressedRecord();
			}

			//delaying update for smoooooooooth movement
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}


		//escape
		if(input.isKeyDown(Input.KEY_ESCAPE)){
			quit = true;

		}      

		//when they hit escape
		if(quit==true){
			if(input.isKeyDown(Input.KEY_R)){
				quit = false;
			}
			if(input.isKeyDown(Input.KEY_M)){
				AppGameContainer apgc = (AppGameContainer)gc;
				apgc.setDisplayMode(900, 384, false);
				sbg.enterState(0);
				try{
					Thread.sleep(250);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
			if(input.isKeyDown(Input.KEY_Q)){
				System.exit(0);
			}
		}
	}




	private boolean isWarp(float xi, float yi)
	{
		int xBlock = (int)xi / SIZE;
		int yBlock = (int)yi / SIZE;
		if(xBlock <0){
			xBlock=0;
		}else if(xBlock >15){
			xBlock = 15;
		}
		if (yBlock <0){
			yBlock =0;
		}else if(yBlock> 20){
			yBlock=20;
		}
		return warps[xBlock-1][yBlock];
	}
	private boolean isTrap(float xi, float yi)
	{
		int xBlock = (int)xi / SIZE;
		int yBlock = (int)yi / SIZE;
		if(xBlock< 0){
			xBlock=0;
		}else if(xBlock> 15){
			xBlock = 15;
		}
		if (yBlock< 0){
			yBlock =0;
		}else if(yBlock> 20){
			yBlock=20;
		}
		return traps[xBlock][yBlock];
	}


	@Override
	public int getID() {
		return 1;
	}
	public void NewGame(GameContainer arg0, StateBasedGame sbg, Graphics g) throws SlickException{
		Hero.getHeroInstance().setLife(120);
		BothloE.setLife(120);
		AppGameContainer apgc = (AppGameContainer)arg0;
		apgc.setDisplayMode(900, 384, false);
		sbg.enterState(0);

		try{
			Thread.sleep(250);
		}catch(InterruptedException e){
			e.printStackTrace();
		}

		x=240f;
		y=569f;
		enemyPos.x= 240;
		enemyPos.y= 32;
	}
	public boolean isDead(Enemy E){
		if(E.getLife()<=0){
			return true;
		}
		else return false;}



}