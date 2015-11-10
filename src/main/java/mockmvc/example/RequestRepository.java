package mockmvc.example;

import org.springframework.stereotype.Repository;

@Repository
public class RequestRepository {
	public RequestComment findByUUID(String uuid) {
		return null;
	}
}
