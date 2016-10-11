package com.mZone.epro.testhisotry.data;

import java.util.Date;
import com.mZone.epro.client.data.BookProviderMetaData.TestHistoryTableMetaData;
import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

public class TestHistoryItem implements Parcelable {

	private int _id;
	private int bookID;
	private int status;
	private Date createdDate;
	private Date modifiedDate;
	private String filePath;
	private int mode;
	private int scorePart1;
	private int scorePart2;
	private int scorePart3;
	private int scorePart4;
	private int scorePart5;
	private int scorePart6;
	private int scorePart7;
	private int currentTime;
	private int currentReadingTime;
	private int activePart;
	private long mediaCurrentTime;
	
	//using for extension
	private int bookRowID;
	private String bookName;
	private String isHavingImage;
	
	//using for status checking
	public static final int STATUS_DOING = 0;
	public static final int STATUS_FINISH = 1;
	
	public TestHistoryItem(int bookID, int status, Date createdDate, String filePath, int mode, 
			int currentTime, int currentReadingTime,int activePart, long mediaCurrentTime) {
		// TODO Auto-generated constructor stub
		this.bookID = bookID;
		this.status = status;
		this.createdDate = createdDate;
		this.modifiedDate = createdDate;
		this.filePath = filePath;
		this.mode = mode;
		this.currentTime = currentTime;
		this.currentReadingTime = currentReadingTime;
		this.activePart = activePart;
		this.mediaCurrentTime = mediaCurrentTime;
		this.scorePart1 = 0;
		this.scorePart2 = 0;
		this.scorePart3 = 0;
		this.scorePart4 = 0;
		this.scorePart5 = 0;
		this.scorePart6 = 0;
		this.scorePart7 = 0;
	}
	
	public TestHistoryItem(int _id, int bookID, int status, Date createdDate,
			Date modifiedDate, String filePath, int mode, int scorePart1,
			int scorePart2, int scorePart3, int scorePart4, int scorePart5,
			int scorePart6, int scorePart7, int currentTime, int currentReadingTime, int activePart,
			long mediaCurrentTime, int bookRowID, String bookName,
			String isHavingImage) {
		super();
		this._id = _id;
		this.bookID = bookID;
		this.status = status;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.filePath = filePath;
		this.mode = mode;
		this.scorePart1 = scorePart1;
		this.scorePart2 = scorePart2;
		this.scorePart3 = scorePart3;
		this.scorePart4 = scorePart4;
		this.scorePart5 = scorePart5;
		this.scorePart6 = scorePart6;
		this.scorePart7 = scorePart7;
		this.currentTime = currentTime;
		this.currentReadingTime = currentReadingTime;
		this.activePart = activePart;
		this.mediaCurrentTime = mediaCurrentTime;
		this.bookRowID = bookRowID;
		this.bookName = bookName;
		this.isHavingImage = isHavingImage;
	}

	public ContentValues getContentValues(){
		ContentValues contentValues = new ContentValues();
		contentValues.put(TestHistoryTableMetaData.BOOK_ID, bookID);
		contentValues.put(TestHistoryTableMetaData.STATUS, status);
		contentValues.put(TestHistoryTableMetaData.CREATE_DATE, createdDate.getTime());
		contentValues.put(TestHistoryTableMetaData.MODIFIED_DATE, modifiedDate.getTime());
		contentValues.put(TestHistoryTableMetaData.FILE_PATH, filePath);
		contentValues.put(TestHistoryTableMetaData.MODE, mode);
		contentValues.put(TestHistoryTableMetaData.SCORE_PART1, scorePart1);
		contentValues.put(TestHistoryTableMetaData.SCORE_PART2, scorePart2);
		contentValues.put(TestHistoryTableMetaData.SCORE_PART3, scorePart3);
		contentValues.put(TestHistoryTableMetaData.SCORE_PART4, scorePart4);
		contentValues.put(TestHistoryTableMetaData.SCORE_PART5, scorePart5);
		contentValues.put(TestHistoryTableMetaData.SCORE_PART6, scorePart6);
		contentValues.put(TestHistoryTableMetaData.SCORE_PART7, scorePart7);
		contentValues.put(TestHistoryTableMetaData.CURRENT_TIME, currentTime);
		contentValues.put(TestHistoryTableMetaData.CURRENT_READING_TIME, currentReadingTime);
		contentValues.put(TestHistoryTableMetaData.ACTIVE_PART, activePart);
		contentValues.put(TestHistoryTableMetaData.MEDIA_CURRENT_TIME, mediaCurrentTime);
		return contentValues;
	}
	
