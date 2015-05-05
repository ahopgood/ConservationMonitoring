package com.alexander.archive;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Reading {

	private Date time;
	private Double temperature;
	private Double humidity;
	private static final DateFormat format 
		= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public Reading(Date time, Double temperature, Double humidity){
		this.time = time;
		this.temperature = temperature;
		this.humidity = humidity;
	}
	
	public static DateFormat getDateFormat(){
		return format;
	}

	public Date getTime() {
		return time;
	}

	public Double getTemperature() {
		return temperature;
	}

	public Double getHumidity() {
		return humidity;
	}
	
	@Override
	public String toString(){
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
		.append(this.getTime())
		.append(this.getTemperature())
		.append(this.getHumidity())		
		.build();
	}
}
