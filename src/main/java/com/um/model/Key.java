package com.um.model;

public class Key {

private String letter1;
private String letter2;
private long press1_release1;	//the time interval between a key press and the key release.
private long press1_press2;		//the time interval between a key press and the next key press.
private long release1_press2;	//the time interval between a key release and the next key press. (-)
private long release_release;	//the time interval between a key release and the next key release.

public String getLetter1() {
	return letter1;
}
public void setLetter1(String letter1) {
	this.letter1 = letter1;
}
public String getLetter2() {
	return letter2;
}
public void setLetter2(String letter2) {
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
public long getRelease_release() {
	return release_release;
}
public void setRelease_release(long release_release) {
	this.release_release = release_release;
}
}
