package mockmvc.example;

import org.springframework.stereotype.Repository;

import global.Log;

@Repository
public class RequestRepository {
	public RequestRepository() {
		Log.append("RR ");
	}

	public RequestComment findByUUID(String uuid) {
		return null;
	}
}
