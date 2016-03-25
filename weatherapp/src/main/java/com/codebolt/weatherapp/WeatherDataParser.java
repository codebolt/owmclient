package com.codebolt.weatherapp;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.ReadContext;

/**
 * This class provides functionality for parsing the JSON returned from OpenWeatherMap into a WeatherData object. 
 * @author Rune
 *
 */
public class WeatherDataParser {
	private final ParseContext parseContext ;
	
	public WeatherDataParser() {
		final Configuration config = Configuration.defaultConfiguration()
				.addOptions(Option.SUPPRESS_EXCEPTIONS) ;
		this.parseContext = JsonPath.using(config) ;
	}
	
	/**
	 * Reads out the given path from the provided document read context, and converts it to a Double. 
	 * If the object could not be converted to a Double, null is returned.
	 */
	static private Double readDouble(final ReadContext doc, final String path) {
		final Object obj = doc.read(path) ;
		try {
			if (obj == null) return null ;
			else if (obj instanceof Double) return (Double) obj ;
			else return new Double(obj.toString()) ;
		} catch(Exception e) {
			System.err.println("Warning: Unable to parse element '" + path + "' value '" + obj + "' as decimal: " + e.toString());
			return null ;
		}
	}

	/**
	 * Parses a JSON string returned from the OpenWeatherMap web service into a WeatherData instance, using JSONPath.
	 * @param json
	 * @return
	 */
	public WeatherData parse(String json) {
		final ReadContext doc = parseContext.parse(json) ;
		final WeatherData data = new WeatherData() ;
		data.setCity(doc.read("$.name"));
		data.setCountry(doc.read("$.sys.country"));
		data.setTitle(doc.read("$.weather[0].main"));
		data.setDescription(doc.read("$.weather[0].description"));
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
