package com.avengers.publicim.data.third_party;

import java.util.List;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by D-IT-MAX2 on 2017/1/4.
 */

public interface ThirdPartyService {
	@GET("thirdAPIServerM-0.1/App/Service")
	Observable<List<AppInfo>> appList();
}
