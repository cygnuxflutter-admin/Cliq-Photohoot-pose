package com.photo.pose.photoshoot.cliq.PCliq_items;

import com.google.gson.annotations.SerializedName;

public class PCliq_ItemColors {

	@SerializedName("color_id")
	String id;
	@SerializedName("color_name")
	String colorName;
	@SerializedName("color_code")
	String colorHex;

	public PCliq_ItemColors(String id, String colorName, String colorHex) {
		this.id = id;
		this.colorName = colorName;
		this.colorHex = colorHex;
	}

	public String getId() {
		return id;
	}

	public String getColorName() {
		return colorName;
	}

	public String getColorHex() {
		return colorHex;
	}
}