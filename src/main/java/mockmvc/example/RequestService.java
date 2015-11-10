package mockmvc.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import global.Log;

@Service
public class RequestService {
	@Autowired
	private RequestRepository repository;

	public RequestService() {
		Log.append("RS ");
	}

	public RequestComment getRequestCommentByUUID(String uuid) {
		return repository.findByUUID(uuid);
	}
}
