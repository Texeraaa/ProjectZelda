package com.nrstudios.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.nrstudios.main.Game;
import com.nrstudios.world.Camera;
import com.nrstudios.world.World;

public class Player extends Entity{
	
	public boolean right,up,left,down;
	public static int right_dir = 0, left_dir = 1;
	public static int dir = right_dir;
	public double speed = 1.0;
	
	private int frames = 0,maxFrames = 2,index = 0,maxIndex = 3;
	private boolean moved = false;
	
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	private BufferedImage playerDamageLeft;
	private BufferedImage playerDamageRight;
	
	public  boolean isDamaged = false;
	private int damageFrames = 0;
	
	public  boolean shoot = false,mouseShoot = false;
	
	public static boolean hasGun = false;
	public int bullet = 0;
	public double life = 100, maxLife = 100;
	public int mx,my;

	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
	
	rightPlayer = new BufferedImage[4];
	leftPlayer = new BufferedImage[4];
	playerDamageLeft = Game.spritesheet.getSprite(16*28, 0, 16, 16);
	playerDamageRight = Game.spritesheet.getSprite(16*31, 0, 16, 16);
	
	for (int i =0;i < 4; i++) {
	rightPlayer[i] = Game.spritesheet.getSprite(112 + (i*16), 0, 16, 16);
	
	}
	for (int i =0;i < 4; i++) {
		leftPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 0, 16, 16);
		
		}
	
	}
	//MOVIMENTO
	public void tick() {
		depth = 1;
		moved = false;
			if(right && World.isFree((int)(x+speed),this.getY())) {
				moved = true;
				dir = right_dir;
				x+=speed;
				
			}
			else if(left && World.isFree((int)(x-speed),this.getY())) {
				moved = true;
				dir = left_dir;
				x-=speed;
			}
			if(up && World.isFree(this.getX(),(int)(y-speed))) {
				moved = true;
				y-=speed;
			}
			else if(down && World.isFree(this.getX(),(int)(y+speed))) {
				moved = true;
				y+=speed;
			}
			if(moved) {
				frames++;
				if(frames == maxFrames) {
					frames = 0;
					index++;
					if(index > maxIndex)
						index = 0;
				}
			}
			
			this.checkCollisionLifePack();
			this.checkCollisionBullet();
			this.checkCollisionGun();
			
			if(isDamaged) {
				this.damageFrames++;
				if(this.damageFrames == 3) {
					this.damageFrames = 0;
					isDamaged = false;
				}
			}
			
			if(shoot) {
				shoot = false;
				if(hasGun && bullet > 0) {
				bullet--;
				shoot = false;
				int dx = 0;
				int px = -3;
				int py = -2;
				if(dir == right_dir) {
					px = -5;
					dx = 1;
				}else {
					px = -9;
					dx = -1; 
				}
				
				BulletShoot bullet = new BulletShoot(this.getX() - px, this.getY() - py,3,3,null,dx,0);
				Game.bullets.add(bullet);
				}
				
			}
			
			if(mouseShoot) {
				
			mouseShoot = false;
			double angle = (Math.atan2(my - (this.getY() + 3 - Camera.y),mx - (this.getX() + 3  - Camera.x)));
				
			if(hasGun && bullet > 0) {
			bullet--;
			//CRIAR FLECHA
			double dx = Math.cos(angle);
			double dy = Math.sin(angle);
			int px = -3;
			int py = -2;
			
			BulletShoot bullet = new BulletShoot(this.getX() - px, this.getY() - py,3,3,null,dx,dy);
			Game.bullets.add(bullet);
					}
				}
			
			
			if(life<=0) {
				//GAMEOVER
				life = 0;
				Game.gameState = "GAME_OVER";
			}
			
			//CAMERA
			updateCamera();
	}
	
	public void updateCamera() {
		Camera.x = Camera.clamp(this.getX() - (Game.Width/2), 0,World.WIDTH*16 - Game.Width);
		Camera.y = Camera.clamp(this.getY() - (Game.Height/2), 0,World.HEIGHT*16 - Game.Height);
	}
	
	public void checkCollisionBullet() {
		for(int i = 0; i < Game.entities.size(); i++) {
		Entity atual = Game.entities.get(i);
		if(atual instanceof Bullet) {
			if(Entity.isColidding(this, atual)) {
				bullet+=10;
				Game.entities.remove(atual);
						}
					}
				}
			}
	public void checkCollisionGun() {
		for(int i = 0; i < Game.entities.size(); i++) {
		Entity atual = Game.entities.get(i);
		if(atual instanceof Weapon) {
			if(Entity.isColidding(this, atual)) {
				hasGun = true;
				Game.entities.remove(atual);
						}
					}
				}
			}
	
	public void checkCollisionLifePack() {
		for(int i = 0; i < Game.entities.size(); i++) {
		Entity atual = Game.entities.get(i);
		if(atual instanceof lifePack) {
			if(Entity.isColidding(this, atual)) {
				life+=10;
				if(life >= 100)
					life = 100;
				Game.entities.remove(atual);
				return;
				}
			}
		}
	}
	
	//RENDER DE SPRITE
	public void render(Graphics g) {
		if(!isDamaged) {
		if(dir == right_dir) {
	g.drawImage(rightPlayer[index], this.getX() - Camera.x,this.getY() - Camera.y,null);
			if(hasGun) {
				//ARMA DIREITA
				g.drawImage(Entity.GUN_RIGHT, this.getX() + 4 - Camera.x, this.getY() + 2 - Camera.y,null);
			}
		}else if(dir == left_dir) {
	g.drawImage(leftPlayer[index], this.getX() - Camera.x,this.getY() - Camera.y,null);
			if(hasGun) {
				//ARMA ESQUERDA
				g.drawImage(Entity.GUN_LEFT, this.getX() - 4 - Camera.x, this.getY() + 2 - Camera.y,null);
				}
			}	
		}
		//MOVIMENTO DANO
		if (isDamaged) {
		if(dir == right_dir) {
				g.drawImage(playerDamageRight, this.getX() - Camera.x, this.getY() - Camera.y, null);
			}
		else if(dir ==left_dir) {
				g.drawImage(playerDamageLeft, this.getX() - Camera.x, this.getY() - Camera.y, null);
					
					}if(hasGun) {
		if(dir == right_dir) {
			g.drawImage(Entity.GUN_RIGHTD, this.getX() + 4 - Camera.x, this.getY() + 2 - Camera.y,null);		
					}else {
			g.drawImage(Entity.GUN_LEFTD, this.getX() - 4 - Camera.x, this.getY() + 2 - Camera.y,null);
					}
				}
			}
		}
	}
