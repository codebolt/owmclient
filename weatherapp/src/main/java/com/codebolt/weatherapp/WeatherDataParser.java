package com.codebolt.weatherapp;

import org.apache.commons.lang3.StringUtils;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.ReadContext;

public class WeatherDataParser {
	private final ParseContext parseContext ;
	
	public WeatherDataParser() {
		final Configuration config = Configuration.defaultConfiguration()
				.addOptions(Option.SUPPRESS_EXCEPTIONS) ;
		this.parseContext = JsonPath.using(config) ;
	}
	
	static private Double readDouble(final ReadContext doc, final String path) {
		final Object obj = doc.read(path) ;
		try {
			return obj == null ? null : new Double(obj.toString()) ;
		} catch(Exception e) {
			System.out.println("Warning: Unable to parse element '" + path + "' value '" + obj + "' as decimal: " + e.toString());
			return null ;
		}
	}
	
	static private String readString(final ReadContext doc, final String path) {
		return StringUtils.trimToEmpty(doc.read(path)) ;
	}

	public WeatherData parse(String json) {
		final ReadContext doc = parseContext.parse(json) ;
		final WeatherData data = new WeatherData() ;
		data.setCity(readString(doc,"$.name"));
		data.setCountry(readString(doc,"$.sys.country"));
		data.setTitle(readString(doc,"$.weather[0].main"));
		data.setDescription(readString(doc,"$.weather[0].description"));
		data.setTemperature(readDouble(doc,"$.main.temp"));
		data.setPressure(readDouble(doc,"$.main.pressure"));
		data.setHumidity(readDouble(doc,"$.main.humidity"));
		data.setMinTemperature(readDouble(doc,"$.main.temp_min"));
		data.setMaxTemperature(readDouble(doc,"$.main.temp_max"));
		data.setSeaLevel(readDouble(doc,"$.main.sea_level"));
		data.setGroundLevel(readDouble(doc,"$.main.grnd_level"));
		data.setWindSpeed(readDouble(doc,"$.wind.speed"));
		data.setWindDegrees(readDouble(doc,"$.wind.deg"));
		data.setCloudiness(readDouble(doc,"$.clouds.all"));
		data.setRain3h(readDouble(doc,"$.rain.3h"));
		data.setSnow3h(readDouble(doc,"$.snow.3h"));
		return data;
	}
}
