package ch.michu.tech.swissbudget.framework.authentication;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SessionToken {

    protected Date issuedAt;
    protected Date expiresAt;
    protected String userId;
    protected String userAgent;
    protected String remoteAddress;
    protected boolean stay;
    protected String sessionId;
}
