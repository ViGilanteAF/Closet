package com.example.closet;

public class Item {
	String name;
	String category;
	String brand;
	String weather;
	String image;
	
	public Item(String _name, String _category, String _brand, 
									String _weather, String _image){
		name = _name;
		category = _category;
		brand = _brand;
		weather = _weather;
		image=_image;
	}
	

	public String getName(){return name;}
	public String getCategory(){return category;}
	public String getBrand(){return brand;}
	public String getWeather(){return weather;}
	public String getImage() {return image;}
}
