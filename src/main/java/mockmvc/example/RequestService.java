package mockmvc.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestService {
	@Autowired
	private RequestRepository repository;

	public RequestComment getRequestCommentByUUID(String uuid) {
		return repository.findByUUID(uuid);
	}
}
