package org.evasoft.settings.utils;

import java.util.Comparator;

import org.evasoft.settings.AppItem;

public class SizeComparator implements Comparator<AppItem> {

	@Override
	public int compare(AppItem one, AppItem two) {
		long _one = one.lsize;
		long _two = two.lsize;
		if (_one > _two) {
			return -1;
		} else return 1;
	}
}
