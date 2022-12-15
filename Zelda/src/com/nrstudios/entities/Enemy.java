package com.nrstudios.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.nrstudios.main.Game;
import com.nrstudios.world.AStar;
import com.nrstudios.world.Camera;
import com.nrstudios.world.Vector2i;


public class Enemy extends Entity {
	
	
	
	private int frames = 0,maxFrames = 3,index = 0,maxIndex = 11;
	
	private BufferedImage[] sprites;
	
	private int life = 4;
	
	private boolean isDamaged = false;
	private int damageFrames = 10,DamageCurrent = 0;
	
//ANIMACAO
	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, null);
		sprites = new BufferedImage[13];
		sprites[0] = Game.spritesheet.getSprite(224+16, 0, 16, 16);
		sprites[1] = Game.spritesheet.getSprite(224+16*2, 0, 16, 16);
		sprites[2] = Game.spritesheet.getSprite(224+16*3, 0, 16, 16);
		sprites[3] = Game.spritesheet.getSprite(224+16*4, 0, 16, 16);
		sprites[4] = Game.spritesheet.getSprite(224+16*5, 0, 16, 16);
		sprites[5] = Game.spritesheet.getSprite(224+16*6, 0, 16, 16);
		sprites[6] = Game.spritesheet.getSprite(224+16*7, 0, 16, 16);
		sprites[7] = Game.spritesheet.getSprite(224+16*8, 0, 16, 16);
		sprites[8] = Game.spritesheet.getSprite(224+16*9, 0, 16, 16);
		sprites[9] = Game.spritesheet.getSprite(224+16*10, 0, 16, 16);
		sprites[10] = Game.spritesheet.getSprite(224+16*11, 0, 16, 16);
		sprites[11] = Game.spritesheet.getSprite(224+16*12, 0, 16, 16);
		sprites[12] = Game.spritesheet.getSprite(224+16*13, 0, 16, 16);
		sprites[12] = Game.spritesheet.getSprite(224+16*14, 0, 16, 16);
	}
	//MOVIMENTO
	public void tick() {
	depth = 0;
	if(this.calculateDistance(this.getX(),this.getY(), Game.player.getX(), Game.player.getY()) < 200) {	
	if(!isColiddingWithPlay	()) {	
		if(path == null || path.size() == 0) {
			Vector2i start = new Vector2i((int)(x/16),(int)(y/16));
			Vector2i end = new Vector2i((int)(Game.player.x/16),(int)(Game.player.y/16));
			path = AStar.findPath(Game.world, start, end);
			}
		}else {
			if(new Random().nextInt(100) < 5) {
				Game.player.life-=Game.rand.nextInt(3);
				Game.player.isDamaged = true;
			}
		}
			if(new Random().nextInt(100) < 60)
			followPath(path);
			if(new Random().nextInt(100) < 5) {
				Vector2i start = new Vector2i((int)(x/16),(int)(y/16));
				Vector2i end = new Vector2i((int)(Game.player.x/16),(int)(Game.player.y/16));
				path = AStar.findPath(Game.world, start, end);
			}
	}
			frames++;
			if(frames == maxFrames) {
				frames = 0;
				index++;
				if(index > maxIndex)
					index = 0;
			}
			collidingBullet();	
			
			if(life <=0) {
				destroySelf();
				return;
			}
			//FEEDBACK
			if(isDamaged) {
				this.DamageCurrent++;
				if(this.DamageCurrent == this.damageFrames) {
					this.DamageCurrent = 0;
					this.isDamaged = false;
				}
			}
			
			//TOMANDO DANO
		}
		public void destroySelf() {
			Game.enemies.remove(this);
			Game.entities.remove(this);
		}
		public void collidingBullet() {
		for(int i = 0; i < Game.bullets.size(); i++) {
			Entity e = Game.bullets.get(i);
			if(e instanceof BulletShoot) {
				if(Entity.isColidding(this, e)) {
					isDamaged = true;
					life--;
					Game.bullets.remove(i);
					return;
					}
				}
			}
		}
		//COLISAO
		public boolean isColiddingWithPlay() {
			Rectangle enemyCurrent = new Rectangle(this.getX() + maskx,this.getY() + masky,mwidth,mheight);
			Rectangle player = new Rectangle(Game.player.getX(),Game.player.getY(),16,16);
		
				return enemyCurrent.intersects(player);
			}
	
		
		//RENDER COLISOES
		public void render(Graphics g) {
		if(!isDamaged)
			g.drawImage(sprites[index], this.getX() - Camera.x,this.getY() - Camera.y,null);
		else
			g.drawImage(Entity.ENEMY_FEEDBACK, this.getX() - Camera.x,this.getY() - Camera.y,null);
		//g.setColor(Color.blue);
		//g.fillRect(this.getX() + maskx - Camera.x,this.getY() + masky - Camera.y,mwidth, mheight);
		
	}

}
