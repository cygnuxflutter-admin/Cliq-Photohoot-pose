package com.photo.pose.photoshoot.cliq.PCliq_items;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PCliq_ItemPage implements Serializable{

	@SerializedName("page_id")
	String id;

	@SerializedName("page_title")
	String title;

	@SerializedName("page_content")
	String content;

	public PCliq_ItemPage(String id, String title, String content) {
		this.id = id;
		this.title = title;
		this.content = content;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}
}