	public TestHistoryItem() {
		// TODO Auto-generated constructor stub
	}
	public TestHistoryItem(Parcel in) {
		// TODO Auto-generated constructor stub
		readFromParcel(in);
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		@Override
		public TestHistoryItem createFromParcel(Parcel in) {
			 return new TestHistoryItem(in); 
		}   
		@Override
		public TestHistoryItem[] newArray(int size) {
			 return new TestHistoryItem[size]; 
		} 
	}; 

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(_id);
		dest.writeInt(bookID);
		dest.writeInt(status);
		dest.writeLong(createdDate.getTime());
		dest.writeLong(modifiedDate.getTime());
		dest.writeString(filePath);
		dest.writeInt(mode);
		dest.writeInt(scorePart1);
		dest.writeInt(scorePart2);
		dest.writeInt(scorePart3);
		dest.writeInt(scorePart4);
		dest.writeInt(scorePart5);
		dest.writeInt(scorePart6);
		dest.writeInt(scorePart7);
		dest.writeInt(currentTime);
		dest.writeInt(currentReadingTime);
		dest.writeInt(activePart);
		dest.writeLong(mediaCurrentTime);
		dest.writeInt(bookRowID);
		dest.writeString(bookName);
		dest.writeString(isHavingImage);
	}
	
	private void readFromParcel(Parcel in){
		this._id = in.readInt();
		this.bookID = in.readInt();
		this.status = in.readInt();
		this.createdDate = new Date(in.readLong());
		this.modifiedDate = new Date(in.readLong());
		this.filePath = in.readString();
		this.mode = in.readInt();
		this.scorePart1 = in.readInt();
		this.scorePart2 = in.readInt();
		this.scorePart3 = in.readInt();
		this.scorePart4 = in.readInt();
		this.scorePart5 = in.readInt();
		this.scorePart6 = in.readInt();
		this.scorePart7 = in.readInt();
		this.currentTime = in.readInt();
		this.currentReadingTime = in.readInt();
		this.activePart = in.readInt();
		this.mediaCurrentTime = in.readLong();
		this.bookRowID = in.readInt();
		this.bookName = in.readString();
		this.isHavingImage = in.readString();
	}
	

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public int getBookID() {
		return bookID;
	}

	public void setBookID(int bookID) {
		this.bookID = bookID;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getScorePart1() {
		return scorePart1;
	}

	public void setScorePart1(int scorePart1) {
		this.scorePart1 = scorePart1;
	}

	public int getScorePart2() {
		return scorePart2;
	}

	public void setScorePart2(int scorePart2) {
		this.scorePart2 = scorePart2;
	}

	public int getScorePart3() {
		return scorePart3;
	}

	public void setScorePart3(int scorePart3) {
		this.scorePart3 = scorePart3;
	}

	public int getScorePart4() {
		return scorePart4;
	}

	public void setScorePart4(int scorePart4) {
		this.scorePart4 = scorePart4;
	}

	public int getScorePart5() {
		return scorePart5;
	}

	public void setScorePart5(int scorePart5) {
		this.scorePart5 = scorePart5;
	}

	public int getScorePart6() {
		return scorePart6;
	}

	public void setScorePart6(int scorePart6) {
		this.scorePart6 = scorePart6;
	}

	public int getScorePart7() {
		return scorePart7;
	}

	public void setScorePart7(int scorePart7) {
		this.scorePart7 = scorePart7;
	}
	
	public void setScore(int[] score){
		if (score == null) return;
		scorePart1 = score[0];
		scorePart2 = score[1];
		scorePart3 = score[2];
		scorePart4 = score[3];
		scorePart5 = score[4];
		scorePart6 = score[5];
		scorePart7 = score[6];
	}
	
	public int[] getScore(){
		int[] score = {scorePart1, scorePart2, scorePart3, scorePart4, scorePart5, scorePart6, scorePart7};
		return score;
	}
	
	public int[] getListeningScore(){
		int[] score = {scorePart1, scorePart2, scorePart3, scorePart4};
		return score;
	}
	
	public int[] getReadingScore(){
		int[] score = {scorePart5, scorePart6, scorePart7};
		return score;
	}

	public int getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(int currentTime) {
		this.currentTime = currentTime;
	}

	public int getCurrentReadingTime() {
		return currentReadingTime;
	}

	public void setCurrentReadingTime(int currentReadingTime) {
		this.currentReadingTime = currentReadingTime;
	}

	public int getActivePart() {
		return activePart;
	}

	public void setActivePart(int activePart) {
		this.activePart = activePart;
	}

	public long getMediaCurrentTime() {
		return mediaCurrentTime;
	}

	public void setMediaCurrentTime(long mediaCurrentTime) {
		this.mediaCurrentTime = mediaCurrentTime;
	}

	public int getBookRowID() {
		return bookRowID;
	}

	public void setBookRowID(int bookRowID) {
		this.bookRowID = bookRowID;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getIsHavingImage() {
		return isHavingImage;
	}

	public void setIsHavingImage(String isHavingImage) {
		this.isHavingImage = isHavingImage;
	}

	@Override
	public String toString() {
		return "TestHistoryItem [_id=" + _id + ", bookID=" + bookID
				+ ", status=" + status + ", createdDate=" + createdDate
				+ ", modifiedDate=" + modifiedDate + ", filePath=" + filePath
				+ ", mode=" + mode + ", scorePart1=" + scorePart1
				+ ", scorePart2=" + scorePart2 + ", scorePart3=" + scorePart3
				+ ", scorePart4=" + scorePart4 + ", scorePart5=" + scorePart5
				+ ", scorePart6=" + scorePart6 + ", scorePart7=" + scorePart7
				+ ", currentTime=" + currentTime + ", activePart=" + activePart
				+ ", mediaCurrentTime=" + mediaCurrentTime + ", bookRowID="
				+ bookRowID + ", bookName=" + bookName + ", isHavingImage="
				+ isHavingImage + "]";
	}
	
}
