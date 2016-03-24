package com.codebolt.weatherapp;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WeatherDataFormat {
	static public final List<String> COLUMN_NAMES = 
			Arrays.asList(WeatherData.class.getDeclaredFields()).stream().map(f -> f.getName()).collect(Collectors.toList()) ;
	
	static private final String SEPARATOR = ";" ;
	
	static private String formatNumber(Double value) {
		return value == null ? "" : 
			DecimalFormat.getNumberInstance().format(value.doubleValue()) ;
	}
		
	static private String formatString(String value) {
		return value == null ? "" : value.replace(SEPARATOR, "") ;
	}
	
	private List<String> formatColumns(final WeatherData wd) {
		return Arrays.asList(
			formatString(wd.getCity()),
			formatString(wd.getCountry()),
			formatString(wd.getTitle()),
			formatString(wd.getDescription()),
			formatNumber(wd.getTemperature()),
			formatNumber(wd.getPressure()),
			formatNumber(wd.getHumidity()),
			formatNumber(wd.getMinTemperature()),
			formatNumber(wd.getMaxTemperature()),
			formatNumber(wd.getSeaLevel()),
			formatNumber(wd.getGroundLevel()),
			formatNumber(wd.getWindSpeed()),
			formatNumber(wd.getWindDegrees()),
			formatNumber(wd.getRain3h()),
			formatNumber(wd.getSnow3h()),
			formatNumber(wd.getCloudiness())) ;
	}
	
	private String formatRow(final List<String> columns) {
		return columns.stream().collect(Collectors.joining(SEPARATOR)) ;
	}
	
	public String formatHeader() {
		return this.formatRow(COLUMN_NAMES) ;
	}
	
	public String formatData(WeatherData wd) {
		return this.formatRow(this.formatColumns(wd)) ;
	}
}
