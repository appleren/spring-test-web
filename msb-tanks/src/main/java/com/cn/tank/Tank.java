package com.cn.tank;

import com.cn.tankFactory.BaseTank;
import lombok.Data;

import java.awt.*;
import java.util.Random;

/**
 * @ClassName Tank
 * @Description TODO
 * @Author
 * @Date 2020/5/31 23:04
 */
@Data
public class Tank extends BaseTank {
    public int x, y;
    public Dir dir = Dir.DOWN;
    private static final int SPEED = 3;
    private boolean moveing = true;
    private boolean live = true;
    public static int WIDTH = ResourcesMgr.goodTankD.getWidth(), HEIGTH = ResourcesMgr.goodTankD.getHeight();

    public TankFrame fs = null;
    public Group group = Group.BAD;
    private Random random = new Random();
    FireStrategy fireStrategy = DefaltFireStrategy.getInstance();

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setDir(Dir dir) {
        this.dir = dir;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public TankFrame getFs() {
        return fs;
    }

    public Dir getDir() {
        return dir;
    }

    public static int getSPEED() {
        return SPEED;
    }
    public Tank(int x, int y, Dir dir, TankFrame fs, Group group) {
        super();
        this.dir = dir;
        this.x = x;
        this.y = y;
        this.fs = fs;
        this.group = group;

        rect.x = x;
        rect.y= y;
        rect.width=WIDTH;
        rect.height=HEIGTH;
        if(group==Group.GOOD){
            String goodFSName = PropertyMgr.get("goodsFS").toString();
            try {
            /**
             * 通过类加载方式，从获取到类名，然后通过java反射new出来类对象；
             * fireStrategy =   Class.forName(goodFSName).newInstance();
             * 方法保留在此
             */
                fireStrategy =   (FireStrategy)Class.forName(goodFSName).newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            fireStrategy=  new FourFireStrategy();
        }
    }

    public void paint(Graphics graphics) {
        if (!live) {
            fs.tanks.remove(this);
        }
        /**
         * 根据方向赋值对应的图片
         */
        switch (dir) {
            case LEFT:
                graphics.drawImage(this.group==Group.GOOD?ResourcesMgr.goodTankL:ResourcesMgr.badTankL, x, y, null);
                break;
            case UP:
                graphics.drawImage(this.group==Group.GOOD?ResourcesMgr.goodTankU:ResourcesMgr.badTankU, x, y, null);
                break;
            case RIGHT:
                graphics.drawImage(this.group==Group.GOOD?ResourcesMgr.goodTankR:ResourcesMgr.badTankR, x, y, null);
                break;
            case DOWN:
                graphics.drawImage(this.group==Group.GOOD?ResourcesMgr.goodTankD:ResourcesMgr.badTankD, x, y, null);
                break;
        }
        move();
    }

    public void setMoveing(boolean moveing) {
        this.moveing = moveing;
    }

    public void move() {
        if (!moveing) {
            return;
        }
        switch (dir) {
            case LEFT:
                x -= SPEED;
                break;
            case UP:
                y -= SPEED;
                break;
            case RIGHT:
                x += SPEED;
                break;
            case DOWN:
                y += SPEED;
                break;
        }

        rect.x = this.x;
        rect.y= this.y;
        rect.width=this.WIDTH;
        rect.height=this.HEIGTH;

        if (this.group == Group.BAD && random.nextInt(100) > 95) {
            this.fire();
            randomDir();
        }

        boundsCheck();
    }

    public void fire() {

        //开火的时候添加子弹
        fireStrategy.fire(this);
    }

    public void die() {
        this.live = false;

    }

    public void randomDir() {
        this.dir = Dir.values()[random.nextInt(4)];
    }
    public void boundsCheck(){
        if(this.x<2){
            x=2;
        }else if(this.y<28){
            y=28;
        }else if(this.x>=TankFrame.GAME_WIDTH-Tank.WIDTH-2){
            x=TankFrame.GAME_WIDTH-Tank.WIDTH-2;
        }else if(this.y>=TankFrame.GAME_HEIGTH-Tank.HEIGTH-2){
            y=TankFrame.GAME_HEIGTH-Tank.HEIGTH-2;
        }
    }
}
