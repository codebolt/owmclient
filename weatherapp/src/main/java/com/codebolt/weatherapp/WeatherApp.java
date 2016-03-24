package com.codebolt.weatherapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

public class WeatherApp {
	enum ThreadMode { SINGLE_THREAD, MULTI_THREAD } ;
	enum SortOrder { ASCENDING, DESCENDING } ;

	private static final String APPID = "baa5e6a85fc665e1bce9791f4e2313d7" ;
	private final ThreadMode threadMode ;
	private final String sortColumn ;
	private final SortOrder sortOrder ;
	private final Class<?> sortType ;
	private final WebTarget weatherDataEndpoint ;
	private final WeatherDataParser weatherDataParser ;
	private final WeatherDataFormat weatherDataFormat ;
	private final boolean debug ;

	public static void main(String[] args) throws Exception {
		final Options options = new Options();
		options.addOption("help", "show this message");
		options.addOption("debug", "display debug output") ;
		options.addOption("st", "run single-threaded");
		final String columnValues = WeatherDataFormat.COLUMN_NAMES.stream().collect(Collectors.joining("|")) ;
		options.addOption("sort", true, "sort by field (" + columnValues + ")") ;
		options.addOption("desc", "sort descending") ;
		final CommandLineParser parser = new DefaultParser() ;
		final CommandLine commandLine = parser.parse(options, args) ;
		if(commandLine.hasOption("help")) {
			new HelpFormatter().printHelp("weatherapp", options);
		} else {
			System.out.println("Run with -help to see options.");
			final ThreadMode threadMode;
			if(commandLine.hasOption("st")) {
				threadMode = ThreadMode.SINGLE_THREAD ;
			} else {
				threadMode = ThreadMode.MULTI_THREAD ;
			}
			final String sortColumn;
			if(commandLine.hasOption("sort")) {
				sortColumn = commandLine.getOptionValue("sort") ;
			} else {
				sortColumn = WeatherDataFormat.COLUMN_NAMES.get(0) ;
			}
			final SortOrder sortOrder ;
			if(commandLine.hasOption("desc")) {
				sortOrder = SortOrder.DESCENDING ;
			} else {
				sortOrder = SortOrder.ASCENDING ;
			}
			final boolean debug = commandLine.hasOption("debug") ;

			// validate sort column
			if(!WeatherDataFormat.COLUMN_NAMES.contains(sortColumn)) {
				System.err.println("'" + sortColumn + "' is not a valid column name. Run with -help for more information.");
			}
			else new WeatherApp(threadMode, sortColumn, sortOrder, debug).runWithConsoleInput(); 
		}
	}

	public WeatherApp(ThreadMode threadMode, String sortColumn, SortOrder sortOrder, boolean debug) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		this.weatherDataEndpoint = ClientBuilder.newClient()
				.target("http://api.openweathermap.org").path("data/2.5/weather").queryParam("APPID", APPID) ;		
		this.weatherDataParser = new WeatherDataParser() ;
		this.weatherDataFormat = new WeatherDataFormat() ;
		this.threadMode = threadMode ;
		this.sortColumn = sortColumn ;
		this.sortOrder = sortOrder ;
		this.sortType = PropertyUtils.getPropertyType(new WeatherData(), sortColumn) ;
		this.debug = debug ;
	}
	
	public void runWithConsoleInput() throws Exception {
		final List<String> cityList = this.readCitiesFromConsole() ;
		if(cityList.isEmpty()) {
			System.out.println("No cities entered, exiting.");
		} else {
			System.out.println("Fetching weather data for " + cityList.size() + " cities.");
			final Stream<String> stream ;
			if(threadMode == ThreadMode.SINGLE_THREAD) {
				stream = cityList.stream() ;
			} else { // MULTI_THREAD
				stream = cityList.parallelStream() ;
			}
			List<WeatherData> dataList = stream.map(this::fetchWeatherData).collect(Collectors.toList()) ;
			dataList = this.sortList(dataList) ;
			this.printTable(dataList);
		}
	}
	
	public List<String> readCitiesFromConsole() throws IOException {
		final List<String> cityList = new ArrayList<>() ;
		System.out.println("Enter cities (one per line, blank line to end): ");
		final BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in)) ;
		while(true) {
			final String city = consoleReader.readLine() ;
			if(StringUtils.isNotBlank(city)) {
				cityList.add(city) ;
			}
			else break ;
		}
		consoleReader.close() ;
		return cityList ;
	}
	
	public WeatherData fetchWeatherData(String city) {
		WeatherData result ;
		try {
			if(debug) {
				System.out.println("Retrieving weather data for city '" + city + "'.");
			}
			final Response response = this.weatherDataEndpoint.queryParam("q", city).request().get() ;
			if(response.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
				throw new Exception("Request unsuccessful. Response was HTTP " + 
						StringUtils.trim(response.getStatusInfo().getStatusCode() + " " + response.getStatusInfo().getReasonPhrase())
						+ ".") ;
			}
			final String jsonData = response.readEntity(String.class) ;
			if(debug) {
				System.out.println("JSON retrieved: " + jsonData);
			}
			result = this.weatherDataParser.parse(jsonData) ;
			if(debug) {
				System.out.println("Weather data for city '" + city + "' successfully retrieved.");
			}
		} catch(Exception e) {
			System.err.println("Unable to retrieve weather data for city '" + city + "': " + e.toString());
			if(debug) {
				e.printStackTrace();
			}
			result = new WeatherData() ;
			result.setCity(city) ;
		}
		if(threadMode == ThreadMode.SINGLE_THREAD) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) { }
		}
		return result ;
	}
	
	public List<WeatherData> sortList(final List<WeatherData> dataList) throws Exception {		
		return dataList.stream().sorted(new Comparator<WeatherData>() {
			@Override
			public int compare(WeatherData wd1, WeatherData wd2) {
				try {
					final int result ;
					if(sortType.equals(Double.class)) {
						final Double o1 = (Double) PropertyUtils.getProperty(wd1, sortColumn) ;
						final Double o2 = (Double) PropertyUtils.getProperty(wd2, sortColumn) ;
						result = ObjectUtils.compare((Double)o1, (Double)o2) ;
					} else {
						final String o1 = BeanUtils.getProperty(wd1, sortColumn) ;
						final String o2 = BeanUtils.getProperty(wd2, sortColumn) ;
						result = ObjectUtils.compare(o1, o2) ;
					}
					return sortOrder == SortOrder.ASCENDING ? result : 0-result ;
				} catch (Exception e) {
					return 0;
				}
			}
		}).collect(Collectors.toList()) ;
	}
	
	public void printTable(List<WeatherData> dataList) {
		// print header
		System.out.println(weatherDataFormat.formatHeader());
		// print rows
		for(WeatherData data : dataList) {
			System.out.println(weatherDataFormat.formatData(data));
		}
	}
}
