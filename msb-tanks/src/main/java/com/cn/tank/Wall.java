package com.cn.tank;

import java.awt.*;

/**
 * @ClassName Wall
 * @Description 墙
 * @Author zhanghongjun
 * @Date 2020-06-10 22:34
 * @Version 1.0
 */
public class Wall extends GameObject {
    int w, h;
    public Rectangle rect;

    public Wall(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.rect = new Rectangle(x, y, w, h);
    }

    @Override
    public void paint(Graphics g) {
        Color c = g.getColor();
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, y, w, h);
        g.setColor(c);
    }
}
