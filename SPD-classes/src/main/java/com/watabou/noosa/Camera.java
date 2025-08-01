/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * Summoning Pixel Dungeon Reincarnated
 * Copyright (C) 2023-2025 Trashbox Bobylev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.watabou.noosa;

import com.watabou.glwrap.Matrix;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Camera extends Gizmo {

	private static ArrayList<Camera> all = new ArrayList<>();
	
	protected static float invW2;
	protected static float invH2;
	
	public static Camera main;

	public boolean fullScreen;

	public float zoom;
	
	public int x;
	public int y;
	public int width;
	public int height;
	
	int screenWidth;
	int screenHeight;
	
	public float[] matrix;

	public PointF edgeScroll;
	public PointF scroll;
	public PointF centerOffset;
	
	private float shakeMagX		= 10f;
	private float shakeMagY		= 10f;
	private float shakeTime		= 0f;
	private float shakeDuration	= 1f;
	
	protected float shakeX;
	protected float shakeY;
	
	public static Camera reset() {
		return reset( createFullscreen( 1 ) );
	}
	
	public static synchronized Camera reset( Camera newCamera ) {
		
		invW2 = 2f / Game.width;
		invH2 = 2f / Game.height;
		
		int length = all.size();
		for (int i=0; i < length; i++) {
			all.get( i ).destroy();
		}
		all.clear();
		
		return main = add( newCamera );
	}
	
	public static synchronized Camera add( Camera camera ) {
		all.add( camera );
		return camera;
	}
	
	public static synchronized Camera remove( Camera camera ) {
		all.remove( camera );
		return camera;
	}
	
	public static synchronized void updateAll() {
		int length = all.size();
		for (int i=0; i < length; i++) {
			Camera c = all.get( i );
			if (c != null && c.exists && c.active) {
				c.update();
			}
		}
	}
	
	public static Camera createFullscreen( float zoom ) {
		int w = (int)Math.ceil( Game.width / zoom );
		int h = (int)Math.ceil( Game.height / zoom );
		Camera c = new Camera(
				(int)(Game.width - w * zoom) / 2,
				(int)(Game.height - h * zoom) / 2,
				w, h, zoom );
		c.fullScreen = true;
		return c;
	}
	
	public Camera( int x, int y, int width, int height, float zoom ) {
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.zoom = zoom;
		
		screenWidth = (int)(width * zoom);
		screenHeight = (int)(height * zoom);

		edgeScroll = new PointF();
		scroll = new PointF();
		centerOffset = new PointF();
		
		matrix = new float[16];
		Matrix.setIdentity( matrix );
	}
	
	@Override
	public void destroy() {
		panIntensity = 0f;
	}
	
	public synchronized void zoom( float value ) {
		zoom( value,
			scroll.x + width / 2f,
			scroll.y + height / 2f );
	}
	
	public synchronized void zoom( float value, float fx, float fy ) {

		PointF offsetAdjust = centerOffset.clone();
		centerOffset.scale(zoom).invScale(value);

		zoom = value;
		width = (int)(screenWidth / zoom);
		height = (int)(screenHeight / zoom);
		
		snapTo( fx - offsetAdjust.x, fy - offsetAdjust.y );
	}
	
	public synchronized void resize( int width, int height ) {
		this.width = width;
		this.height = height;
		screenWidth = (int)(width * zoom);
		screenHeight = (int)(height * zoom);
	}
	
	Visual followTarget = null;
	PointF panTarget = new PointF();
	//camera moves at a speed such that it will pan to its current target in 1/intensity seconds
	//keep in mind though that this speed is constantly decreasing, so actual pan time is higher
	float panIntensity = 0f;

	//what percentage of the screen to ignore when follow panning.
	// 0% means always keep in the center, 50% would mean pan until target is within center 50% of screen
	float followDeadzone = 0f;
	
	@Override
	public synchronized void update() {
		super.update();

		float deadX = 0;
		float deadY = 0;
		if (followTarget != null){
			//manually assign here to avoid an allocation from sprite.center()
			panTarget.x = followTarget.x + followTarget.width()/2;
			panTarget.y = followTarget.y + followTarget.height()/2;
			panTarget.offset(centerOffset);
			deadX = width * followDeadzone /2f;
			deadY = height * followDeadzone /2f;
		}
		
		if (panIntensity > 0f){

			float panX = panTarget.x - (scroll.x + width/2f);
			float panY = panTarget.y - (scroll.y + height/2f);

			if (panX > deadX){
				panX -= deadX;
			} else if (panX < -deadX){
				panX += deadX;
			} else {
				panX = 0;
			}

			if (panY > deadY){
				panY -= deadY;
			} else if (panY < -deadY){
				panY += deadY;
			} else {
				panY = 0;
			}

			panX *= Math.min(1f, Game.elapsed * panIntensity);
			panY *= Math.min(1f, Game.elapsed * panIntensity);

			scroll.offset(panX, panY);
		}
		
		if ((shakeTime -= Game.elapsed) > 0) {
			float damping = shakeTime / shakeDuration;
			shakeX = Random.Float( -shakeMagX, +shakeMagX ) * damping;
			shakeY = Random.Float( -shakeMagY, +shakeMagY ) * damping;
		} else {
			shakeX = 0;
			shakeY = 0;
		}
		
		updateMatrix();
	}
	
	public PointF center() {
		return new PointF( width / 2, height / 2 );
	}
	
	public boolean hitTest( float x, float y ) {
		return x >= this.x && y >= this.y && x < this.x + screenWidth && y < this.y + screenHeight;
	}
	
	public synchronized void shift( PointF point ){
		scroll.offset(point);
		panIntensity = 0f;
	}

	public synchronized void setCenterOffset( float x, float y ){
		scroll.x    += x - centerOffset.x;
		scroll.y    += y - centerOffset.y;
		if (panTarget != null) {
			panTarget.x += x - centerOffset.x;
			panTarget.y += y - centerOffset.y;
		}
		centerOffset.set(x, y);
	}
	
	public synchronized void snapTo(float x, float y ) {
		scroll.set( x - width / 2f, y - height / 2f ).offset(centerOffset);
		panIntensity = 0f;
		followTarget = null;
	}
	
	public void snapTo(PointF point ) {
		snapTo( point.x, point.y );
	}
	
	public synchronized void panTo( PointF dst, float intensity ){
		panTarget = dst.offset(centerOffset);
		panIntensity = intensity;
		followTarget = null;
	}
	
	public synchronized void panFollow(Visual target, float intensity ){
		followTarget = target;
		panIntensity = intensity;
	}

	public synchronized Visual followTarget(){
		return followTarget;
	}

	public synchronized void setFollowDeadzone( float deadzone ){
		followDeadzone = deadzone;
	}
	
	public PointF screenToCamera( int x, int y ) {
		return new PointF(
			(x - this.x) / zoom + scroll.x,
			(y - this.y) / zoom + scroll.y );
	}
	
	public Point cameraToScreen( float x, float y ) {
		return new Point(
			(int)((x - scroll.x) * zoom + this.x),
			(int)((y - scroll.y) * zoom + this.y));
	}
	
	public float screenWidth() {
		return width * zoom;
	}
	
	public float screenHeight() {
		return height * zoom;
	}
	
	protected void updateMatrix() {

	/*	Matrix.setIdentity( matrix );
		Matrix.translate( matrix, -1, +1 );
		Matrix.scale( matrix, 2f / G.width, -2f / G.height );
		Matrix.translate( matrix, x, y );
		Matrix.scale( matrix, zoom, zoom );
		Matrix.translate( matrix, scroll.x, scroll.y );*/
		
		matrix[0] = +zoom * invW2;
		matrix[5] = -zoom * invH2;
		
		matrix[12] = -1 + x * invW2 - (scroll.x + shakeX) * matrix[0];
		matrix[13] = +1 - y * invH2 - (scroll.y + shakeY) * matrix[5];
		
	}
	
	public synchronized void shake( float magnitude, float duration ) {
		shakeMagX = shakeMagY = magnitude;
		shakeTime = shakeDuration = duration;
	}
}
