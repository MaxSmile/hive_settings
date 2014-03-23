package org.evasoft.settings.utils;

import java.util.Comparator;

import org.evasoft.settings.AppItem;

public class NameComparator implements Comparator<AppItem> {

	@Override
	public int compare(AppItem one, AppItem two) {
		return one.name.compareTo(two.name);
	}
}
