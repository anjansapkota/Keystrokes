package com.um.model;

public class Key {

private int id;
private long letter1;
private long letter2;
private long press1_release1;	//the time interval between a key press and the key release.
private long press1_press2;		//the time interval between a key press and the next key press.
private long release1_press2;	//the time interval between a key release and the next key press. (-)
private long release1_release2;	//the time interval between a key release and the next key release.
private long press1_release2;	//the time interval between a key press and the next key release.

public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public long getLetter1() {
	return letter1;
}
public void setLetter1(long letter1) {
	this.letter1 = letter1;
}
public long getLetter2() {
	return letter2;
}
public void setLetter2(long letter2) {
	this.letter2 = letter2;
}
public long getPress1_release1() {
	return press1_release1;
}
public void setPress1_release1(long press1_release1) {
	this.press1_release1 = press1_release1;
}
public long getPress1_press2() {
	return press1_press2;
}
public void setPress1_press2(long press1_press2) {
	this.press1_press2 = press1_press2;
}
public long getRelease1_press2() {
	return release1_press2;
}
public void setRelease1_press2(long release1_press2) {
	this.release1_press2 = release1_press2;
}
public long getRelease1_release2() {
	return release1_release2;
}
public void setRelease1_release2(long release_release) {
	this.release1_release2 = release_release;
}
public long getPress1_release2() {
	return press1_release2;
}
public void setPress1_release2(long press1_release2) {
	this.press1_release2 = press1_release2;
}

}
