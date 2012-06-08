/**
 ** Copyright (c) 2010 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 **
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.
 **
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 **
 **/
package com.ushahidi.android.app.services;

import android.content.Intent;
import android.util.Log;

import com.ushahidi.android.app.ImageManager;
import com.ushahidi.android.app.models.ListCheckinModel;
import com.ushahidi.android.app.models.ListReportModel;
import com.ushahidi.android.app.net.CategoriesHttpClient;
import com.ushahidi.android.app.net.CheckinHttpClient;
import com.ushahidi.android.app.net.ReportsHttpClient;
import com.ushahidi.android.app.util.ApiUtils;

/**
 * @author eyedol
 * 
 */
public class FetchReports extends SyncServices {
	
	private static String CLASS_TAG = FetchReports.class.getSimpleName();
	
	private Intent statusIntent; // holds the status of the sync and sends it to

	private int status = 113;

	public FetchReports() {
		super(CLASS_TAG);
		statusIntent = new Intent(SYNC_SERVICES_ACTION);
	}

	/**
	 * Clear saved reports
	 */
	public void clearCachedData() {
		// delete reports
		new ListReportModel().deleteReport();

		// delete checkins data
		new ListCheckinModel().deleteCheckin();

		// delete pending photos
		ImageManager.deleteImages(this);

		// delete fetched photos
		ImageManager.deletePendingImages(this);
	}

	@Override
	protected void executeTask(Intent intent) {

		Log.i(CLASS_TAG, "executeTask() executing this task");
		clearCachedData();
		if (!new ApiUtils(this).isCheckinEnabled()) {
			
			// fetch categories
			new CategoriesHttpClient(this).getCategoriesFromWeb();
			// fetch reports
			status = new ReportsHttpClient(this).getAllReportFromWeb();

		} else {

			// TODO process checkin if there is one
			status = new CheckinHttpClient(this).getAllCheckinFromWeb();
		}

		statusIntent.putExtra("status", status);
		sendBroadcast(statusIntent);

	}
}