/***
  Copyright (c) 2009-11 CommonsWare, LLC
  
  Licensed under the Apache License, Version 2.0 (the "License"); you may
  not use this file except in compliance with the License. You may obtain
  a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package org.mcxiaoke.fancooker.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

abstract public class WakefulIntentService extends IntentService {
	abstract protected void doWakefulWork(Intent intent);

	static final String NAME = Constants.PACKAGE_NAME+".service.WakefulIntentService";
	private static volatile PowerManager.WakeLock lockStatic = null;

	synchronized private static PowerManager.WakeLock getLock(Context context) {
		if (lockStatic == null) {
			PowerManager mgr = (PowerManager) context
					.getSystemService(Context.POWER_SERVICE);

			lockStatic = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, NAME);
			lockStatic.setReferenceCounted(true);
		}
		return (lockStatic);
	}

	public static void sendWakefulWork(Context ctxt, Intent i) {
		getLock(ctxt.getApplicationContext()).acquire();
		ctxt.startService(i);
	}

	public static void sendWakefulWork(Context ctxt, Class<?> clsService) {
		sendWakefulWork(ctxt, new Intent(ctxt, clsService));
	}

	public WakefulIntentService(String name) {
		super(name);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		getLock(this.getApplicationContext()).acquire();
		super.onStartCommand(intent, flags, startId);
		return (START_NOT_STICKY);
	}

	@Override
	final protected void onHandleIntent(Intent intent) {
		try {
			doWakefulWork(intent);
		} finally {
			getLock(this.getApplicationContext()).release();
		}
	}
}
