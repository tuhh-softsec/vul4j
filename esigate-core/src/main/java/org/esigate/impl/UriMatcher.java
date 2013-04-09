package org.esigate.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.esigate.Driver;

public class UriMatcher {

	public Driver lookup(String uri) {
		return null;
	}

	public static void main(String[] args) throws Exception {
		Pattern pattern = Pattern.compile("((http://|https://)[^/:]*(:([0-9]*)))?(/[^*]*)(\\*?)([^*]*)");
		Matcher matcher = pattern.matcher("http://foo.com:80/a*.jsp");
		if (!matcher.matches())
			throw new Exception("Does not match!");
		System.out.println(matcher.group(1));
		System.out.println(matcher.group(5));
		System.out.println(matcher.group(7));
	}
}
