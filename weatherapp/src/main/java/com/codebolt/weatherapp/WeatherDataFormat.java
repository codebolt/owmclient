package com.codebolt.weatherapp;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is responsible for the formatting of WeatherData objects.<br>
 * Specifically, this means converting all the values to strings and concatenating the strings together to make semicolon-separated rows.
 * @author Rune
 *
 */
public class WeatherDataFormat {
	/**
	 * Contains names for all the columns returned by formatColumns.
	 */
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
	
	/**
	 * @param wd WeatherData instance.
	 * @return A list of all the properties of <code>wd</code> formatted to strings for output. 
	 */
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
	
	/**
	 * @return Joins together the strings in <code>columns</code> to form a semicolon-separated row of values.
	 */
	private String formatRow(final List<String> columns) {
		return columns.stream().collect(Collectors.joining(SEPARATOR)) ;
	}
	
	/**
	 * @return Semicolon-separated list of column names (to use as a header).
	 */
	public String formatHeader() {
		return this.formatRow(COLUMN_NAMES) ;
	}
	
	/**
	 * @param wd
	 * @return Semicolon-separated list of the properties in <code>wd</code>.
	 */
	public String formatData(WeatherData wd) {
		return this.formatRow(this.formatColumns(wd)) ;
	}
}
