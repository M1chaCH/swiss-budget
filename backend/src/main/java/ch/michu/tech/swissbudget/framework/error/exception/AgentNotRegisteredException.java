package ch.michu.tech.swissbudget.framework.error.exception;

import ch.michu.tech.swissbudget.app.dto.MfaCodeDto;
import jakarta.ws.rs.core.Response.Status;
import java.util.Map;

public class AgentNotRegisteredException extends AppException {

    public AgentNotRegisteredException(String username, String agent, MfaCodeDto dto) {
        super(String.format("user %s tries login on new agent %s", username, agent),
            Status.OK, Map.of("processId", dto.getProcessId().toString(), "userId", dto.getUserId().toString()));
    }
}
