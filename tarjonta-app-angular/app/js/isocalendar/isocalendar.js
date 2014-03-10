'use strict';

/**
 * ISO8601 calendar.
 * 
 * new ISOCalendar()
 * new ISOCalendar(date)
 * new ISOCalendar(year)
 * new ISOCalendar(year, month)
 * new ISOCalendar(year, month, day)
 * 
 * @param year Full year.
 * @param month Month as (0..11).
 * @param day Day of the month (1..31).
 */
function ISOCalendar(year, month, day) {

	if (year==undefined) {
		year = new Date();
	} else {
		if (month==undefined) {
			month = 0;
		}
		if (day==undefined) {
			day=1;
		}
	}
	
	var d = year instanceof Date ? year : new Date(year, month, day, 0,0,0,0);
	
	this.year = d.getFullYear();
	this.month = d.getMonth();
	this.day = d.getDate();
	this.weekday = (d.getDay()+6) % 7 + 1;

	this.isoYear = this.year;
	
	function gregdaynumber(year, month, day) {
		// computes the day number since 0 January 0 CE (Gregorian)
		if (month < 3) {
			year = year - 1;
			month = month + 12;
		}
		return Math.floor(365.25 * year)
			- Math.floor(year / 100)
			+ Math.floor(year / 400)
			+ Math.floor(30.6 * (month + 1))
			+ day - 62;
	}


	var d0 = gregdaynumber(this.year, 1, 0);
	var weekday0 = ((d0 + 4) % 7) + 1;

	var d = gregdaynumber(this.year, this.month + 1, this.day);
	this.isoWeek = Math.floor((d - d0 + weekday0 + 6) / 7) - Math.floor((weekday0 + 3) / 7);

	// check whether the last few days of December belong to the next year's ISO week
	if ((this.month == 11) && ((this.day - this.weekday) > 27)) {
		this.isoWeek = 1;
		this.isoYear++;
	}


	// check whether the first few days of January belong to the previous year's ISO week
	if ((this.month == 0) && ((this.weekday - this.day) > 3)) {
		d0 = gregdaynumber(this.year - 1, 1, 0);
		weekday0 = ((d0 + 4) % 7) + 1;
		this.isoWeek = Math.floor((d - d0 + weekday0 + 6) / 7)
				- Math.floor((weekday0 + 3) / 7);
		this.isoYear--;
	}
	
	/**
	 * Javascript date.
	 */
	this.toDate = function() {
		return new Date(this.year,this.month,this.day,0,0,0,0);
	}

	/**
	 * Full year.
	 */
	this.getYear = function() {
		return this.year;
	}

	/**
	 * Month (0..11).
	 */
	this.getMonth = function() {
		return this.month;
	}

	/**
	 * Day (1..31).
	 */
	this.getDay = function() {
		return this.day;
	}
	
	/**
	 * ISO week number (1..53).
	 */
	this.getIsoWeek = function() {
		return this.isoWeek;
	}
	
	/**
	 * Full ISO year.
	 */
	this.getIsoYear = function() {
		return this.isoYear;
	}
	
	/**
	 * ISO weekday (1..7).
	 */
	this.getWeekDay = function() {
		return this.weekday;
	}
	
	/**
	 * First day of the week.
	 */
	this.getMonday = function() {
		return new ISOCalendar(this.year, this.month, this.day - this.weekday + 1);
	}

	/**
	 * Last day of the week.
	 */
	this.getFriday = function() {
		return new ISOCalendar(this.year, this.month, this.day - this.weekday + 7);
	}

	this.getYesterday = function() {
		return new ISOCalendar(this.year, this.month, this.day - 1);
	}

	this.getTomorrow = function() {
		return new ISOCalendar(this.year, this.month, this.day + 1);
	}
	
	this.getFirstDayOfMonth = function() {
		return new ISOCalendar(this.year, this.month, 1);
	}
	
	this.getLastDayOfMonth = function() {
		return new ISOCalendar(this.year, this.month+1, 0);
	}
	
	this.getNextWeek = function() {
		return new ISOCalendar(this.year, this.month, this.day + 7);
	}
	
	this.compareTo = function(cal) {
		return this.toDate().getTime() - cal.toDate().getTime();
	}
	
}

